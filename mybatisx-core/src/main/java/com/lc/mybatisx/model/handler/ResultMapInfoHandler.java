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
        EntityRelationTree entityRelationTree = mapperInfo.getEntityRelationTree(resultClass);

        // 处理结果集
        List<ResultMapInfo> resultMapInfoList = new ArrayList();
        ResultMapInfo resultMapInfo = relationResultMap.buildResultMapInfo(resultMapInfoList, entityRelationTree);
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

        protected String getResultMapId(String className) {
            return String.format("%s_ResultMap", className.replaceAll("\\.", "_"));
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
            List<EntityRelationTree> childEntityRelationTreeList = entityRelationTree.getEntityRelationList();
            for (EntityRelationTree childEntityRelationTree : childEntityRelationTreeList) {
                int level = childEntityRelationTree.getLevel();
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) childEntityRelationTree.getColumnInfo();
                EntityRelationSelectInfo nestedSelectInfo = this.buildNestedSelectInfo(resultMapInfo, childEntityRelationTree);

                // 子查询和join的第一级都无法生成join查询，第一级join会造成结果膨胀问题，第二级采用in查询或者批量查询，解决N+1问题，把N+1变成1+1
                FetchMode fetchMode = relationColumnInfo.getFetchMode();
                if (fetchMode == FetchMode.SELECT) {
                    entityRelationSelectInfoList.add(nestedSelectInfo);
                } else if (fetchMode == FetchMode.BATCH) {
                    // user     user role      role menu    menu resource    user detail
                    EntityRelationSelectInfo entityRelationSelectInfo = new EntityRelationSelectInfo();
                    entityRelationSelectInfo.setId(nestedSelectInfo.getId());
                    entityRelationSelectInfo.setResultMapId(nestedSelectInfo.getResultMapId());
                    entityRelationSelectInfo.setColumnInfo(nestedSelectInfo.getColumnInfo());
                    entityRelationSelectInfo.setEntityInfo(entityRelationTree.getEntityInfo());
                    entityRelationSelectInfo.setEntityRelationSelectInfoList(Arrays.asList(nestedSelectInfo));
                    entityRelationSelectInfoList.add(entityRelationSelectInfo);
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

        public ResultMapInfo buildResultMapInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationTree entityRelationTree) {
            String resultMapId = this.getResultMapId(null, entityRelationTree);
            ResultMapInfo resultMapInfo = new ResultMapInfo();
            resultMapInfo.setId(resultMapId);
            resultMapInfo.setColumnInfo(entityRelationTree.getColumnInfo());
            resultMapInfo.setMiddleEntityInfo(entityRelationTree.getMiddleEntityInfo());
            resultMapInfo.setEntityInfo(entityRelationTree.getEntityInfo());

            resultMapInfoList.add(resultMapInfo);

            List<ResultMapInfo> resultMapRelationInfoList = this.buildResultMapRelationInfo(resultMapInfoList, entityRelationTree);
            resultMapInfo.setResultMapInfoList(resultMapRelationInfoList);

            return resultMapInfo;
        }

        /**
         * 构建结果集关联信息
         *
         * @param resultMapInfoList              结果集信息列表
         * @return
         */
        protected List<ResultMapInfo> buildResultMapRelationInfo(List<ResultMapInfo> resultMapInfoList, EntityRelationTree entityRelationTree) {
            List<EntityRelationTree> childrenEntityRelationTreeList = entityRelationTree.getEntityRelationList();
            List<ResultMapInfo> resultMapRelationInfoList = new ArrayList();
            for (EntityRelationTree childrenEntityRelationTree : childrenEntityRelationTreeList) {
                int level = childrenEntityRelationTree.getLevel();
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) childrenEntityRelationTree.getColumnInfo();

                // 构建一个内嵌查询结果集
                ResultMapInfo nestedResultMapInfo = this.buildNestedSelectResultMapInfo(childrenEntityRelationTree, relationColumnInfo);
                resultMapRelationInfoList.add(nestedResultMapInfo);

                FetchMode fetchMode = relationColumnInfo.getFetchMode();
                if (fetchMode == FetchMode.SELECT) {
                    ResultMapInfo resultMapInfo = this.buildOneToOneResultMapInfo(childrenEntityRelationTree);
                    List<ResultMapInfo> resultMapRelationInfoList111 = this.buildResultMapRelationInfo(resultMapInfoList, childrenEntityRelationTree);
                    resultMapInfo.setResultMapInfoList(resultMapRelationInfoList111);

                    resultMapInfoList.add(resultMapInfo);
                } else if (fetchMode == FetchMode.BATCH) {
                    // user     user role      role menu    menu resource    user detail
                    ResultMapInfo resultMapInfo = this.buildOneToOneResultMapInfo(childrenEntityRelationTree);

                    List<ResultMapInfo> resultMapRelationInfoList111 = this.buildResultMapRelationInfo(resultMapInfoList, childrenEntityRelationTree);
                    resultMapInfo.setResultMapInfoList(resultMapRelationInfoList111);

                    ResultMapInfo parentResultMapInfo = new ResultMapInfo();
                    parentResultMapInfo.setId(resultMapInfo.getId());
                    parentResultMapInfo.setEntityInfo(entityRelationTree.getEntityInfo());
                    parentResultMapInfo.setResultMapInfoList(Arrays.asList(resultMapInfo));

                    resultMapInfoList.add(parentResultMapInfo);
                } else if (fetchMode == FetchMode.JOIN) {
                    if (level <= 2) {
                        ResultMapInfo resultMapInfo = this.buildOneToOneResultMapInfo(childrenEntityRelationTree);
                        List<ResultMapInfo> resultMapRelationInfoList111 = this.buildResultMapRelationInfo(resultMapInfoList, childrenEntityRelationTree);
                        resultMapInfo.setResultMapInfoList(resultMapRelationInfoList111);

                        resultMapInfoList.add(resultMapInfo);
                    }
                    if (level > 2) {
                        List<ResultMapInfo> childResultMapRelationInfoList = this.buildResultMapRelationInfo(resultMapInfoList, childrenEntityRelationTree);
                        nestedResultMapInfo.setResultMapInfoList(childResultMapRelationInfoList);
                    }
                } else {
                    throw new RuntimeException("未知的抓取模式");
                }
            }
            return resultMapRelationInfoList;
        }

        public ResultMapInfo buildOneToOneResultMapInfo(EntityRelationTree entityRelationTree) {
            String resultMapId = this.getResultMapId(null, entityRelationTree);
            ResultMapInfo resultMapInfo = new ResultMapInfo();
            resultMapInfo.setId(resultMapId);
            resultMapInfo.setColumnInfo(entityRelationTree.getColumnInfo());
            resultMapInfo.setMiddleEntityInfo(entityRelationTree.getMiddleEntityInfo());
            resultMapInfo.setEntityInfo(entityRelationTree.getEntityInfo());
            return resultMapInfo;
        }

        private ResultMapInfo buildNestedSelectResultMapInfo(EntityRelationTree entityRelationTree, RelationColumnInfo relationColumnInfo) {
            String nestedSelectId = this.getNestedSelectId(relationColumnInfo.getJavaType(), relationColumnInfo.getCollectionType());
            ResultMapInfo resultMapRelationInfo = new ResultMapInfo();
            resultMapRelationInfo.setNestedSelectId(nestedSelectId);
            resultMapRelationInfo.setColumnInfo(entityRelationTree.getColumnInfo());
            return resultMapRelationInfo;
        }
    }
}
