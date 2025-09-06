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
        EntityRelationTree entityRelationInfo = mapperInfo.getEntityRelationTree(resultClass);
        resultMapInfo = this.buildResultMapInfo(resultMapInfoList, entityRelationInfo);
        this.buildEntityRelationSelect(resultMapInfo, entityRelationSelectInfoList, entityRelationInfo, null);

        mapperInfo.addResultMapInfoList(resultMapInfoList);
        mapperInfo.setEntityRelationSelectInfoList(entityRelationSelectInfoList);
        return resultMapInfo.getId();
    }

    private ResultMapInfo buildResultMapInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationTree entityRelationInfo) {
        List<ResultMapInfo> resultMapRelationInfoList = this.buildResultMapRelationInfo(
                resultMapInfoList,
                entityRelationInfo.getEntityRelationList()
        );
        EntityInfo entityInfo = entityRelationInfo.getEntityInfo();
        ResultMapInfo resultMapInfo = new ResultMapInfo();
        resultMapInfo.setId(this.getResultMapId(null, entityRelationInfo));
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
            ColumnInfo columnInfo = childrenEntityRelationInfo.getColumnInfo();
            EntityInfo entityInfo = childrenEntityRelationInfo.getEntityInfo();

            ResultMapInfo resultMapRelationInfo = new ResultMapInfo();
            resultMapRelationInfo.setSelect(this.getSelect(columnInfo.getJavaType(), columnInfo.getCollectionType()));
            resultMapRelationInfo.setColumnInfo(columnInfo);
            resultMapRelationInfo.setEntityInfo(entityInfo);

            FetchMode fetchMode = columnInfo.getColumnRelationInfo().getFetchMode();
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
     * @param entityRelationInfo
     */
    private void buildEntityRelationSelect(
            ResultMapInfo resultMapInfo,
            List<EntityRelationSelectInfo> entityRelationSelectInfoList,
            EntityRelationTree entityRelationInfo,
            EntityRelationSelectInfo parentEntityRelationSelectInfo
    ) {
        List<EntityRelationTree> childrenEntityRelationInfoList = entityRelationInfo.getEntityRelationList();
        for (EntityRelationTree childrenEntityRelationInfo : childrenEntityRelationInfoList) {
            int level = childrenEntityRelationInfo.getLevel();
            ColumnInfo columnInfo = childrenEntityRelationInfo.getColumnInfo();
            EntityRelationSelectInfo entityRelationSelectInfo = this.buildEntityRelationSelect(resultMapInfo, childrenEntityRelationInfo);

            // 子查询和join的第一级都无法生成join查询，第一级join会造成结果膨胀问题，第二级采用in查询或者批量查询，解决N+1问题，把N+1变成1+1
            FetchMode fetchMode = columnInfo.getColumnRelationInfo().getFetchMode();
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

    private EntityRelationSelectInfo buildEntityRelationSelect(ResultMapInfo resultMapInfo, EntityRelationTree entityRelationInfo) {
        ColumnInfo columnInfo = entityRelationInfo.getColumnInfo();
        EntityInfo entityInfo = entityRelationInfo.getEntityInfo();
        EntityRelationSelectInfo entityRelationSelectInfo = new EntityRelationSelectInfo();
        entityRelationSelectInfo.setId(this.getSelect(columnInfo.getJavaType(), columnInfo.getCollectionType()));
        entityRelationSelectInfo.setResultMapId(this.getResultMapId(resultMapInfo, entityRelationInfo));
        entityRelationSelectInfo.setColumnInfo(columnInfo);
        entityRelationSelectInfo.setEntityInfo(entityInfo);
        Boolean isExistMiddleTable = columnInfo.getColumnRelationInfo().getManyToMany() != null;
        entityRelationSelectInfo.setExistMiddleTable(isExistMiddleTable);

        // 处理关联查询主外键映射，方便模型处理
        /*ColumnRelationInfo columnRelationInfo = columnInfo.getColumnRelationInfo();
        String mappedBy = columnRelationInfo.getMappedBy();
        if (isExistMiddleTable) {
            if (StringUtils.isBlank(mappedBy)) {
                entityRelationSelectInfo.addForeignKeyColumnInfo(entityInfo.getTableName(), columnRelationInfo.getInverseForeignKeyColumnInfoList());
            } else {
                ColumnRelationInfo mappedByColumnRelationInfo = entityInfo.getColumnInfo(mappedBy).getColumnRelationInfo();
                entityRelationSelectInfo.addForeignKeyColumnInfo(entityInfo.getTableName(), mappedByColumnRelationInfo.getForeignKeyColumnInfoList());
            }
        } else {
            if (StringUtils.isBlank(mappedBy)) {
                entityRelationSelectInfo.addForeignKeyColumnInfo(entityInfo.getTableName(), columnRelationInfo.getInverseForeignKeyColumnInfoList());
            } else {
                ColumnRelationInfo mappedByColumnRelationInfo = entityInfo.getColumnInfo(mappedBy).getColumnRelationInfo();
                entityRelationSelectInfo.addForeignKeyColumnInfo(entityInfo.getTableName(), mappedByColumnRelationInfo.getInverseForeignKeyColumnInfoList());
            }
        }*/
        return entityRelationSelectInfo;
    }

    protected String getResultMapId(ResultMapInfo resultMapInfo, EntityRelationTree entityRelationInfo) {
        Class<?> resultMapClazz = resultMapInfo != null ? resultMapInfo.getEntityInfo().getClazz() : null;
        Class<?> entityRelationClazz = entityRelationInfo.getEntityInfo().getClazz();
        int level = entityRelationInfo.getLevel();
        String className = entityRelationInfo.getEntityInfo().getClazzName();
        if (level == 1 || entityRelationClazz == resultMapClazz) {
            return String.format("%sResultMap", className.replaceAll("\\.", "_"));
        } else {
            return this.getSubQueryResultMapId(className, entityRelationInfo.getColumnInfo());
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
