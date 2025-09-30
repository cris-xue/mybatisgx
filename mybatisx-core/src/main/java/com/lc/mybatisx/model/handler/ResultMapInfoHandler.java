package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.FetchMode;
import com.lc.mybatisx.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        EntityRelationTree entityRelationTree = mapperInfo.getEntityRelationTree(resultClass);

        // 处理结果集
        ResultMapContext resultMapContext = this.relationResultMap.buildResultMapInfo(entityRelationTree);
        ResultMapInfo resultMapInfo = resultMapContext.getResultMapInfo();
        List<ResultMapInfo> resultMapInfoList = resultMapContext.getResultMapInfoList();

        String resultMapId = resultMapInfo.getId();
        ResultMapInfo existResultMapInfo = mapperInfo.getResultMapInfo(resultMapId);
        if (existResultMapInfo != null) {
            return existResultMapInfo.getId();
        }

        // 处理关联查询
        List<EntityRelationSelectInfo> entityRelationSelectInfoList = new ArrayList();
        entityRelationSelect.buildEntityRelationSelect(resultMapInfo, entityRelationSelectInfoList, entityRelationTree, null);

        mapperInfo.addResultMapInfoList(resultMapInfoList);
        mapperInfo.setEntityRelationSelectInfoList(entityRelationSelectInfoList);
        return resultMapInfo.getId();
    }


    public static abstract class AbstractEntityRelation {

        protected String getNestedSelectId(Class<?> entityClass, Class<?> collectionType) {
            return String.format("nestedSelect%s%s", entityClass.getSimpleName(), collectionType != null ? collectionType.getSimpleName() : "");
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

        protected String getResultMapId(String className) {
            return String.format("%s_ResultMap", className.replaceAll("\\.", "_"));
        }


        protected List<EntityInfo> getEntityInfoList(ColumnEntityRelation columnEntityRelation) {
            List<EntityInfo> entityInfoList = new ArrayList();
            EntityInfo entityInfo = columnEntityRelation.getEntityInfo();
            if (entityInfo != null) {
                entityInfoList.add(columnEntityRelation.getEntityInfo());
            }
            List<ResultMapInfo> composites = columnEntityRelation.getComposites();
            for (ResultMapInfo composite : composites) {
                List<EntityInfo> childEntityInfoList = this.getEntityInfoList(composite);
                if (ObjectUtils.isNotEmpty(childEntityInfoList)) {
                    entityInfoList.addAll(childEntityInfoList);
                }
            }
            return entityInfoList;
        }

        protected String getResultMapId(List<EntityInfo> entityInfoList) {
            List<String> classNameList = entityInfoList.stream()
                    .map(entityInfo -> entityInfo.getClazzName().replaceAll("\\.", "_"))
                    .collect(Collectors.toList());
            return StringUtils.join(classNameList, "_join_") + "_ResultMap";
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
            for (EntityRelationTree childEntityRelationTree : entityRelationTree.getEntityRelationList()) {
                int level = childEntityRelationTree.getLevel();
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) childEntityRelationTree.getColumnInfo();
                EntityRelationSelectInfo nestedSelectInfo = this.buildNestedSelectInfo(resultMapInfo, childEntityRelationTree);

                // 子查询和join的第一级都无法生成join查询，第一级join会造成结果膨胀问题，第二级采用in查询或者批量查询，解决N+1问题，把N+1变成1+1
                FetchMode fetchMode = relationColumnInfo.getFetchMode();
                if (fetchMode == FetchMode.SELECT) {
                    entityRelationSelectInfoList.add(nestedSelectInfo);
                } else if (fetchMode == FetchMode.BATCH) {
                    // user     user role      role menu    menu resource    user detail
                    BatchRelationSelectInfo batchRelationSelectInfo = new BatchRelationSelectInfo();
                    batchRelationSelectInfo.setId(nestedSelectInfo.getId());
                    batchRelationSelectInfo.setResultMapId(nestedSelectInfo.getResultMapId());
                    batchRelationSelectInfo.setColumnInfo(nestedSelectInfo.getColumnInfo());
                    batchRelationSelectInfo.setEntityInfo(entityRelationTree.getEntityInfo());
                    batchRelationSelectInfo.setBatchRelationSelectInfo(nestedSelectInfo);
                    entityRelationSelectInfoList.add(batchRelationSelectInfo);
                } else if (fetchMode == FetchMode.JOIN) {
                    if (level == 1) {
                        throw new RuntimeException("解析错误");
                    }
                    if (level == 2) {
                        entityRelationSelectInfoList.add(nestedSelectInfo);
                    }
                    if (level > 2) {
                        parentEntityRelationSelectInfo.addEntityRelationSelectInfo(nestedSelectInfo);
                    }
                } else {
                    throw new RuntimeException("未知的抓取模式");
                }
                this.buildEntityRelationSelect(resultMapInfo, entityRelationSelectInfoList, childEntityRelationTree, nestedSelectInfo);
            }
        }

        public EntityRelationSelectInfo buildNestedSelectInfo(ResultMapInfo resultMapInfo, EntityRelationTree childEntityRelationTree) {
            ColumnInfo columnInfo = childEntityRelationTree.getColumnInfo();
            String nestedSelectId = this.getNestedSelectId(columnInfo.getJavaType(), columnInfo.getCollectionType());
            String resultMapId = this.getResultMapId(resultMapInfo, childEntityRelationTree);
            EntityRelationSelectInfo entityRelationSelectInfo = new EntityRelationSelectInfo();
            entityRelationSelectInfo.setId(nestedSelectId);
            entityRelationSelectInfo.setResultMapId(resultMapId);

            entityRelationSelectInfo.setColumnInfo(childEntityRelationTree.getColumnInfo());
            entityRelationSelectInfo.setMiddleEntityInfo(childEntityRelationTree.getMiddleEntityInfo());
            entityRelationSelectInfo.setEntityInfo(childEntityRelationTree.getEntityInfo());
            return entityRelationSelectInfo;
        }
    }

    public static class EntityRelationResultMap extends AbstractEntityRelation {

        public ResultMapContext buildResultMapInfo(EntityRelationTree entityRelationTree) {
            ResultMapContext resultMapContext = new ResultMapContext();
            ResultMapInfo resultMapInfo = this.buildAloneResultMapInfo(resultMapContext, entityRelationTree);

            resultMapContext.setResultMapInfo(resultMapInfo);
            resultMapContext.addRelationResultMap(resultMapInfo);

            resultMapInfo.setResultMapInfoList(this.buildRelationResultMapInfo(resultMapContext, entityRelationTree));

            List<ResultMapInfo> resultMapInfoList = resultMapContext.getResultMapInfoList();
            this.processResultMapInfo(resultMapInfoList);
            return resultMapContext;
        }

        /**
         * 构建结果集关联信息
         *
         * @param resultMapContext              结果集信息列表
         * @return
         */
        protected List<ResultMapInfo> buildRelationResultMapInfo(ResultMapContext resultMapContext, EntityRelationTree entityRelationTree) {
            List<ResultMapInfo> resultMapInfoList = new ArrayList();
            for (EntityRelationTree childEntityRelationTree : entityRelationTree.getEntityRelationList()) {
                int level = childEntityRelationTree.getLevel();
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) childEntityRelationTree.getColumnInfo();

                // 构建一个内嵌查询结果集
                ResultMapInfo nestedResultMapInfo = this.buildNestedSelectResultMapInfo(childEntityRelationTree, relationColumnInfo);
                resultMapInfoList.add(nestedResultMapInfo);

                FetchMode fetchMode = relationColumnInfo.getFetchMode();
                if (fetchMode == FetchMode.SELECT) {
                    this.buildSelectResultMapInfo(resultMapContext, childEntityRelationTree);
                } else if (fetchMode == FetchMode.BATCH) {
                    this.buildBatchResultMapInfo(resultMapContext, entityRelationTree, childEntityRelationTree);
                } else if (fetchMode == FetchMode.JOIN) {
                    if (level == 1) {
                        throw new RuntimeException("解析错误");
                    }
                    if (level == 2) {
                        this.buildBatchResultMapInfo(resultMapContext, entityRelationTree, childEntityRelationTree);
                    }
                    if (level > 2) {
                        this.buildJoinResultMapInfo(resultMapContext, nestedResultMapInfo, childEntityRelationTree);
                    }
                } else {
                    throw new RuntimeException("未知的抓取模式");
                }
            }
            return resultMapInfoList;
        }

        private ResultMapInfo buildNestedSelectResultMapInfo(EntityRelationTree entityRelationTree, RelationColumnInfo relationColumnInfo) {
            int level = entityRelationTree.getLevel();
            FetchMode fetchMode = relationColumnInfo.getFetchMode();
            if (fetchMode == FetchMode.JOIN && level > 2) {
                ResultMapInfo resultMapInfo = new ResultMapInfo();
                resultMapInfo.setColumnInfo(entityRelationTree.getColumnInfo());
                resultMapInfo.setMiddleEntityInfo(entityRelationTree.getMiddleEntityInfo());
                resultMapInfo.setEntityInfo(entityRelationTree.getEntityInfo());
                return resultMapInfo;
            } else {
                String nestedSelectId = this.getNestedSelectId(relationColumnInfo.getJavaType(), relationColumnInfo.getCollectionType());
                ResultMapInfo resultMapRelationInfo = new ResultMapInfo();
                resultMapRelationInfo.setNestedSelectId(nestedSelectId);
                resultMapRelationInfo.setColumnInfo(entityRelationTree.getColumnInfo());
                return resultMapRelationInfo;
            }
        }

        private ResultMapInfo buildSelectResultMapInfo(ResultMapContext resultMapContext, EntityRelationTree childEntityRelationTree) {
            ResultMapInfo resultMapInfo = this.buildAloneResultMapInfo(resultMapContext, childEntityRelationTree);
            resultMapContext.addRelationResultMap(resultMapInfo);

            resultMapInfo.setResultMapInfoList(this.buildRelationResultMapInfo(resultMapContext, childEntityRelationTree));
            return resultMapInfo;
        }

        private ResultMapInfo buildBatchResultMapInfo(ResultMapContext resultMapContext, EntityRelationTree parentRelationTree, EntityRelationTree childEntityRelationTree) {
            ResultMapInfo resultMapInfo = this.buildAloneResultMapInfo(resultMapContext, childEntityRelationTree);

            ResultMapInfo parentResultMapInfo = new ResultMapInfo();
            // parentResultMapInfo.setId(resultMapInfo.getId());
            parentResultMapInfo.setEntityInfo(parentRelationTree.getEntityInfo());
            parentResultMapInfo.setResultMapInfoList(Arrays.asList(resultMapInfo));

            resultMapContext.addRelationResultMap(parentResultMapInfo);

            resultMapInfo.setResultMapInfoList(this.buildRelationResultMapInfo(resultMapContext, childEntityRelationTree));

            return parentResultMapInfo;
        }

        private void buildJoinResultMapInfo(ResultMapContext resultMapContext, ResultMapInfo nestedResultMapInfo, EntityRelationTree childEntityRelationTree) {
            List<ResultMapInfo> childResultMapRelationInfoList = this.buildRelationResultMapInfo(resultMapContext, childEntityRelationTree);
            nestedResultMapInfo.setResultMapInfoList(childResultMapRelationInfoList);
        }

        private ResultMapInfo buildAloneResultMapInfo(ResultMapContext resultMapContext, EntityRelationTree childEntityRelationTree) {
            // String resultMapId = this.getResultMapId(null, childEntityRelationTree);
            ResultMapInfo resultMapInfo = new ResultMapInfo();
            // resultMapInfo.setId(resultMapId);
            resultMapInfo.setColumnInfo(childEntityRelationTree.getColumnInfo());
            resultMapInfo.setMiddleEntityInfo(childEntityRelationTree.getMiddleEntityInfo());
            resultMapInfo.setEntityInfo(childEntityRelationTree.getEntityInfo());
            return resultMapInfo;
        }

        protected void processResultMapInfo(List<ResultMapInfo> resultMapInfoList) {
            for (ResultMapInfo resultMapInfo : resultMapInfoList) {
                List<EntityInfo> entityInfoList = this.getEntityInfoList(resultMapInfo);
                String resultMapId = this.getResultMapId(entityInfoList);
                resultMapInfo.setId(resultMapId);
            }
        }
    }

    /**
     * 结果集上下文，生命周期仅仅在解析实体关系时有效
     * @author ccxuef
     * @date 2025/9/30 14:59
     */
    private static class ResultMapContext {

        private ResultMapInfo resultMapInfo;

        private List<ResultMapInfo> resultMapInfoList = new ArrayList();

        public ResultMapInfo getResultMapInfo() {
            return resultMapInfo;
        }

        public void setResultMapInfo(ResultMapInfo resultMapInfo) {
            this.resultMapInfo = resultMapInfo;
        }

        public List<ResultMapInfo> getResultMapInfoList() {
            return resultMapInfoList;
        }

        public void addRelationResultMap(ResultMapInfo resultMapInfo) {
            this.resultMapInfoList.add(resultMapInfo);
        }
    }
}
