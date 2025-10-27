package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.TypeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WhereTemplateHandler {

    private final ConditionProcessorFactory conditionProcessorFactory = new ConditionProcessorFactory();

    public void execute(Element parentElement, EntityInfo entityInfo, MethodInfo methodInfo) {
        this.execute(parentElement, entityInfo, methodInfo, methodInfo.getConditionInfoList());
    }

    public void execute(Element parentElement, EntityInfo entityInfo, MethodInfo methodInfo, List<ConditionInfo> conditionInfoList) {
        Element whereElement = parentElement.addElement("where");
        this.handleConditionGroup(methodInfo, whereElement, conditionInfoList);
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
    private void handleConditionGroup(MethodInfo methodInfo, Element whereElement, List<ConditionInfo> conditionInfoList) {
        for (ConditionInfo conditionInfo : conditionInfoList) {
            ConditionGroupInfo conditionGroupInfo = conditionInfo.getConditionGroupInfo();
            if (conditionGroupInfo != null) {
                // 处理分组的括号
                whereElement.addText(String.format(" %s %s", conditionInfo.getLogicOperator(), conditionInfo.getLeftBracket()));
                this.handleConditionGroup(methodInfo, whereElement, conditionGroupInfo.getConditionInfoList());
                whereElement.addText(conditionInfo.getRightBracket());
            } else {
                ColumnInfo columnInfo = conditionInfo.getColumnInfo();
                LogicDelete logicDelete = columnInfo.getLogicDelete();
                if (logicDelete != null) {
                    continue;
                }
                conditionProcessorFactory.process(methodInfo, conditionInfo, whereElement);
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

        void singleParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement);

        void multiParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement);

        void handleBasicTypeSingleParam(Element whereElement, Boolean dynamic);

        void handleObjectTypeNoAnnotationSingleParam(Element whereElement, Boolean dynamic);

        void handleCompositeObjectNoAnnotationSingleParam(Element whereElement, Boolean dynamic);

        void handleObjectTypeWithAnnotationSingleParam(Element whereElement, Boolean dynamic);

        void handleCompositeObjectWithAnnotationSingleParam(Element whereElement, Boolean dynamic);

        void handleRelationColumnSingleParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic);


        void handleBasicTypeMultiParam(Element whereElement, Boolean dynamic);

        void handleObjectTypeNoAnnotationMultiParam(Element whereElement, Boolean dynamic);

        void handleCompositeObjectNoAnnotationMultiParam(Element whereElement, Boolean dynamic);

        void handleObjectTypeWithAnnotationMultiParam(Element whereElement, Boolean dynamic);

        void handleCompositeObjectWithAnnotationMultiParam(Element whereElement, Boolean dynamic);

        void handleRelationColumnMultiParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic);
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

            int paramCount = methodInfo.getMethodParamInfoList().size();
            if (paramCount == 1) {
                this.singleParamHandle(methodInfo, conditionInfo, whereElement);
            } else {
                this.multiParamHandle(methodInfo, conditionInfo, whereElement);
            }
        }

        @Override
        public void singleParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            Boolean dynamic = methodInfo.getDynamic();
            ColumnInfo columnInfo = conditionInfo.getColumnInfo();
            MethodParamInfo methodParamInfo = conditionInfo.getMethodParamInfo();
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                if (methodParamInfo.getBasicType()) {
                    // findById(Long id) findById(@Param("id") Long id)
                    this.handleBasicTypeSingleParam(whereElement, dynamic);
                } else {
                    // findById(MultiId id) findById(@Param("id") MultiId id)
                    if (methodParamInfo.getParam() == null) {
                        if (ObjectUtils.isEmpty(columnInfo.getComposites())) {
                            this.handleObjectTypeNoAnnotationSingleParam(whereElement, dynamic);
                        } else {
                            this.handleCompositeObjectNoAnnotationSingleParam(whereElement, dynamic);
                        }
                    } else {
                        if (ObjectUtils.isEmpty(columnInfo.getComposites())) {
                            this.handleObjectTypeWithAnnotationSingleParam(whereElement, dynamic);
                        } else {
                            this.handleCompositeObjectWithAnnotationSingleParam(whereElement, dynamic);
                        }
                    }
                }
            }
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                if (relationColumnInfo.getMappedByRelationColumnInfo() == null) {
                    this.handleRelationColumnSingleParam(whereElement, relationColumnInfo, dynamic);
                }
            }
        }

        @Override
        public void multiParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            Boolean dynamic = methodInfo.getDynamic();
            ColumnInfo columnInfo = conditionInfo.getColumnInfo();
            MethodParamInfo methodParamInfo = conditionInfo.getMethodParamInfo();
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                if (methodParamInfo.getBasicType()) {
                    // findById(Long id) findById(@Param("id") Long id)
                    this.handleBasicTypeMultiParam(whereElement, dynamic);
                } else {
                    // findById(MultiId id) findById(@Param("id") MultiId id)
                    if (methodParamInfo.getParam() == null) {
                        if (ObjectUtils.isEmpty(columnInfo.getComposites())) {
                            this.handleObjectTypeNoAnnotationMultiParam(whereElement, dynamic);
                        } else {
                            this.handleCompositeObjectNoAnnotationMultiParam(whereElement, dynamic);
                        }
                    } else {
                        if (ObjectUtils.isEmpty(columnInfo.getComposites())) {
                            this.handleObjectTypeWithAnnotationMultiParam(whereElement, dynamic);
                        } else {
                            this.handleCompositeObjectWithAnnotationMultiParam(whereElement, dynamic);
                        }
                    }
                }
            }
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                if (relationColumnInfo.getMappedByRelationColumnInfo() == null) {
                    this.handleRelationColumnMultiParam(whereElement, relationColumnInfo, dynamic);
                }
            }
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

        protected void addWhereOrIfElementWithText(Element whereOrIfElement, String conditionExpression) {
            whereOrIfElement.addText(conditionExpression);
        }

        protected void addWhereOrIfElementWithElement(Element whereOrIfElement, Element whereOrIfChildElement) {
            whereOrIfElement.add(whereOrIfChildElement);
        }
    }

    static class LikeConditionHandler extends AbstractConditionHandler {

        @Override
        public void handleBasicTypeSingleParam(Element whereElement, Boolean dynamic) {
            /**
             *             <if test="@org.apache.commons.lang3.StringUtils@isNotBlank(likeClientCode)">
             *                 <bind name="likeClientCode" value="'%' + likeClientCode + '%'"/>
             *                 and act.client_code like #{likeClientCode}
             *             </if>
             */

            String bindValue = "'%'+" + methodParamInfo.getArgName() + "+'%'";
            Element bindElement = this.buildBindElement(methodParamInfo.getArgName(), bindValue);

            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);

            String paramValueExpression = String.format("#{%1$s}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);

            this.addWhereOrIfElementWithElement(whereOrIfElement, bindElement);
            this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
        }

        @Override
        public void handleObjectTypeNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleCompositeObjectNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleObjectTypeWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleCompositeObjectWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleRelationColumnSingleParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {

        }

        @Override
        public void handleBasicTypeMultiParam(Element whereElement, Boolean dynamic) {
            /**
             *             <if test="@org.apache.commons.lang3.StringUtils@isNotBlank(likeClientCode)">
             *                 <bind name="likeClientCode" value="'%' + likeClientCode + '%'"/>
             *                 and act.client_code like #{likeClientCode}
             *             </if>
             */

            String bindValue = "'%'+" + methodParamInfo.getArgName() + "+'%'";
            Element bindElement = this.buildBindElement(methodParamInfo.getArgName(), bindValue);

            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);

            String paramValueExpression = String.format("#{%1$s}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);

            this.addWhereOrIfElementWithElement(whereOrIfElement, bindElement);
            this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
        }

        @Override
        public void handleObjectTypeNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleCompositeObjectNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleObjectTypeWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleCompositeObjectWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleRelationColumnMultiParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {

        }
    }

    static class InConditionHandler extends AbstractConditionHandler {

        @Override
        public void handleBasicTypeSingleParam(Element whereElement, Boolean dynamic) {
            /**
             *             <if test="terminal == @com.iss.dtg.idms.constant.Terminal@EBANK">
             *                 and act.client_code in
             *                 <foreach item="item" index="index" collection="unDraftClientCodeList" open="(" separator="," close=")">
             *                     #{item}
             * 				</foreach>
             *             </if>
             */

            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);

            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, "");

            Element foreachElement = DocumentHelper.createElement("foreach");
            foreachElement.addAttribute("index", "index");
            foreachElement.addAttribute("item", "item");
            foreachElement.addAttribute("collection", methodParamInfo.getArgName());
            foreachElement.addAttribute("open", "(");
            foreachElement.addAttribute("close", ")");
            foreachElement.addAttribute("separator", ",");
            foreachElement.addText("#{item}");

            this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
            this.addWhereOrIfElementWithElement(whereOrIfElement, foreachElement);
        }

        @Override
        public void handleObjectTypeNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleCompositeObjectNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleObjectTypeWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleCompositeObjectWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleRelationColumnSingleParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {

        }

        @Override
        public void handleBasicTypeMultiParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleObjectTypeNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleCompositeObjectNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleObjectTypeWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleCompositeObjectWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleRelationColumnMultiParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {

        }
    }

    static class BetweenConditionHandler extends AbstractConditionHandler {

        @Override
        public void handleBasicTypeSingleParam(Element whereElement, Boolean dynamic) {
            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            String paramValueExpression = String.format("#{%1$s[0]} and #{%1$s[1]}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
            this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
        }

        @Override
        public void handleObjectTypeNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            String testExpression = String.format("%1$s != null", conditionInfo.getColumnName());
            String paramValueExpression = String.format("#{%1$s[0]} and #{%1$s[1]}", conditionInfo.getColumnName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
            this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
        }

        @Override
        public void handleCompositeObjectNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            for (ColumnInfo columnInfoComposite : columnInfo.getComposites()) {
                String testExpression = String.format("%1$s != null and %1$s.%2$s != null", columnInfo.getJavaColumnName(), columnInfoComposite.getJavaColumnName());
                String paramValueExpression = String.format("#{%1$s.%2$s[0]} and #{%1$s.%2$s[1]}", columnInfo.getJavaColumnName());
                String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
                Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
                this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
            }
        }

        @Override
        public void handleObjectTypeWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleCompositeObjectWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleRelationColumnSingleParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {

        }

        @Override
        public void handleBasicTypeMultiParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleObjectTypeNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleCompositeObjectNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleObjectTypeWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleCompositeObjectWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {

        }

        @Override
        public void handleRelationColumnMultiParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {

        }
    }

    static class CommonConditionHandler extends AbstractConditionHandler {

        @Override
        public void handleBasicTypeSingleParam(Element whereElement, Boolean dynamic) {
            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            String paramValueExpression = String.format("#{%1$s}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
            this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
        }

        @Override
        public void handleObjectTypeNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            String testExpression = String.format("%1$s != null", columnInfo.getJavaColumnName());
            String paramValueExpression = String.format("#{%1$s}", columnInfo.getJavaColumnName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
            this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
        }

        @Override
        public void handleCompositeObjectNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            for (ColumnInfo columnInfoComposite : columnInfo.getComposites()) {
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
                Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
                this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
            }
        }

        @Override
        public void handleObjectTypeWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            String testExpression = String.format("%1$s.%2$s != null", methodParamInfo.getArgName(), columnInfo.getJavaColumnName());
            String paramValueExpression = String.format("#{%1$s.%2$s}", methodParamInfo.getArgName(), columnInfo.getJavaColumnName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
            this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
        }

        @Override
        public void handleCompositeObjectWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            for (ColumnInfo columnInfoComposite : columnInfo.getComposites()) {
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
                Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
                this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
            }
        }

        @Override
        public void handleRelationColumnSingleParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
            for (ForeignKeyColumnInfo foreignKeyInfo : inverseForeignKeyColumnInfoList) {
                ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
                String testExpression = String.format(
                        "%1$s != null and %1$s.%2$s != null",
                        columnInfo.getJavaColumnName(),
                        referencedColumnInfo.getJavaColumnName()
                );
                String paramValueExpression = String.format("#{%1$s.%2$s}", columnInfo.getJavaColumnName(), referencedColumnInfo.getJavaColumnName());
                String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
                Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
                this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
            }
        }

        @Override
        public void handleBasicTypeMultiParam(Element whereElement, Boolean dynamic) {
            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            String paramValueExpression = String.format("#{%1$s}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
            this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
        }

        @Override
        public void handleObjectTypeNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {
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
            Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
            this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
        }

        @Override
        public void handleCompositeObjectNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            for (ColumnInfo columnInfoComposite : columnInfo.getComposites()) {
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
                Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
                this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
            }
        }

        @Override
        public void handleObjectTypeWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {
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
            Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
            this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
        }

        @Override
        public void handleCompositeObjectWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            for (ColumnInfo columnInfoComposite : columnInfo.getComposites()) {
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
                Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
                this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
            }
        }

        @Override
        public void handleRelationColumnMultiParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {
            for (ForeignKeyColumnInfo foreignKeyInfo : relationColumnInfo.getInverseForeignKeyColumnInfoList()) {
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
                Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, dynamic, testExpression);
                this.addWhereOrIfElementWithText(whereOrIfElement, conditionExpression);
            }
        }
    }
}
