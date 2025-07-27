package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultMapInfoHandler extends BasicInfoHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultMapInfoHandler.class);

    private static ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();

    public String execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        String action = methodInfo.getAction();
        if (Arrays.asList("insert", "delete", "update").contains(action)) {
            return null;
        }

        MethodReturnInfo methodReturnInfo = methodInfo.getMethodReturnInfo();
        Class<?> resultClass = methodReturnInfo.getType();
        EntityInfo entityInfo = EntityInfoContextHolder.get(resultClass);

        List<ColumnInfo> tableColumnInfoList;
        List<ColumnInfo> relationColumnInfoList = null;
        if (entityInfo != null) {
            tableColumnInfoList = entityInfo.getTableColumnInfoList();
            relationColumnInfoList = entityInfo.getRelationColumnInfoList();
        } else {
            tableColumnInfoList = columnInfoHandler.getColumnInfoList(resultClass);
        }

        ResultMapInfo resultMapInfo = mapperInfo.getResultMapInfo(resultClass);
        if (resultMapInfo != null) {
            return resultMapInfo.getId();
        }
        // 解决循环引用问题
        ResultMapDependencyTree resultMapDependencyTree = new ResultMapDependencyTree(null, resultClass);
        List<ResultMapAssociationInfo> resultMapAssociationInfoList = this.processRelationColumnInfoList(1, resultMapDependencyTree, relationColumnInfoList);
        resultMapInfo = new ResultMapInfo();
        resultMapInfo.setId(this.getResultMapId(resultClass));
        resultMapInfo.setType(resultClass);
        resultMapInfo.setColumnInfoList(tableColumnInfoList);
        resultMapInfo.setResultMapAssociationInfoList(resultMapAssociationInfoList);
        mapperInfo.addResultMapInfo(resultMapInfo);
        return resultMapInfo.getId();
    }

    private List<ResultMapAssociationInfo> processRelationColumnInfoList(int level, ResultMapDependencyTree resultMapDependencyTree, List<ColumnInfo> associationColumnInfoList) {
        List<ResultMapAssociationInfo> resultMapAssociationInfoList = new ArrayList();
        for (ColumnInfo associationColumnInfo : associationColumnInfoList) {
            Class<?> javaType = associationColumnInfo.getJavaType();
            Boolean isCycleRef = resultMapDependencyTree.cycleRefCheck(javaType);
            if (isCycleRef) {
                String pathString = StringUtils.join(resultMapDependencyTree.getPath(), "->");
                LOGGER.info("{}->{}存在循环引用，消除循环引用防止无限循环", pathString, javaType);
                continue;
            }
            ResultMapDependencyTree childrenResultMapDependencyTree = new ResultMapDependencyTree(resultMapDependencyTree, javaType);
            EntityInfo entityInfo = EntityInfoContextHolder.get(javaType);

            ResultMapAssociationInfo resultMapAssociationInfo = new ResultMapAssociationInfo();
            resultMapAssociationInfo.setSelect(this.getSelect(javaType, associationColumnInfo.getCollectionType()));
            resultMapAssociationInfo.setResultMapId(this.getSubQueryResultMapId(javaType, associationColumnInfo));
            resultMapAssociationInfo.setLevel(level);
            resultMapAssociationInfo.setType(javaType);
            resultMapAssociationInfo.setColumnInfoList(entityInfo.getTableColumnInfoList());
            resultMapAssociationInfo.setColumnInfo(associationColumnInfo);

            List<ColumnInfo> subRelationColumnInfoList = entityInfo.getRelationColumnInfoList();
            if (ObjectUtils.isNotEmpty(subRelationColumnInfoList)) {
                List<ResultMapAssociationInfo> subResultMapAssociationInfoList = this.processRelationColumnInfoList(level + 1, childrenResultMapDependencyTree, subRelationColumnInfoList);
                resultMapAssociationInfo.setResultMapAssociationInfoList(subResultMapAssociationInfoList);
            }
            resultMapAssociationInfoList.add(resultMapAssociationInfo);
        }
        return resultMapAssociationInfoList;
    }

    protected String getResultMapId(Class<?> entityClass) {
        return String.format("%sResultMap", entityClass.getTypeName().replaceAll("\\.", "_"));
    }

    protected String getSubQueryResultMapId(Class<?> entityClass, ColumnInfo columnInfo) {
        Class<?> collectionType = columnInfo.getCollectionType();
        String resultMapId;
        if (collectionType == null) {
            resultMapId = String.format("%sResultMap%s", entityClass.getTypeName().replaceAll("\\.", "_"), "Association");
        } else {
            resultMapId = String.format("%sResultMap%s", entityClass.getTypeName().replaceAll("\\.", "_"), "Collection");
        }
        return resultMapId;
    }

    private String getSelect(Class<?> entityClass, Class<?> containerType) {
        return String.format("find%s%s", entityClass.getSimpleName(), containerType != null ? containerType.getSimpleName() : "");
    }
}
