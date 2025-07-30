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
        List<EntityRelationSelectInfo> entityRelationSelectInfoList = new ArrayList();
        EntityRelationInfo entityRelationInfo = mapperInfo.getEntityRelationInfo(resultClass);
        ResultMapInfo rootResultMapInfo = this.buildResultMapInfo(resultMapInfoList, entityRelationInfo);
        this.buildEntityRelationSelect(entityRelationSelectInfoList, entityRelationInfo, null);

        mapperInfo.addResultMapInfoList(resultMapInfoList);
        mapperInfo.setEntityRelationSelectInfoList(entityRelationSelectInfoList);
        return rootResultMapInfo.getId();
    }

    private ResultMapInfo buildResultMapInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationInfo entityRelationInfo) {
        List<ResultMapRelationInfo> resultMapRelationInfoList = this.buildResultMapRelationInfo(
                resultMapInfoList,
                entityRelationInfo.getEntityRelationList()
        );
        EntityInfo entityInfo = entityRelationInfo.getEntityInfo();
        Class<?> clazz = entityInfo.getClazz();
        ResultMapInfo resultMapInfo = new ResultMapInfo();
        resultMapInfo.setId(this.getResultMapId(clazz));
        resultMapInfo.setEntityInfo(entityInfo);
        resultMapInfo.setResultMapRelationInfoList(resultMapRelationInfoList);
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
    private List<ResultMapRelationInfo> buildResultMapRelationInfo(List<ResultMapInfo> resultMapInfoList, List<EntityRelationInfo> childrenEntityRelationInfoList) {
        List<ResultMapRelationInfo> resultMapRelationInfoList = new ArrayList();
        for (EntityRelationInfo childrenEntityRelationInfo : childrenEntityRelationInfoList) {
            int level = childrenEntityRelationInfo.getLevel();
            ColumnInfo columnInfo = childrenEntityRelationInfo.getColumnInfo();
            EntityInfo entityInfo = childrenEntityRelationInfo.getEntityInfo();

            ResultMapRelationInfo resultMapRelationInfo = new ResultMapRelationInfo();
            resultMapRelationInfo.setSelect(this.getSelect(columnInfo.getJavaType(), columnInfo.getCollectionType()));
            resultMapRelationInfo.setColumnInfo(columnInfo);
            resultMapRelationInfo.setEntityInfo(entityInfo);

            LoadStrategy loadStrategy = columnInfo.getColumnInfoAnnotationInfo().getLoadStrategy();
            if (loadStrategy == LoadStrategy.SUB || (loadStrategy == LoadStrategy.JOIN && level <= 2)) {
                this.buildResultMapInfo(resultMapInfoList, childrenEntityRelationInfo);
            } else if (loadStrategy == LoadStrategy.JOIN && level > 2) {
                List<ResultMapRelationInfo> subResultMapAssociationInfoList = this.buildResultMapRelationInfo(resultMapInfoList, childrenEntityRelationInfo.getEntityRelationList());
                resultMapRelationInfo.setResultMapAssociationInfoList(subResultMapAssociationInfoList);
            } else {
                throw new RuntimeException("未知的加载策略");
            }
            resultMapRelationInfoList.add(resultMapRelationInfo);
        }
        return resultMapRelationInfoList;
    }

    /**
     * 子查询和join的第一级都无法生成join查询，第一级join会造成结果膨胀问题，第二级采用in查询或者批量查询，解决N+1问题，把N+1变成1+1
     *
     * @param entityRelationSelectInfoList
     * @param entityRelationInfo
     */
    private void buildEntityRelationSelect(
            List<EntityRelationSelectInfo> entityRelationSelectInfoList,
            EntityRelationInfo entityRelationInfo,
            EntityRelationSelectInfo parentEntityRelationSelectInfo
    ) {
        List<EntityRelationInfo> childrenEntityRelationInfoList = entityRelationInfo.getEntityRelationList();
        for (EntityRelationInfo childrenEntityRelationInfo : childrenEntityRelationInfoList) {
            int level = childrenEntityRelationInfo.getLevel();
            ColumnInfo columnInfo = childrenEntityRelationInfo.getColumnInfo();
            EntityInfo entityInfo = childrenEntityRelationInfo.getEntityInfo();

            EntityRelationSelectInfo entityRelationSelectInfo = new EntityRelationSelectInfo();
            entityRelationSelectInfo.setId(this.getSelect(columnInfo.getJavaType(), columnInfo.getCollectionType()));
            entityRelationSelectInfo.setResultMapId(this.getSubQueryResultMapId(columnInfo.getJavaType(), columnInfo));
            entityRelationSelectInfo.setColumnInfo(columnInfo);
            entityRelationSelectInfo.setEntityInfo(entityInfo);

            LoadStrategy loadStrategy = columnInfo.getColumnInfoAnnotationInfo().getLoadStrategy();
            if (loadStrategy == LoadStrategy.SUB || (loadStrategy == LoadStrategy.JOIN && level <= 2)) {
                // 子查询和join的第一级都无法生成join查询，第一级join会造成结果膨胀问题，第二级采用in查询或者批量查询，解决N+1问题，把N+1变成1+1
                entityRelationSelectInfoList.add(entityRelationSelectInfo);
            } else if (loadStrategy == LoadStrategy.JOIN && level > 2) {
                parentEntityRelationSelectInfo.addEntityRelationSelectInfo(entityRelationSelectInfo);
            } else {
                throw new RuntimeException("未知的加载策略");
            }
            this.buildEntityRelationSelect(entityRelationSelectInfoList, childrenEntityRelationInfo, entityRelationSelectInfo);
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
