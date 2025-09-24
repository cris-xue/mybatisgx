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

    private EntityRelationSelect entityRelationSelect = new EntityRelationSelect();
    private EntityRelationResultMap relationResultMap = new EntityRelationResultMap();

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
        EntityRelationTree entityRelationTree = mapperInfo.getEntityRelationTree(resultClass);

        // 处理结果集
        List<ResultMapInfo> resultMapInfoList = new ArrayList();
        resultMapInfo = relationResultMap.buildResultMapInfo(resultMapInfoList, entityRelationTree);

        // 处理关联查询
        List<EntityRelationSelectInfo> entityRelationSelectInfoList = new ArrayList();
        entityRelationSelect.buildEntityRelationSelect(resultMapInfo, entityRelationSelectInfoList, entityRelationTree, null);

        mapperInfo.addResultMapInfoList(resultMapInfoList);
        mapperInfo.setEntityRelationSelectInfoList(entityRelationSelectInfoList);
        return resultMapInfo.getId();
    }

    public static abstract class AbstractEntityRelation {

        protected String getNestedSelectId(Class<?> entityClass, Class<?> collectionType) {
            return String.format("findNestedSelect%s%s", entityClass.getSimpleName(), collectionType != null ? collectionType.getSimpleName() : "");
        }

        protected String getResultMapId(ResultMapInfo resultMapInfo, EntityRelationTree entityRelationTree) {
            int level = entityRelationTree.getLevel();
            ColumnInfo columnInfo = entityRelationTree.getColumnInfo();
            EntityInfo entityInfo = entityRelationTree.getEntityInfo();

            Class<?> resultMapClazz = resultMapInfo != null ? entityInfo.getClazz() : null;
            Class<?> entityRelationClazz = entityInfo.getClazz();
            if (level == 1 || entityRelationClazz == resultMapClazz) {
                return this.getResultMapId(entityInfo);
            } else {
                return this.getNestedSelectResultMapId(columnInfo, entityInfo);
            }
        }

        protected String getResultMapId(EntityInfo entityInfo) {
            String className = entityInfo.getClazzName();
            return String.format("%s_ResultMap", className.replaceAll("\\.", "_"));
        }

        protected String getNestedSelectResultMapId(ColumnInfo columnInfo, EntityInfo entityInfo) {
            String resultMapId = this.getResultMapId(entityInfo);
            Class<?> collectionType = columnInfo.getCollectionType();
            String nestedSelectResultMapType = collectionType == null ? "Association" : "Collection";
            return String.format("%s_%s", resultMapId, nestedSelectResultMapType);
        }
    }

    public static abstract class AbstractEntityRelationResultMap extends AbstractEntityRelation {

        protected ResultMapInfo buildResultMapInfoNew(List<ResultMapInfo> resultMapInfoList, EntityRelationTree entityRelationTree) {
            String resultMapId = this.getResultMapId(null, entityRelationTree);
            ResultMapInfo resultMapInfo = this.buildResultMapInfo(resultMapId, entityRelationTree);

            List<EntityRelationTree> childrenEntityRelationTreeList = entityRelationTree.getEntityRelationList();
            List<ResultMapInfo> resultMapRelationInfoList = this.buildResultMapRelationInfo(resultMapInfoList, entityRelationTree, childrenEntityRelationTreeList);
            resultMapInfo.setResultMapInfoList(resultMapRelationInfoList);

            return resultMapInfo;
        }

        protected abstract ResultMapInfo buildResultMapInfo(String resultMapId, EntityRelationTree entityRelationTree);

        protected abstract List<ResultMapInfo> buildResultMapRelationInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationTree entityRelationTree, List<EntityRelationTree> childrenEntityRelationTreeList);
    }

    public static class EntityRelationSelect extends AbstractEntityRelation {

        /**
         * 子查询和join的第一级都无法生成join查询，第一级join会造成结果膨胀问题，第二级采用in查询或者批量查询，解决N+1问题，把N+1变成1+1
         *
         * @param entityRelationSelectInfoList
         * @param entityRelationTree
         */
        public void buildEntityRelationSelect(
                ResultMapInfo resultMapInfo,
                List<EntityRelationSelectInfo> entityRelationSelectInfoList,
                EntityRelationTree entityRelationTree,
                EntityRelationSelectInfo parentEntityRelationSelectInfo
        ) {
            List<EntityRelationTree> childrenEntityRelationTreeList = entityRelationTree.getEntityRelationList();
            for (EntityRelationTree childrenEntityRelationTree : childrenEntityRelationTreeList) {
                int level = childrenEntityRelationTree.getLevel();
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) childrenEntityRelationTree.getColumnInfo();
                EntityRelationSelectInfo entityRelationSelectInfo = this.buildEntityRelationSelect(resultMapInfo, childrenEntityRelationTree);

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
                this.buildEntityRelationSelect(resultMapInfo, entityRelationSelectInfoList, childrenEntityRelationTree, entityRelationSelectInfo);
            }
        }

        public EntityRelationSelectInfo buildEntityRelationSelect(ResultMapInfo resultMapInfo, EntityRelationTree entityRelationTree) {
            ColumnInfo columnInfo = entityRelationTree.getColumnInfo();

            EntityRelationSelectInfo entityRelationSelectInfo = new EntityRelationSelectInfo();
            entityRelationSelectInfo.setId(this.getNestedSelectId(columnInfo.getJavaType(), columnInfo.getCollectionType()));
            entityRelationSelectInfo.setResultMapId(this.getResultMapId(resultMapInfo, entityRelationTree));
            ColumnEntityRelationHelper.copy(entityRelationTree, entityRelationSelectInfo);
            /*entityRelationSelectInfo.setColumnInfo(relationColumnInfo);
            entityRelationSelectInfo.setMiddleEntityInfo(middleEntityInfo);
            entityRelationSelectInfo.setEntityInfo(entityInfo);*/
            /*Boolean isExistMiddleTable = relationColumnInfo.getManyToMany() != null;
            entityRelationSelectInfo.setExistMiddleTable(isExistMiddleTable);*/
            return entityRelationSelectInfo;
        }
    }

    public static class EntityRelationResultMap extends AbstractEntityRelationResultMap {

        public ResultMapInfo buildResultMapInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationTree entityRelationTree) {
            String resultMapId = this.getResultMapId(null, entityRelationTree);
            ResultMapInfo resultMapInfo = new ResultMapInfo();
            resultMapInfo.setId(resultMapId);
            ColumnEntityRelationHelper.copy(entityRelationTree, resultMapInfo);
            // ResultMapInfo resultMapInfo = this.buildResultMapInfo(resultMapId, entityRelationTree);

            List<EntityRelationTree> childrenEntityRelationTreeList = entityRelationTree.getEntityRelationList();
            List<ResultMapInfo> resultMapRelationInfoList = this.buildResultMapRelationInfo(resultMapInfoList, entityRelationTree, childrenEntityRelationTreeList);
            resultMapInfo.setResultMapInfoList(resultMapRelationInfoList);

            // ResultMapInfo resultMapInfo = this.buildResultMapInfoNew(resultMapInfoList, entityRelationTree);
            resultMapInfoList.add(resultMapInfo);
            return resultMapInfo;
        }

        /**
         * 构建结果集关联信息
         *
         * @param resultMapInfoList              结果集信息列表
         * @param childrenEntityRelationTreeList
         * @return
         */
        @Override
        protected List<ResultMapInfo> buildResultMapRelationInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationTree entityRelationTree, List<EntityRelationTree> childrenEntityRelationTreeList) {
            List<ResultMapInfo> resultMapRelationInfoList = new ArrayList();
            for (EntityRelationTree childrenEntityRelationTree : childrenEntityRelationTreeList) {
                int level = childrenEntityRelationTree.getLevel();
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) childrenEntityRelationTree.getColumnInfo();
                MiddleEntityInfo middleEntityInfo = childrenEntityRelationTree.getMiddleEntityInfo();
                EntityInfo entityInfo = childrenEntityRelationTree.getEntityInfo();

                ResultMapInfo resultMapRelationInfo = new ResultMapInfo();
                resultMapRelationInfo.setNestedSelectId(this.getNestedSelectId(relationColumnInfo.getJavaType(), relationColumnInfo.getCollectionType()));
                ColumnEntityRelationHelper.copy(childrenEntityRelationTree, resultMapRelationInfo);
                /*resultMapRelationInfo.setColumnInfo(relationColumnInfo);
                resultMapRelationInfo.setMiddleEntityInfo(middleEntityInfo);
                resultMapRelationInfo.setEntityInfo(entityInfo);*/

                FetchMode fetchMode = relationColumnInfo.getFetchMode();
                if (fetchMode == FetchMode.SELECT) {
                    this.buildResultMapInfo(resultMapInfoList, childrenEntityRelationTree);
                } else if (fetchMode == FetchMode.BATCH) {
                    if (middleEntityInfo != null) {
                        // String resultMapId = this.getResultMapId(null, childrenEntityRelationTree);
                        // ResultMapInfo parentResultMapInfo = this.buildResultMapInfo(resultMapId, entityRelationTree);
                        this.buildManyToManyBatchSelectResultMapInfo(resultMapInfoList, childrenEntityRelationTree);
                    } else {
                        this.buildResultMapInfo(resultMapInfoList, childrenEntityRelationTree);
                    }
                } else if (fetchMode == FetchMode.JOIN && level <= 2) {
                    this.buildResultMapInfo(resultMapInfoList, childrenEntityRelationTree);
                } else if (fetchMode == FetchMode.JOIN && level > 2) {
                    List<ResultMapInfo> subResultMapRelationInfoList = this.buildResultMapRelationInfo(resultMapInfoList, childrenEntityRelationTree, childrenEntityRelationTree.getEntityRelationList());
                    resultMapRelationInfo.setResultMapInfoList(subResultMapRelationInfoList);
                } else {
                    throw new RuntimeException("未知的抓取模式");
                }
                resultMapRelationInfoList.add(resultMapRelationInfo);
            }
            return resultMapRelationInfoList;
        }

        @Override
        protected ResultMapInfo buildResultMapInfo(String resultMapId, EntityRelationTree entityRelationTree) {
            ResultMapInfo resultMapInfo = new ResultMapInfo();
            resultMapInfo.setId(resultMapId);
            resultMapInfo.setMiddleEntityInfo(entityRelationTree.getMiddleEntityInfo());
            resultMapInfo.setEntityInfo(entityRelationTree.getEntityInfo());
            return resultMapInfo;
        }

        /**
         * 构建多对多批量查询结果集信息
         * @param resultMapInfoList
         * @param entityRelationTree
         * @return
         */
        private ResultMapInfo buildManyToManyBatchSelectResultMapInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationTree entityRelationTree) {
            String resultMapId = this.getResultMapId(null, entityRelationTree);
            ManyToManyBatchSelectResultMapInfo manyToManyBatchSelectResultMapInfo = new ManyToManyBatchSelectResultMapInfo();
            manyToManyBatchSelectResultMapInfo.setNestedSelectId(resultMapId);
            ColumnEntityRelationHelper.copy(entityRelationTree, manyToManyBatchSelectResultMapInfo);
            /*manyToManyBatchSelectResultMapInfo.setId(resultMapId);
            manyToManyBatchSelectResultMapInfo.setMiddleEntityInfo(entityRelationTree.getMiddleEntityInfo());
            manyToManyBatchSelectResultMapInfo.setEntityInfo(entityRelationTree.getEntityInfo());*/

            List<EntityRelationTree> childrenEntityRelationTreeList = entityRelationTree.getEntityRelationList();
            List<ResultMapInfo> resultMapRelationInfoList = this.buildResultMapRelationInfo(resultMapInfoList, entityRelationTree, childrenEntityRelationTreeList);
            manyToManyBatchSelectResultMapInfo.setResultMapInfoList(resultMapRelationInfoList);

            resultMapInfoList.add(manyToManyBatchSelectResultMapInfo);

            return manyToManyBatchSelectResultMapInfo;
        }
    }
}
