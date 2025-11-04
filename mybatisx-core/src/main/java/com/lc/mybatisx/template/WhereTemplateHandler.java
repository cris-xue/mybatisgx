package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.TypeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.*;

public class WhereTemplateHandler {

    public void execute(Element parentElement, EntityInfo entityInfo, MethodInfo methodInfo) {
        ConditionProcessorFactory factory = new ConditionProcessorFactory();
        Element whereElement = parentElement.addElement("where");
        this.handleConditionGroup(methodInfo, whereElement, methodInfo.getConditionInfoList(), factory);
        this.addOptimisticLockCondition(entityInfo, methodInfo, whereElement);
        this.addLogicDeleteCondition(entityInfo, whereElement);
    }

    private void addOptimisticLockCondition(EntityInfo entityInfo, MethodInfo methodInfo, Element whereElement) {
        // 查询不需要乐观锁版本条件
        ColumnInfo lockColumnInfo = entityInfo.getLockColumnInfo();
        if (lockColumnInfo != null) {
            // 只有更新的场景才需要乐观锁，逻辑删除不需要乐观锁，因为逻辑删除直接改变逻辑删除字段，因为不管什么操作都一定需要逻辑删除字段
            if (methodInfo.getSqlCommandType() == SqlCommandType.UPDATE) {
                whereElement.addText(String.format(" and %s = #{%s}", lockColumnInfo.getDbColumnName(), lockColumnInfo.getJavaColumnName()));
            }
        }
    }

    private void addLogicDeleteCondition(EntityInfo entityInfo, Element whereElement) {
        // 逻辑删除
        ColumnInfo logicDeleteColumnInfo = entityInfo.getLogicDeleteColumnInfo();
        if (logicDeleteColumnInfo != null) {
            LogicDelete logicDelete = logicDeleteColumnInfo.getLogicDelete();
            whereElement.addText(String.format(" and %s = '%s'", logicDeleteColumnInfo.getDbColumnName(), logicDelete.show()));
        }
    }

    /**
     * 处理条件组
     *
     * @param methodInfo
     * @param whereElement
     * @param conditionInfoList
     */
    private void handleConditionGroup(MethodInfo methodInfo, Element whereElement, List<ConditionInfo> conditionInfoList, ConditionProcessorFactory factory) {
        for (ConditionInfo conditionInfo : conditionInfoList) {
            ConditionGroupInfo conditionGroupInfo = conditionInfo.getConditionGroupInfo();
            if (conditionGroupInfo != null) {
                // 处理分组的括号
                whereElement.addText(String.format(" %s %s", conditionInfo.getLogicOperator(), conditionInfo.getLeftBracket()));
                this.handleConditionGroup(methodInfo, whereElement, conditionGroupInfo.getConditionInfoList(), factory);
                whereElement.addText(conditionInfo.getRightBracket());
            } else {
                ColumnInfo columnInfo = conditionInfo.getColumnInfo();
                LogicDelete logicDelete = columnInfo.getLogicDelete();
                if (logicDelete != null) {
                    continue;
                }
                factory.process(methodInfo, conditionInfo, whereElement);
            }
        }
    }

    static class ConditionProcessorFactory {

        protected final Map<String, ConditionHandler> conditionHandlers = new HashMap<>();

        public ConditionProcessorFactory() {
            conditionHandlers.put("like", new LikeConditionHandler());
            conditionHandlers.put("in", new InConditionHandler());
            conditionHandlers.put("between", new BetweenConditionHandler());
            conditionHandlers.put("default", new CommonConditionHandler());
        }

        public void process(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            ConditionHandler conditionHandler = conditionHandlers.getOrDefault(conditionInfo.getComparisonOperator().getValue(), conditionHandlers.get("default"));
            conditionHandler.handle(methodInfo, conditionInfo, whereElement);
        }
    }

    interface ConditionHandler {

        void handle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement);

        WhereItemContext handleBasicTypeSingleParam();

        WhereItemContext handleObjectTypeNoAnnotationSingleParam(ColumnInfo columnInfo);

        WhereItemContext handleObjectTypeNoAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite);

        WhereItemContext handleObjectTypeWithAnnotationSingleParam(ColumnInfo columnInfo);

        WhereItemContext handleObjectTypeWithAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite);

        WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo);


        WhereItemContext handleBasicTypeMultiParam();

        WhereItemContext handleObjectTypeNoAnnotationMultiParam(ColumnInfo columnInfo);

        WhereItemContext handleObjectTypeNoAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite);

        WhereItemContext handleObjectTypeWithAnnotationMultiParam(ColumnInfo columnInfo);

        WhereItemContext handleObjectTypeWithAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite);

        WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo);
    }

    static abstract class AbstractConditionHandler implements ConditionHandler {

        protected ConditionInfo conditionInfo;
        protected ColumnInfo columnInfo;
        protected MethodParamInfo methodParamInfo;
        protected LogicOperator logicOperator;
        protected ComparisonOperator comparisonOperator;

        @Override
        public void handle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            this.conditionInfo = conditionInfo;
            this.columnInfo = conditionInfo.getColumnInfo();
            this.methodParamInfo = conditionInfo.getMethodParamInfo();
            this.logicOperator = conditionInfo.getLogicOperator();
            this.comparisonOperator = conditionInfo.getComparisonOperator();

            List<WhereItemContext> whereItemContextList;
            int paramCount = methodInfo.getMethodParamInfoList().size();
            if (paramCount == 1) {
                whereItemContextList = this.singleParamHandle();
            } else {
                whereItemContextList = this.multiParamHandle();
            }
            this.processWhereItem(whereElement, methodInfo.getDynamic(), whereItemContextList);
        }

        /**
         * 处理条件，有特殊情况的可以在子类中重写
         * @param whereElement
         * @param dynamic
         * @param whereItemContextList
         */
        protected void processWhereItem(Element whereElement, Boolean dynamic, List<WhereItemContext> whereItemContextList) {
            if (ObjectUtils.isEmpty(whereItemContextList)) {
                return;
            }
            for (WhereItemContext whereItemContext : whereItemContextList) {
                String testExpression = whereItemContext.getTestExpression();
                Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
                for (Object content : whereItemContext.getContentList()) {
                    if (content instanceof String) {
                        whereOrIfElement.addText((String) content);
                    }
                    if (content instanceof Element) {
                        whereOrIfElement.add((Element) content);
                    }
                }
            }
        }

        public List<WhereItemContext> singleParamHandle() {
            List<WhereItemContext> whereItemContextList = new ArrayList<>();
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                if (methodParamInfo.getBasicType()) {
                    // findById(Long id) findById(@Param("id") Long id)
                    WhereItemContext whereItemContext = this.handleBasicTypeSingleParam();
                    whereItemContextList.add(whereItemContext);
                } else {
                    // findById(MultiId id) findById(@Param("id") MultiId id)
                    if (methodParamInfo.getParam() == null) {
                        if (conditionInfo.getConditionOriginType() == ConditionOriginType.ENTITY_FIELD) {
                            this.processSingleParamNoAnnotation(whereItemContextList, columnInfo);
                        } else {
                            for (ColumnInfo columnInfo : methodParamInfo.getColumnInfoList()) {
                                this.processSingleParamNoAnnotation(whereItemContextList, columnInfo);
                            }
                        }
                    } else {
                        if (conditionInfo.getConditionOriginType() == ConditionOriginType.ENTITY_FIELD) {
                            this.processSingleParamWithAnnotation(whereItemContextList, columnInfo);
                        } else {
                            for (ColumnInfo columnInfo : methodParamInfo.getColumnInfoList()) {
                                this.processSingleParamWithAnnotation(whereItemContextList, columnInfo);
                            }
                        }
                    }
                }
            }
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                if (relationColumnInfo.getMappedByRelationColumnInfo() == null) {
                    for (ForeignKeyColumnInfo foreignKeyInfo : relationColumnInfo.getInverseForeignKeyColumnInfoList()) {
                        WhereItemContext whereItemContext = this.handleRelationColumnSingleParam(relationColumnInfo, foreignKeyInfo);
                        whereItemContextList.add(whereItemContext);
                    }
                }
            }
            return whereItemContextList;
        }

        private void processSingleParamNoAnnotation(List<WhereItemContext> whereItemContextList, ColumnInfo columnInfo) {
            List<ColumnInfo> columnInfoComposites = columnInfo.getComposites();
            if (ObjectUtils.isEmpty(columnInfoComposites)) {
                WhereItemContext whereItemContext = this.handleObjectTypeNoAnnotationSingleParam(columnInfo);
                whereItemContextList.add(whereItemContext);
            } else {
                for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                    WhereItemContext whereItemContext = this.handleObjectTypeNoAnnotationSingleParam(columnInfo, columnInfoComposite);
                    whereItemContextList.add(whereItemContext);
                }
            }
        }

        private void processSingleParamWithAnnotation(List<WhereItemContext> whereItemContextList, ColumnInfo columnInfo) {
            List<ColumnInfo> columnInfoComposites = columnInfo.getComposites();
            if (ObjectUtils.isEmpty(columnInfoComposites)) {
                WhereItemContext whereItemContext = this.handleObjectTypeWithAnnotationSingleParam(columnInfo);
                whereItemContextList.add(whereItemContext);
            } else {
                for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                    WhereItemContext whereItemContext = this.handleObjectTypeWithAnnotationSingleParam(columnInfo, columnInfoComposite);
                    whereItemContextList.add(whereItemContext);
                }
            }
        }

        public List<WhereItemContext> multiParamHandle() {
            List<WhereItemContext> whereItemContextList = new ArrayList();
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                if (methodParamInfo.getBasicType()) {
                    // findById(Long id) findById(@Param("id") Long id)
                    WhereItemContext whereItemContext = this.handleBasicTypeMultiParam();
                    whereItemContextList.add(whereItemContext);
                } else {
                    // findById(MultiId id) findById(@Param("id") MultiId id)
                    if (methodParamInfo.getParam() == null) {
                        if (conditionInfo.getConditionOriginType() == ConditionOriginType.ENTITY_FIELD) {
                            this.processMultiParamNoAnnotation(whereItemContextList, columnInfo);
                        } else {
                            for (ColumnInfo columnInfo : methodParamInfo.getColumnInfoList()) {
                                this.processMultiParamNoAnnotation(whereItemContextList, columnInfo);
                            }
                        }
                    } else {
                        if (conditionInfo.getConditionOriginType() == ConditionOriginType.ENTITY_FIELD) {
                            this.processMultiParamWithAnnotation(whereItemContextList, columnInfo);
                        } else {
                            for (ColumnInfo columnInfo : methodParamInfo.getColumnInfoList()) {
                                this.processMultiParamWithAnnotation(whereItemContextList, columnInfo);
                            }
                        }
                    }
                }
            }
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                if (relationColumnInfo.getMappedByRelationColumnInfo() == null) {
                    for (ForeignKeyColumnInfo foreignKeyInfo : relationColumnInfo.getInverseForeignKeyColumnInfoList()) {
                        WhereItemContext whereItemContext = this.handleRelationColumnMultiParam(relationColumnInfo, foreignKeyInfo);
                        whereItemContextList.add(whereItemContext);
                    }
                }
            }
            return whereItemContextList;
        }

        private void processMultiParamNoAnnotation(List<WhereItemContext> whereItemContextList, ColumnInfo columnInfo) {
            List<ColumnInfo> columnInfoComposites = columnInfo.getComposites();
            if (ObjectUtils.isEmpty(columnInfoComposites)) {
                WhereItemContext whereItemContext = this.handleObjectTypeNoAnnotationMultiParam(columnInfo);
                whereItemContextList.add(whereItemContext);
            } else {
                for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                    WhereItemContext whereItemContext = this.handleObjectTypeNoAnnotationMultiParam(columnInfo, columnInfoComposite);
                    whereItemContextList.add(whereItemContext);
                }
            }
        }

        private void processMultiParamWithAnnotation(List<WhereItemContext> whereItemContextList, ColumnInfo columnInfo) {
            List<ColumnInfo> columnInfoComposites = columnInfo.getComposites();
            if (ObjectUtils.isEmpty(columnInfoComposites)) {
                WhereItemContext whereItemContext = this.handleObjectTypeWithAnnotationMultiParam(columnInfo);
                whereItemContextList.add(whereItemContext);
            } else {
                for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                    WhereItemContext whereItemContext = this.handleObjectTypeWithAnnotationMultiParam(columnInfo, columnInfoComposite);
                    whereItemContextList.add(whereItemContext);
                }
            }
        }

        protected String getTestExpression(String... paths) {
            if (paths.length == 1) {
                return String.format("%1$s != null", paths);
            }
            if (paths.length == 2) {
                return String.format("%1$s != null and %1$s.%2$s != null", paths);
            }
            if (paths.length == 3) {
                return String.format("%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null", paths);
            }
            return "";
        }

        /**
         *
         * @param logicOperator
         * @param comparisonOperator
         * @param columnInfo
         * @param paramValueExpression #{userId} or #{user.userId}
         * @return and user_id = #{userId} or and user_id = #{user.userId}
         */
        protected String getConditionExpression(LogicOperator logicOperator, ComparisonOperator comparisonOperator, ColumnInfo columnInfo, String paramValueExpression) {
            return String.format(
                    " %1$s %2$s %3$s %4$s",
                    logicOperator.getValue(),
                    columnInfo.getDbColumnName(),
                    comparisonOperator.getValue(),
                    paramValueExpression
            );
        }

        protected Element buildWhereOrIfElement(Element whereElement, Boolean dynamic, String testExpression) {
            if (dynamic) {
                Element ifElement = whereElement.addElement("if");
                ifElement.addAttribute("test", testExpression);
                return ifElement;
            }
            return whereElement;
        }

        protected Element buildBindElement(String name, String value) {
            Element bindElement = DocumentHelper.createElement("bind");
            bindElement.addAttribute("name", name);
            bindElement.addAttribute("value", value);
            return bindElement;
        }
    }

    /**
     *                           <if test="@org.apache.commons.lang3.StringUtils@isNotBlank(likeClientCode)">
     *                               <bind name="likeClientCode" value="'%' + likeClientCode + '%'"/>
     *                               and act.client_code like #{likeClientCode}
     *                           </if>
     * @author ccxuef
     * @date 2025/10/28 18:39
     */
    static class LikeConditionHandler extends AbstractConditionHandler {

        @Override
        public WhereItemContext handleBasicTypeSingleParam() {
            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            String bindValuePath = String.format("%1$s", methodParamInfo.getArgName());
            Element likeBindElement = this.buildLikeBindElement(bindValuePath);
            String paramValueExpression = String.format("#{%1$s}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationSingleParam(ColumnInfo columnInfo) {
            String testExpression = String.format("%1$s != null", conditionInfo.getColumnName());
            String bindValuePath = String.format("%1$s", conditionInfo.getColumnName());
            Element likeBindElement = this.buildLikeBindElement(bindValuePath);
            String paramValueExpression = String.format("#{%1$s}", conditionInfo.getColumnName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String bindValuePath = String.format(
                    "%1$s.%2$s",
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            Element likeBindElement = this.buildLikeBindElement(bindValuePath);
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s}",
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationSingleParam(ColumnInfo columnInfo) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String bindValuePath = String.format(
                    "%1$s.%2$s",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            Element likeBindElement = this.buildLikeBindElement(bindValuePath);
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String bindValuePath = String.format(
                    "%1$s.%2$s.%3$s",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            Element likeBindElement = this.buildLikeBindElement(bindValuePath);
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s.%3$s}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        @Override
        public WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String bindValuePath = String.format(
                    "%1$s.%2$s",
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            Element likeBindElement = this.buildLikeBindElement(bindValuePath);
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s}",
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        @Override
        public WhereItemContext handleBasicTypeMultiParam() {
            /*String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            String bindValuePath = String.format("%1$s", methodParamInfo.getArgName());
            Element likeBindElement = this.buildLikeBindElement(bindValuePath);
            String paramValueExpression = String.format("#{%1$s}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);*/
            return this.handleBasicTypeSingleParam();
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationMultiParam(ColumnInfo columnInfo) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    methodParamInfo.getArgName(),
                    conditionInfo.getColumnName()
            );
            String bindValuePath = String.format(
                    "%1$s.%2$s",
                    methodParamInfo.getArgName(),
                    conditionInfo.getColumnName()
            );
            Element likeBindElement = this.buildLikeBindElement(bindValuePath);
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s}",
                    methodParamInfo.getArgName(),
                    conditionInfo.getColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String bindValuePath = String.format(
                    "%1$s.%2$s.%3$s",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            Element likeBindElement = this.buildLikeBindElement(bindValuePath);
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s.%3$s}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationMultiParam(ColumnInfo columnInfo) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String bindValuePath = String.format(
                    "%1$s.%2$s",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            Element likeBindElement = this.buildLikeBindElement(bindValuePath);
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String bindValuePath = String.format(
                    "%1$s.%2$s.%3$s",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            Element likeBindElement = this.buildLikeBindElement(bindValuePath);
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s.%3$s}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        @Override
        public WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String bindValuePath = String.format(
                    "%1$s.%2$s.%3$s",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            Element likeBindElement = this.buildLikeBindElement(bindValuePath);
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s.%3$s}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        private Element buildLikeBindElement(String bindValuePath) {
            String bindValue = "'%'+" + bindValuePath + "+'%'";
            return this.buildBindElement(bindValuePath, bindValue);
        }
    }

    /**
     *                           <if test="terminal == @com.iss.dtg.idms.constant.Terminal@EBANK">
     *                               and act.client_code in
     *                               <foreach item="item" index="index" collection="unDraftClientCodeList" open="(" separator="," close=")">
     *                                   #{item}
     *               				</foreach>
     *                           </if>
     * @author ccxuef
     * @date 2025/10/28 18:41
     */
    static class InConditionHandler extends AbstractConditionHandler {

        @Override
        public WhereItemContext handleBasicTypeSingleParam() {
            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            String paramValueExpression = String.format("%1$s", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, "");
            Element foreachElement = this.buildForeachElement(paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement));
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationSingleParam(ColumnInfo columnInfo) {
            String testExpression = String.format("%1$s != null", conditionInfo.getColumnName());
            String paramValueExpression = String.format("%1$s", conditionInfo.getColumnName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, "");
            Element foreachElement = this.buildForeachElement(paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement));
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "%1$s.%2$s",
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, "");
            Element foreachElement = this.buildForeachElement(paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationSingleParam(ColumnInfo columnInfo) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "%1$s.%2$s",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, "");
            Element foreachElement = this.buildForeachElement(paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "%1$s.%2$s.%3$s",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, "");
            Element foreachElement = this.buildForeachElement(paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement));
        }

        @Override
        public WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "%1$s.%2$s",
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, "");
            Element foreachElement = this.buildForeachElement(paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement));
        }

        @Override
        public WhereItemContext handleBasicTypeMultiParam() {
            /*String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            String paramValueExpression = String.format("%1$s", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, "");
            Element foreachElement = this.buildForeachElement(paramValueExpression);*/
            return this.handleBasicTypeSingleParam();
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationMultiParam(ColumnInfo columnInfo) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    methodParamInfo.getArgName(),
                    conditionInfo.getColumnName()
            );
            String paramValueExpression = String.format(
                    "%1$s.%2$s",
                    methodParamInfo.getArgName(),
                    conditionInfo.getColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, "");
            Element foreachElement = this.buildForeachElement(paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement));
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "%1$s.%2$s.%3$s",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, "");
            Element foreachElement = this.buildForeachElement(paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationMultiParam(ColumnInfo columnInfo) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "%1$s.%2$s",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, "");
            Element foreachElement = this.buildForeachElement(paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "%1$s.%2$s.%3$s",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, "");
            Element foreachElement = this.buildForeachElement(paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement));
        }

        @Override
        public WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "%1$s.%2$s.%3$s",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, "");
            Element foreachElement = this.buildForeachElement(paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement));
        }

        private Element buildForeachElement(String collection) {
            Element foreachElement = DocumentHelper.createElement("foreach");
            foreachElement.addAttribute("index", "index");
            foreachElement.addAttribute("item", "item");
            foreachElement.addAttribute("collection", collection);
            foreachElement.addAttribute("open", "(");
            foreachElement.addAttribute("close", ")");
            foreachElement.addAttribute("separator", ",");
            foreachElement.addText("#{item}");
            return foreachElement;
        }
    }

    static class BetweenConditionHandler extends AbstractConditionHandler {

        @Override
        public WhereItemContext handleBasicTypeSingleParam() {
            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            String paramValueExpression = String.format("#{%1$s[0]} and #{%1$s[1]}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationSingleParam(ColumnInfo columnInfo) {
            String testExpression = String.format("%1$s != null", conditionInfo.getColumnName());
            String paramValueExpression = String.format("#{%1$s[0]} and #{%1$s[1]}", conditionInfo.getColumnName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s[0]} and #{%1$s.%2$s[1]}",
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationSingleParam(ColumnInfo columnInfo) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s[0]} and #{%1$s.%2$s[1]}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s.%3$s[0]} and #{%1$s.%2$s.%3$s[1]}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s[0]} and #{%1$s.%2$s[1]}",
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleBasicTypeMultiParam() {
            /*String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            String paramValueExpression = String.format("#{%1$s[0]} and #{%1$s[1]}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);*/
            return this.handleBasicTypeSingleParam();
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationMultiParam(ColumnInfo columnInfo) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s[0]} and #{%1$s.%2$s[1]}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s.%3$s[0]} and #{%1$s.%2$s.%3$s[1]}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationMultiParam(ColumnInfo columnInfo) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s[0]} and #{%1$s.%2$s[1]}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s.%3$s[0]} and #{%1$s.%2$s.%3$s[1]}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s.%3$s[0]} and #{%1$s.%2$s.%3$s[1]}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }
    }

    static class CommonConditionHandler extends AbstractConditionHandler {

        @Override
        public WhereItemContext handleBasicTypeSingleParam() {
            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            String paramValueExpression = String.format("#{%1$s}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationSingleParam(ColumnInfo columnInfo) {
            String testExpression = String.format("%1$s != null", conditionInfo.getColumnName());
            String paramValueExpression = String.format("#{%1$s}", conditionInfo.getColumnName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s}",
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationSingleParam(ColumnInfo columnInfo) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s.%3$s}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s}",
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleBasicTypeMultiParam() {
            /*String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            String paramValueExpression = String.format("#{%1$s}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);*/
            return this.handleBasicTypeSingleParam();
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationMultiParam(ColumnInfo columnInfo) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeNoAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s.%3$s}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationMultiParam(ColumnInfo columnInfo) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleObjectTypeWithAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s.%3$s}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    columnInfoComposite.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s.%3$s}",
                    methodParamInfo.getArgName(),
                    columnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }
    }

    static class WhereItemContext {

        private String testExpression;

        private List<Object> contentList;

        public WhereItemContext(String testExpression, List<Object> contentList) {
            this.testExpression = testExpression;
            this.contentList = contentList;
        }

        public String getTestExpression() {
            return testExpression;
        }

        public List<Object> getContentList() {
            return contentList;
        }
    }
}
