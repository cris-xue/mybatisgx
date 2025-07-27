package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.LoadStrategy;
import com.lc.mybatisx.model.*;
import org.apache.commons.lang3.ObjectUtils;
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
        ResultMapInfo resultMapInfo = mapperInfo.getResultMapInfo(resultClass);
        if (resultMapInfo != null) {
            return resultMapInfo.getId();
        }

        EntityRelationInfo entityRelationInfo = mapperInfo.getEntityRelationInfo(resultClass);
        List<ResultMapInfo> resultMapInfoList = new ArrayList();
        mapperInfo.addResultMapInfoList(resultMapInfoList);
        ResultMapInfo rootResultMapInfo = this.buildResultMapInfo(resultMapInfoList, entityRelationInfo);
        return rootResultMapInfo.getId();
    }

    private ResultMapInfo buildResultMapInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationInfo entityRelationInfo) {
        EntityInfo entityInfo = entityRelationInfo.getEntityInfo();
        Class<?> targetEntityClass = entityInfo.getTableEntityClass();
        ResultMapInfo resultMapInfo = new ResultMapInfo();
        resultMapInfo.setId(this.getResultMapId(targetEntityClass));
        resultMapInfo.setType(targetEntityClass);
        resultMapInfo.setColumnInfoList(entityInfo.getTableColumnInfoList());
        resultMapInfo.setResultMapAssociationInfoList(this.getResultMapAssociationInfoList(resultMapInfoList, entityRelationInfo));

        resultMapInfoList.add(resultMapInfo);
        return resultMapInfo;
    }

    private List<ResultMapAssociationInfo> getResultMapAssociationInfoList(List<ResultMapInfo> resultMapInfoList, EntityRelationInfo entityRelationInfo) {
        int level = entityRelationInfo.getLevel();
        List<EntityRelationInfo> entityRelationInfoList = entityRelationInfo.getEntityRelationList();
        List<ResultMapAssociationInfo> resultMapAssociationInfoList = this.buildResultMapAssociationInfo(resultMapInfoList, entityRelationInfoList);
        return resultMapAssociationInfoList;
    }

    private List<ResultMapAssociationInfo> buildResultMapAssociationInfo(List<ResultMapInfo> resultMapInfoList, List<EntityRelationInfo> entityRelationInfoList) {
        List<ResultMapAssociationInfo> resultMapAssociationInfoList = new ArrayList();
        for (EntityRelationInfo entityRelationInfo : entityRelationInfoList) {
            ColumnInfo columnInfo = entityRelationInfo.getColumnInfo();
            EntityInfo entityInfo = entityRelationInfo.getEntityInfo();

            ResultMapAssociationInfo resultMapAssociationInfo = new ResultMapAssociationInfo();
            resultMapAssociationInfo.setSelect(this.getSelect(columnInfo.getJavaType(), columnInfo.getCollectionType()));
            resultMapAssociationInfo.setResultMapId(this.getSubQueryResultMapId(columnInfo.getJavaType(), columnInfo));
            resultMapAssociationInfo.setType(columnInfo.getJavaType());
            resultMapAssociationInfo.setColumnInfoList(entityInfo.getTableColumnInfoList());
            resultMapAssociationInfo.setColumnInfo(columnInfo);

            LoadStrategy loadStrategy = columnInfo.getColumnInfoAnnotationInfo().getLoadStrategy();
            if (loadStrategy == LoadStrategy.SUB) {
                this.buildResultMapInfo(resultMapInfoList, entityRelationInfo);
            } else if (loadStrategy == LoadStrategy.JOIN) {
                List<EntityRelationInfo> subEntityRelationInfoList = entityRelationInfo.getEntityRelationList();
                if (ObjectUtils.isNotEmpty(subEntityRelationInfoList)) {
                    List<ResultMapAssociationInfo> subResultMapAssociationInfoList = this.buildResultMapAssociationInfo(resultMapInfoList, subEntityRelationInfoList);
                    resultMapAssociationInfo.setResultMapAssociationInfoList(subResultMapAssociationInfoList);
                }
            } else {
                throw new RuntimeException("未知的加载策略");
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
