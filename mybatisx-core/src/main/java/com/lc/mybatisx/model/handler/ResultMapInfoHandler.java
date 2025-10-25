package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.FetchMode;
import com.lc.mybatisx.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ResultMapInfoHandler extends BasicInfoHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultMapInfoHandler.class);

    private EntityRelationResultMap relationResultMap = new EntityRelationResultMap();

    public String execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        SqlCommandType sqlCommandType = methodInfo.getSqlCommandType();
        if (Arrays.asList(SqlCommandType.INSERT, SqlCommandType.DELETE, SqlCommandType.UPDATE).contains(sqlCommandType)) {
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

        mapperInfo.setResultMapInfoList(resultMapInfoList);
        return resultMapInfo.getId();
    }


    public static abstract class AbstractEntityRelation {

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

        protected String getNestedSelectId(List<EntityInfo> entityInfoList) {
            List<String> classNameList = entityInfoList.stream()
                    .map(entityInfo -> entityInfo.getClazzName().replaceAll("\\.", "_"))
                    .collect(Collectors.toList());
            return "nestedSelect_" + StringUtils.join(classNameList, "_join_");
        }
    }

    public static class EntityRelationResultMap extends AbstractEntityRelation {

        public ResultMapContext buildResultMapInfo(EntityRelationTree entityRelationTree) {
            ResultMapContext resultMapContext = new ResultMapContext();
            ResultMapInfo resultMapInfo = this.buildAloneResultMapInfo(entityRelationTree);

            resultMapContext.setResultMapInfo(resultMapInfo);
            resultMapContext.addRelationResultMap(resultMapInfo);

            resultMapInfo.setComposites(this.buildRelationResultMapInfo(resultMapContext, entityRelationTree));

            List<ResultMapInfo> resultMapInfoList = resultMapContext.getResultMapInfoList();
            this.processResultMapInfo(resultMapInfoList);
            return resultMapContext;
        }

        /**
         * 构建结果集关联信息
         *
         * @param resultMapContext 结果集信息列表
         * @return
         */
        protected List<ResultMapInfo> buildRelationResultMapInfo(ResultMapContext resultMapContext, EntityRelationTree entityRelationTree) {
            List<ResultMapInfo> resultMapInfoList = new ArrayList();
            for (EntityRelationTree childEntityRelationTree : entityRelationTree.getComposites()) {
                int level = childEntityRelationTree.getLevel();
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) childEntityRelationTree.getColumnInfo();

                // 构建一个内嵌查询结果集
                ResultMapInfo nestedResultMapInfo = this.buildNestedSelectResultMapInfo(childEntityRelationTree, relationColumnInfo);
                resultMapInfoList.add(nestedResultMapInfo);

                FetchMode fetchMode = relationColumnInfo.getFetchMode();
                if (fetchMode == FetchMode.SIMPLE) {
                    ResultMapInfo resultMapInfo = this.buildSimpleNestedResultMapInfo(resultMapContext, childEntityRelationTree);
                    resultMapInfo.setNestedSelect(nestedResultMapInfo.getNestedSelect());
                } else if (fetchMode == FetchMode.BATCH) {
                    ResultMapInfo resultMapInfo = this.buildBatchNestedResultMapInfo(resultMapContext, entityRelationTree, childEntityRelationTree);
                    resultMapInfo.setNestedSelect(nestedResultMapInfo.getNestedSelect());
                } else if (fetchMode == FetchMode.JOIN) {
                    if (level == 1) {
                        throw new RuntimeException("解析错误");
                    }
                    if (level == 2) {
                        ResultMapInfo resultMapInfo = this.buildBatchNestedResultMapInfo(resultMapContext, entityRelationTree, childEntityRelationTree);
                        resultMapInfo.setNestedSelect(nestedResultMapInfo.getNestedSelect());
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
                ResultMapInfo resultMapInfo = new ResultMapInfo();
                resultMapInfo.setColumnInfo(entityRelationTree.getColumnInfo());
                resultMapInfo.setNestedSelect(new ResultMapInfo.NestedSelect());
                return resultMapInfo;
            }
        }

        private ResultMapInfo buildSimpleNestedResultMapInfo(ResultMapContext resultMapContext, EntityRelationTree childEntityRelationTree) {
            ResultMapInfo resultMapInfo = new SimpleNestedResultMapInfo();
            resultMapInfo.setColumnInfo(childEntityRelationTree.getColumnInfo());
            resultMapInfo.setMiddleEntityInfo(childEntityRelationTree.getMiddleEntityInfo());
            resultMapInfo.setEntityInfo(childEntityRelationTree.getEntityInfo());

            resultMapContext.addRelationResultMap(resultMapInfo);

            resultMapInfo.setComposites(this.buildRelationResultMapInfo(resultMapContext, childEntityRelationTree));
            return resultMapInfo;
        }

        private ResultMapInfo buildBatchNestedResultMapInfo(ResultMapContext resultMapContext, EntityRelationTree parentRelationTree, EntityRelationTree childEntityRelationTree) {
            ResultMapInfo resultMapInfo = this.buildAloneResultMapInfo(childEntityRelationTree);

            ResultMapInfo parentResultMapInfo = new BatchNestedResultMapInfo();
            parentResultMapInfo.setEntityInfo(parentRelationTree.getEntityInfo());
            parentResultMapInfo.setComposites(Arrays.asList(resultMapInfo));
            resultMapContext.addRelationResultMap(parentResultMapInfo);

            resultMapInfo.setComposites(this.buildRelationResultMapInfo(resultMapContext, childEntityRelationTree));

            return parentResultMapInfo;
        }

        private void buildJoinResultMapInfo(ResultMapContext resultMapContext, ResultMapInfo nestedResultMapInfo, EntityRelationTree childEntityRelationTree) {
            List<ResultMapInfo> childResultMapRelationInfoList = this.buildRelationResultMapInfo(resultMapContext, childEntityRelationTree);
            nestedResultMapInfo.setComposites(childResultMapRelationInfoList);
        }

        private ResultMapInfo buildAloneResultMapInfo(EntityRelationTree childEntityRelationTree) {
            ResultMapInfo resultMapInfo = new ResultMapInfo();
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

                ResultMapInfo.NestedSelect nestedSelect = resultMapInfo.getNestedSelect();
                if (nestedSelect != null) {
                    String nestedSelectId = this.getNestedSelectId(entityInfoList);
                    nestedSelect.setId(nestedSelectId);
                }
            }
        }
    }

    /**
     * 结果集上下文，生命周期仅仅在解析实体关系时有效
     *
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
