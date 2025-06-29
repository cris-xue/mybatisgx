package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomUtils;
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
        String resultMapId = getResultMapId(resultClass);
        EntityInfo entityInfo = EntityInfoContextHolder.get(resultClass);

        List<ColumnInfo> tableColumnInfoList;
        List<ColumnInfo> associationColumnInfoList = null;
        if (entityInfo != null) {
            tableColumnInfoList = entityInfo.getTableColumnInfoList();
            associationColumnInfoList = entityInfo.getAssociationColumnInfoList();
        } else {
            tableColumnInfoList = columnInfoHandler.getColumnInfoList(resultClass);
        }

        ResultMapInfo resultMapInfo = mapperInfo.getResultMapInfo(resultClass);
        if (resultMapInfo != null) {
            return resultMapInfo.getId();
        }
        // 解决循环引用问题
        ResultMapDependencyTree resultMapDependencyTree = new ResultMapDependencyTree(null, resultClass);
        List<ResultMapAssociationInfo> resultMapAssociationInfoList = this.processAssociationColumnInfoList(resultMapDependencyTree, associationColumnInfoList);
        resultMapInfo = new ResultMapInfo();
        resultMapInfo.setId(resultMapId);
        resultMapInfo.setType(resultClass);
        resultMapInfo.setColumnInfoList(tableColumnInfoList);
        resultMapInfo.setResultMapAssociationInfoList(resultMapAssociationInfoList);
        mapperInfo.setResultMapInfo(resultMapInfo);
        return resultMapInfo.getId();
    }

    private List<ResultMapAssociationInfo> processAssociationColumnInfoList(ResultMapDependencyTree resultMapDependencyTree, List<ColumnInfo> associationColumnInfoList) {
        List<ResultMapAssociationInfo> resultMapAssociationInfoList = new ArrayList();
        associationColumnInfoList.forEach(associationColumnInfo -> {
            Class<?> javaType = associationColumnInfo.getJavaType();
            Boolean isCycleRef = resultMapDependencyTree.cycleRefCheck(javaType);
            if (isCycleRef) {
                LOGGER.debug("{}存在循环引用，消除循环引用防止无线循环", javaType);
                return;
            }
            ResultMapDependencyTree childrenResultMapDependencyTree = new ResultMapDependencyTree(resultMapDependencyTree, javaType);
            EntityInfo entityInfo = EntityInfoContextHolder.get(javaType);

            ResultMapAssociationInfo resultMapAssociationInfo = new ResultMapAssociationInfo();
            resultMapAssociationInfo.setSelect(this.getSelect(javaType, associationColumnInfo.getContainerType()));
            resultMapAssociationInfo.setResultMapId(this.getResultMapId(javaType));
            resultMapAssociationInfo.setType(javaType);
            resultMapAssociationInfo.setColumnInfoList(entityInfo.getTableColumnInfoList());
            resultMapAssociationInfo.setColumnInfo(associationColumnInfo);

            List<ColumnInfo> subAssociationColumnInfoList = entityInfo.getAssociationColumnInfoList();
            if (ObjectUtils.isNotEmpty(subAssociationColumnInfoList)) {
                List<ResultMapAssociationInfo> subResultMapAssociationInfoList = this.processAssociationColumnInfoList(childrenResultMapDependencyTree, subAssociationColumnInfoList);
                resultMapAssociationInfo.setResultMapAssociationInfoList(subResultMapAssociationInfoList);
            }
            resultMapAssociationInfoList.add(resultMapAssociationInfo);
        });
        return resultMapAssociationInfoList;
    }

    protected String getResultMapId(Class<?> entityClass) {
        Integer resultMapIdIndex = RandomUtils.nextInt(10000, 99999);
        return String.format("%sResultMap%s", entityClass.getTypeName().replaceAll("\\.", "_"), resultMapIdIndex);
    }

    private String getSelect(Class<?> entityClass, Class<?> containerType) {
        return String.format("find%s%s", entityClass.getSimpleName(), containerType != null ? containerType.getSimpleName() : "");
    }
}
