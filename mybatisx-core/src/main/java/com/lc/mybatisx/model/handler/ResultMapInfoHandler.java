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
            Class<?> resultMapClazz = resultMapInfo != null ? resultMapInfo.getEntityInfo().getClazz() : null;
            Class<?> entityRelationClazz = entityRelationTree.getEntityInfo().getClazz();
            int level = entityRelationTree.getLevel();
            String className = entityRelationTree.getEntityInfo().getClazzName();
            if (level == 1 || entityRelationClazz == resultMapClazz) {
                return this.getResultMapId(className);
            } else {
                Class<?> collectionType = entityRelationTree.getColumnInfo().getCollectionType();
                return this.getNestedSelectResultMapId(className, collectionType);
            }
        }

        protected String getResultMapId(EntityInfo entityInfo) {
            return this.getResultMapId(entityInfo.getClazzName());
        }

        protected String getResultMapId(String className) {
            return String.format("%s_ResultMap", className.replaceAll("\\.", "_"));
        }

        protected String getNestedSelectResultMapId(ColumnInfo columnInfo, EntityInfo entityInfo) {
            return this.getNestedSelectResultMapId(columnInfo, entityInfo);
        }

        protected String getNestedSelectResultMapId(String className, Class<?> collectionType) {
            String resultMapId = this.getResultMapId(className);
            String nestedSelectResultMapType = collectionType == null ? "Association" : "Collection";
            return String.format("%s_%s", resultMapId, nestedSelectResultMapType);
        }
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
                EntityRelationSelectInfo nestedSelectInfo = this.buildNestedSelectInfo(resultMapInfo, childrenEntityRelationTree);

                // 子查询和join的第一级都无法生成join查询，第一级join会造成结果膨胀问题，第二级采用in查询或者批量查询，解决N+1问题，把N+1变成1+1
                FetchMode fetchMode = relationColumnInfo.getFetchMode();
                if (fetchMode == FetchMode.SELECT) {
                    entityRelationSelectInfoList.add(nestedSelectInfo);
                } else if (fetchMode == FetchMode.BATCH) {
                    entityRelationSelectInfoList.add(nestedSelectInfo);
                } else if (fetchMode == FetchMode.JOIN) {
                    if (level <= 2) {
                        entityRelationSelectInfoList.add(nestedSelectInfo);
                    }
                    if (level > 2) {
                        parentEntityRelationSelectInfo.addEntityRelationSelectInfo(nestedSelectInfo);
                    }
                } else {
                    throw new RuntimeException("未知的抓取模式");
                }
                this.buildEntityRelationSelect(resultMapInfo, entityRelationSelectInfoList, childrenEntityRelationTree, nestedSelectInfo);
            }
        }

        public EntityRelationSelectInfo buildNestedSelectInfo(ResultMapInfo resultMapInfo, EntityRelationTree entityRelationTree) {
            ColumnInfo columnInfo = entityRelationTree.getColumnInfo();
            EntityRelationSelectInfo entityRelationSelectInfo = new EntityRelationSelectInfo();
            entityRelationSelectInfo.setId(this.getNestedSelectId(columnInfo.getJavaType(), columnInfo.getCollectionType()));
            entityRelationSelectInfo.setResultMapId(this.getResultMapId(resultMapInfo, entityRelationTree));
            ColumnEntityRelationHelper.copy(entityRelationTree, entityRelationSelectInfo);
            return entityRelationSelectInfo;
        }
    }

    public static class EntityRelationResultMap extends AbstractEntityRelation {

        public ResultMapInfo buildResultMapInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationTree entityRelationTree) {
            String resultMapId = this.getResultMapId(null, entityRelationTree);
            ResultMapInfo resultMapInfo = new ResultMapInfo();
            resultMapInfo.setId(resultMapId);
            ColumnEntityRelationHelper.copy(entityRelationTree, resultMapInfo);

            List<EntityRelationTree> childrenEntityRelationTreeList = entityRelationTree.getEntityRelationList();
            List<ResultMapInfo> resultMapRelationInfoList = this.buildResultMapRelationInfo(resultMapInfoList, entityRelationTree, childrenEntityRelationTreeList);
            resultMapInfo.setResultMapInfoList(resultMapRelationInfoList);

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
        protected List<ResultMapInfo> buildResultMapRelationInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationTree entityRelationTree, List<EntityRelationTree> childrenEntityRelationTreeList) {
            List<ResultMapInfo> resultMapRelationInfoList = new ArrayList();
            for (EntityRelationTree childrenEntityRelationTree : childrenEntityRelationTreeList) {
                int level = childrenEntityRelationTree.getLevel();
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) childrenEntityRelationTree.getColumnInfo();

                // 构建一个内嵌查询结果集
                ResultMapInfo nestedResultMapInfo = this.buildNestedSelectResultMapInfo(childrenEntityRelationTree, relationColumnInfo);
                resultMapRelationInfoList.add(nestedResultMapInfo);

                FetchMode fetchMode = relationColumnInfo.getFetchMode();
                RelationType RelationType = relationColumnInfo.getRelationType();
                if (fetchMode == FetchMode.SELECT) {
                    this.buildOneToOneResultMapInfo(resultMapInfoList, childrenEntityRelationTree);
                } else if (fetchMode == FetchMode.BATCH) {
                    if (RelationType == RelationType.ONE_TO_ONE || RelationType == RelationType.MANY_TO_ONE) {
                        this.buildOneToOneResultMapInfo(resultMapInfoList, childrenEntityRelationTree);
                    }
                    if (RelationType == RelationType.MANY_TO_ONE || RelationType == RelationType.MANY_TO_MANY) {
                        this.buildManyToManyBatchSelectResultMapInfo(resultMapInfoList, entityRelationTree, childrenEntityRelationTree);
                    }
                } else if (fetchMode == FetchMode.JOIN) {
                    if (level <= 2) {
                        this.buildOneToOneResultMapInfo(resultMapInfoList, childrenEntityRelationTree);
                    }
                    if (level > 2) {
                        List<ResultMapInfo> childResultMapRelationInfoList = this.buildResultMapRelationInfo(resultMapInfoList, childrenEntityRelationTree, childrenEntityRelationTree.getEntityRelationList());
                        nestedResultMapInfo.setResultMapInfoList(childResultMapRelationInfoList);
                    }
                } else {
                    throw new RuntimeException("未知的抓取模式");
                }
            }
            return resultMapRelationInfoList;
        }

        public ResultMapInfo buildOneToOneResultMapInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationTree entityRelationTree) {
            String resultMapId = this.getResultMapId(null, entityRelationTree);
            ResultMapInfo resultMapInfo = new ResultMapInfo();
            resultMapInfo.setId(resultMapId);
            ColumnEntityRelationHelper.copy(entityRelationTree, resultMapInfo);

            List<EntityRelationTree> childrenEntityRelationTreeList = entityRelationTree.getEntityRelationList();
            List<ResultMapInfo> resultMapRelationInfoList = this.buildResultMapRelationInfo(resultMapInfoList, entityRelationTree, childrenEntityRelationTreeList);
            resultMapInfo.setResultMapInfoList(resultMapRelationInfoList);

            resultMapInfoList.add(resultMapInfo);
            return resultMapInfo;
        }

        public ResultMapInfo buildOneToManyResultMapInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationTree entityRelationTree) {
            return this.buildOneToOneResultMapInfo(resultMapInfoList, entityRelationTree);
        }

        public ResultMapInfo buildManyToOneResultMapInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationTree entityRelationTree) {
            return this.buildOneToOneResultMapInfo(resultMapInfoList, entityRelationTree);
        }

        private ResultMapInfo buildManyToManyResultMapInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationTree parentEntityRelationTree, EntityRelationTree entityRelationTree) {
            if (entityRelationTree == null) {

            }
            return this.buildOneToOneResultMapInfo(resultMapInfoList, entityRelationTree);
        }

        /**
         * 构建多对多批量查询结果集信息
         * @param resultMapInfoList
         * @param entityRelationTree
         * @return
         */
        private ManyToManyBatchSelectResultMapInfo buildManyToManyBatchSelectResultMapInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationTree parentEntityRelationTree, EntityRelationTree entityRelationTree) {
            String resultMapId = this.getResultMapId(null, entityRelationTree);
            ManyToManyBatchSelectResultMapInfo manyToManyBatchSelectResultMapInfo = new ManyToManyBatchSelectResultMapInfo();
            manyToManyBatchSelectResultMapInfo.setId(resultMapId);
            ColumnEntityRelationHelper.copy(entityRelationTree, manyToManyBatchSelectResultMapInfo);

            // 构建一个内嵌查询结果集
            ResultMapInfo resultMapInfo = new ResultMapInfo();
            resultMapInfo.setId(resultMapId);
            resultMapInfo.setEntityInfo(parentEntityRelationTree.getEntityInfo());
            resultMapInfo.setResultMapInfoList(Arrays.asList(manyToManyBatchSelectResultMapInfo));
            manyToManyBatchSelectResultMapInfo.setManyToManyBatchSelectResultMapInfo(resultMapInfo);

            List<EntityRelationTree> childrenEntityRelationTreeList = entityRelationTree.getEntityRelationList();
            List<ResultMapInfo> resultMapRelationInfoList = this.buildResultMapRelationInfo(resultMapInfoList, entityRelationTree, childrenEntityRelationTreeList);
            manyToManyBatchSelectResultMapInfo.setResultMapInfoList(resultMapRelationInfoList);

            resultMapInfoList.add(manyToManyBatchSelectResultMapInfo);

            return manyToManyBatchSelectResultMapInfo;
        }

        private ResultMapInfo buildNestedSelectResultMapInfo(EntityRelationTree entityRelationTree, RelationColumnInfo relationColumnInfo) {
            String nestedSelectId = this.getNestedSelectId(relationColumnInfo.getJavaType(), relationColumnInfo.getCollectionType());
            ResultMapInfo resultMapRelationInfo = new ResultMapInfo();
            resultMapRelationInfo.setNestedSelectId(nestedSelectId);

            resultMapRelationInfo.setColumnInfo(entityRelationTree.getColumnInfo());
            /*resultMapRelationInfo.setMiddleEntityInfo(entityRelationTree.getMiddleEntityInfo());
            resultMapRelationInfo.setEntityInfo(entityRelationTree.getEntityInfo());*/

            return resultMapRelationInfo;
        }
    }
}
