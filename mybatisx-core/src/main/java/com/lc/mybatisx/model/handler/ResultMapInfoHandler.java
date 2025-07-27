package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.LoadStrategy;
import com.lc.mybatisx.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

        List<ResultMapInfo> resultMapInfoList = this.buildResultMapInfo(entityRelationInfo);
        mapperInfo.addResultMapInfo(resultMapInfo);
        return resultMapInfo.getId();
    }

    private void processLoadStrategy(EntityRelationInfo entityRelationInfo) {
        ColumnInfo columnInfo = entityRelationInfo.getColumnInfo();
        LoadStrategy loadStrategy = columnInfo.getColumnInfoAnnotationInfo().getLoadStrategy();
        if (loadStrategy == LoadStrategy.SUB) {
            this.buildResultMapInfo(entityRelationInfo);
        } else if (loadStrategy == LoadStrategy.JOIN) {

        } else {
            throw new RuntimeException("未知的加载策略");
        }
    }

    private List<ResultMapInfo> buildResultMapInfo(EntityRelationInfo entityRelationInfo) {
        int level = entityRelationInfo.getLevel();
        ColumnInfo columnInfo = entityRelationInfo.getColumnInfo();
        EntityInfo entityInfo = entityRelationInfo.getEntityInfo();
        List<EntityRelationInfo> entityRelationInfoList = entityRelationInfo.getEntityRelationList();
        Class<?> targetEntityClass = entityInfo.getTableEntityClass();

        Class<?> collectionType = columnInfo.getCollectionType();
        List<ResultMapAssociationInfo> resultMapAssociationInfoList;
        if (collectionType == Collection.class) {
            resultMapAssociationInfoList = this.buildResultMapAssociationInfo(entityRelationInfoList);
        } else {
            resultMapAssociationInfoList = this.buildResultMapAssociationInfo(entityRelationInfoList);
        }

        ResultMapInfo resultMapInfo = new ResultMapInfo();
        resultMapInfo.setId(this.getResultMapId(targetEntityClass));
        resultMapInfo.setType(targetEntityClass);
        resultMapInfo.setColumnInfoList(entityInfo.getTableColumnInfoList());
        resultMapInfo.setResultMapAssociationInfoList(resultMapAssociationInfoList);

        return null;
    }

    private List<ResultMapAssociationInfo> buildResultMapAssociationInfo(List<EntityRelationInfo> entityRelationInfoList) {
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

            List<EntityRelationInfo> subEntityRelationInfoList = entityRelationInfo.getEntityRelationList();
            if (ObjectUtils.isNotEmpty(subEntityRelationInfoList)) {
                List<ResultMapAssociationInfo> subResultMapAssociationInfoList = this.buildResultMapAssociationInfo(subEntityRelationInfoList);
                resultMapAssociationInfo.setResultMapAssociationInfoList(subResultMapAssociationInfoList);
                resultMapAssociationInfoList.add(resultMapAssociationInfo);
            }
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
