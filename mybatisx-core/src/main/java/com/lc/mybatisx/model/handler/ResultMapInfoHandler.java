package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.LoadStrategy;
import com.lc.mybatisx.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultMapInfoHandler extends BasicInfoHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultMapInfoHandler.class);

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

        List<ResultMapInfo> resultMapInfoList = new ArrayList();
        List<ResultMapAssociationInfo> entityRelationQueryMethodList = new ArrayList();
        EntityRelationInfo entityRelationInfo = mapperInfo.getEntityRelationInfo(resultClass);
        ResultMapInfo rootResultMapInfo = this.buildResultMapInfo(resultMapInfoList, entityRelationInfo);
        this.buildEntityRelationQueryMethod(entityRelationQueryMethodList, entityRelationInfo, null);

        mapperInfo.addResultMapInfoList(resultMapInfoList);
        mapperInfo.setEntityRelationQueryMethodList(entityRelationQueryMethodList);
        return rootResultMapInfo.getId();
    }

    private ResultMapInfo buildResultMapInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationInfo entityRelationInfo) {
        List<ResultMapAssociationInfo> resultMapAssociationInfoList = this.buildResultMapRelationInfo(
                resultMapInfoList,
                entityRelationInfo.getEntityRelationList()
        );
        EntityInfo entityInfo = entityRelationInfo.getEntityInfo();
        Class<?> targetEntityClass = entityInfo.getTableEntityClass();
        ResultMapInfo resultMapInfo = new ResultMapInfo();
        resultMapInfo.setId(this.getResultMapId(targetEntityClass));
        resultMapInfo.setType(targetEntityClass);
        resultMapInfo.setColumnInfoList(entityInfo.getTableColumnInfoList());
        resultMapInfo.setResultMapAssociationInfoList(resultMapAssociationInfoList);
        resultMapInfoList.add(resultMapInfo);
        return resultMapInfo;
    }

    /**
     * 构建结果集关联信息
     *
     * @param resultMapInfoList              结果集信息列表
     * @param childrenEntityRelationInfoList
     * @return
     */
    private List<ResultMapAssociationInfo> buildResultMapRelationInfo(List<ResultMapInfo> resultMapInfoList, List<EntityRelationInfo> childrenEntityRelationInfoList) {
        List<ResultMapAssociationInfo> resultMapAssociationInfoList = new ArrayList();
        for (EntityRelationInfo childrenEntityRelationInfo : childrenEntityRelationInfoList) {
            int level = childrenEntityRelationInfo.getLevel();
            ColumnInfo columnInfo = childrenEntityRelationInfo.getColumnInfo();
            EntityInfo entityInfo = childrenEntityRelationInfo.getEntityInfo();

            ResultMapAssociationInfo resultMapAssociationInfo = new ResultMapAssociationInfo();
            resultMapAssociationInfo.setSelect(this.getSelect(columnInfo.getJavaType(), columnInfo.getCollectionType()));
            resultMapAssociationInfo.setResultMapId(this.getSubQueryResultMapId(columnInfo.getJavaType(), columnInfo));
            resultMapAssociationInfo.setType(columnInfo.getJavaType());
            resultMapAssociationInfo.setColumnInfoList(entityInfo.getTableColumnInfoList());
            resultMapAssociationInfo.setColumnInfo(columnInfo);

            LoadStrategy loadStrategy = columnInfo.getColumnInfoAnnotationInfo().getLoadStrategy();
            if (loadStrategy == LoadStrategy.SUB || (loadStrategy == LoadStrategy.JOIN && level <= 2)) {
                this.buildResultMapInfo(resultMapInfoList, childrenEntityRelationInfo);
            } else if (loadStrategy == LoadStrategy.JOIN && level > 2) {
                List<ResultMapAssociationInfo> subResultMapAssociationInfoList = this.buildResultMapRelationInfo(resultMapInfoList, childrenEntityRelationInfo.getEntityRelationList());
                resultMapAssociationInfo.setResultMapAssociationInfoList(subResultMapAssociationInfoList);
            } else {
                throw new RuntimeException("未知的加载策略");
            }
            resultMapAssociationInfoList.add(resultMapAssociationInfo);
        }
        return resultMapAssociationInfoList;
    }

    /**
     * 子查询和join的第一级都无法生成join查询，第一级join会造成结果膨胀问题，第二级采用in查询或者批量查询，解决N+1问题，把N+1变成1+1
     *
     * @param entityRelationQueryMethodList
     * @param entityRelationInfo
     */
    private void buildEntityRelationQueryMethod(
            List<ResultMapAssociationInfo> entityRelationQueryMethodList,
            EntityRelationInfo entityRelationInfo,
            ResultMapAssociationInfo parentResultMapAssociationInfo
    ) {
        List<EntityRelationInfo> childrenEntityRelationInfoList = entityRelationInfo.getEntityRelationList();
        for (EntityRelationInfo childrenEntityRelationInfo : childrenEntityRelationInfoList) {
            int level = childrenEntityRelationInfo.getLevel();
            ColumnInfo columnInfo = childrenEntityRelationInfo.getColumnInfo();
            EntityInfo entityInfo = childrenEntityRelationInfo.getEntityInfo();

            ResultMapAssociationInfo resultMapAssociationInfo = new ResultMapAssociationInfo();
            resultMapAssociationInfo.setSelect(this.getSelect(columnInfo.getJavaType(), columnInfo.getCollectionType()));
            resultMapAssociationInfo.setResultMapId(this.getSubQueryResultMapId(columnInfo.getJavaType(), columnInfo));
            resultMapAssociationInfo.setType(columnInfo.getJavaType());
            resultMapAssociationInfo.setColumnInfoList(entityInfo.getTableColumnInfoList());
            resultMapAssociationInfo.setColumnInfo(columnInfo);

            LoadStrategy loadStrategy = columnInfo.getColumnInfoAnnotationInfo().getLoadStrategy();
            if (loadStrategy == LoadStrategy.SUB || (loadStrategy == LoadStrategy.JOIN && level <= 2)) {
                // 子查询和join的第一级都无法生成join查询，第一级join会造成结果膨胀问题，第二级采用in查询或者批量查询，解决N+1问题，把N+1变成1+1
                entityRelationQueryMethodList.add(resultMapAssociationInfo);
            } else if (loadStrategy == LoadStrategy.JOIN && level > 2) {
                parentResultMapAssociationInfo.addResultMapAssociationInfo(resultMapAssociationInfo);
            } else {
                throw new RuntimeException("未知的加载策略");
            }
            this.buildEntityRelationQueryMethod(entityRelationQueryMethodList, childrenEntityRelationInfo, resultMapAssociationInfo);
        }
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
