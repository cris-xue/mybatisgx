package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.FetchMode;
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
        EntityRelationTree entityRelationTree = mapperInfo.getEntityRelationTree(resultClass);
        resultMapInfo = this.buildResultMapInfo(resultMapInfoList, entityRelationTree);
        this.buildEntityRelationSelect(resultMapInfo, entityRelationSelectInfoList, entityRelationTree, null);

        mapperInfo.addResultMapInfoList(resultMapInfoList);
        mapperInfo.setEntityRelationSelectInfoList(entityRelationSelectInfoList);
        return resultMapInfo.getId();
    }

    private ResultMapInfo buildResultMapInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationTree entityRelationTree) {
        List<EntityRelationTree> entityRelationTreeList = entityRelationTree.getEntityRelationList();
        List<ResultMapInfo> resultMapRelationInfoList = this.buildResultMapRelationInfo(resultMapInfoList, entityRelationTreeList);
        EntityInfo entityInfo = entityRelationTree.getEntityInfo();
        ResultMapInfo resultMapInfo = new ResultMapInfo();
        resultMapInfo.setId(this.getResultMapId(null, entityRelationTree));
        resultMapInfo.setEntityInfo(entityInfo);
        resultMapInfo.setResultMapInfoList(resultMapRelationInfoList);
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
    private List<ResultMapInfo> buildResultMapRelationInfo(List<ResultMapInfo> resultMapInfoList, List<EntityRelationTree> childrenEntityRelationInfoList) {
        List<ResultMapInfo> resultMapRelationInfoList = new ArrayList();
        for (EntityRelationTree childrenEntityRelationInfo : childrenEntityRelationInfoList) {
            int level = childrenEntityRelationInfo.getLevel();
            RelationColumnInfo columnInfo = (RelationColumnInfo) childrenEntityRelationInfo.getColumnInfo();
            EntityInfo entityInfo = childrenEntityRelationInfo.getEntityInfo();

            ResultMapInfo resultMapRelationInfo = new ResultMapInfo();
            resultMapRelationInfo.setSelect(this.getSelect(columnInfo.getJavaType(), columnInfo.getCollectionType()));
            resultMapRelationInfo.setColumnInfo(columnInfo);
            resultMapRelationInfo.setEntityInfo(entityInfo);

            FetchMode fetchMode = columnInfo.getFetchMode();
            if (fetchMode == FetchMode.SELECT) {
                this.buildResultMapInfo(resultMapInfoList, childrenEntityRelationInfo);
            } else if (fetchMode == FetchMode.BATCH) {
                this.buildResultMapInfo(resultMapInfoList, childrenEntityRelationInfo);
            } else if (fetchMode == FetchMode.JOIN && level <= 2) {
                this.buildResultMapInfo(resultMapInfoList, childrenEntityRelationInfo);
            } else if (fetchMode == FetchMode.JOIN && level > 2) {
                List<ResultMapInfo> subResultMapRelationInfoList = this.buildResultMapRelationInfo(resultMapInfoList, childrenEntityRelationInfo.getEntityRelationList());
                resultMapRelationInfo.setResultMapInfoList(subResultMapRelationInfoList);
            } else {
                throw new RuntimeException("未知的抓取模式");
            }
            resultMapRelationInfoList.add(resultMapRelationInfo);
        }
        return resultMapRelationInfoList;
    }

    /**
     * 子查询和join的第一级都无法生成join查询，第一级join会造成结果膨胀问题，第二级采用in查询或者批量查询，解决N+1问题，把N+1变成1+1
     *
     * @param entityRelationSelectInfoList
     * @param entityRelationTree
     */
    private void buildEntityRelationSelect(
            ResultMapInfo resultMapInfo,
            List<EntityRelationSelectInfo> entityRelationSelectInfoList,
            EntityRelationTree entityRelationTree,
            EntityRelationSelectInfo parentEntityRelationSelectInfo
    ) {
        List<EntityRelationTree> childrenEntityRelationInfoList = entityRelationTree.getEntityRelationList();
        for (EntityRelationTree childrenEntityRelationInfo : childrenEntityRelationInfoList) {
            int level = childrenEntityRelationInfo.getLevel();
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) childrenEntityRelationInfo.getColumnInfo();
            EntityRelationSelectInfo entityRelationSelectInfo = this.buildEntityRelationSelect(resultMapInfo, childrenEntityRelationInfo);

            // 子查询和join的第一级都无法生成join查询，第一级join会造成结果膨胀问题，第二级采用in查询或者批量查询，解决N+1问题，把N+1变成1+1
            FetchMode fetchMode = relationColumnInfo.getFetchMode();
            if (fetchMode == FetchMode.SELECT) {
                entityRelationSelectInfoList.add(entityRelationSelectInfo);
            } else if (fetchMode == FetchMode.BATCH) {
                entityRelationSelectInfoList.add(entityRelationSelectInfo);
            } else if (fetchMode == FetchMode.JOIN && level <= 2) {
                entityRelationSelectInfoList.add(entityRelationSelectInfo);
            } else if (fetchMode == FetchMode.JOIN && level > 2) {
                parentEntityRelationSelectInfo.addEntityRelationSelectInfo(entityRelationSelectInfo);
            } else {
                throw new RuntimeException("未知的抓取模式");
            }
            this.buildEntityRelationSelect(resultMapInfo, entityRelationSelectInfoList, childrenEntityRelationInfo, entityRelationSelectInfo);
        }
    }

    private EntityRelationSelectInfo buildEntityRelationSelect(ResultMapInfo resultMapInfo, EntityRelationTree entityRelationTree) {
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) entityRelationTree.getColumnInfo();
        EntityInfo entityInfo = entityRelationTree.getEntityInfo();
        EntityRelationSelectInfo entityRelationSelectInfo = new EntityRelationSelectInfo();
        entityRelationSelectInfo.setId(this.getSelect(relationColumnInfo.getJavaType(), relationColumnInfo.getCollectionType()));
        entityRelationSelectInfo.setResultMapId(this.getResultMapId(resultMapInfo, entityRelationTree));
        entityRelationSelectInfo.setColumnInfo(relationColumnInfo);
        entityRelationSelectInfo.setEntityInfo(entityInfo);
        Boolean isExistMiddleTable = relationColumnInfo.getManyToMany() != null;
        entityRelationSelectInfo.setExistMiddleTable(isExistMiddleTable);
        return entityRelationSelectInfo;
    }

    protected String getResultMapId(ResultMapInfo resultMapInfo, EntityRelationTree entityRelationTree) {
        Class<?> resultMapClazz = resultMapInfo != null ? resultMapInfo.getEntityInfo().getClazz() : null;
        Class<?> entityRelationClazz = entityRelationTree.getEntityInfo().getClazz();
        int level = entityRelationTree.getLevel();
        String className = entityRelationTree.getEntityInfo().getClazzName();
        if (level == 1 || entityRelationClazz == resultMapClazz) {
            return String.format("%sResultMap", className.replaceAll("\\.", "_"));
        } else {
            return this.getSubQueryResultMapId(className, entityRelationTree.getColumnInfo());
        }
    }

    protected String getSubQueryResultMapId(String className, ColumnInfo columnInfo) {
        Class<?> collectionType = columnInfo.getCollectionType();
        String resultMapId;
        if (collectionType == null) {
            resultMapId = String.format("%sResultMap%s", className.replaceAll("\\.", "_"), "Association");
        } else {
            resultMapId = String.format("%sResultMap%s", className.replaceAll("\\.", "_"), "Collection");
        }
        return resultMapId;
    }

    private String getSelect(Class<?> entityClass, Class<?> collectionType) {
        return String.format("find%s%s", entityClass.getSimpleName(), collectionType != null ? collectionType.getSimpleName() : "");
    }
}
