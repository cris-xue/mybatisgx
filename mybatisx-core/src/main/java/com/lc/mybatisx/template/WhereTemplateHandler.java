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

        List<WhereItemContext> singleParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement);

        List<WhereItemContext> multiParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement);

        List<WhereItemContext> handleBasicTypeSingleParam(Element whereElement, Boolean dynamic);

        List<WhereItemContext> handleObjectTypeNoAnnotationSingleParam(Element whereElement, Boolean dynamic);

        List<WhereItemContext> handleCompositeObjectNoAnnotationSingleParam(Element whereElement, Boolean dynamic);

        List<WhereItemContext> handleObjectTypeWithAnnotationSingleParam(Element whereElement, Boolean dynamic);

        List<WhereItemContext> handleCompositeObjectWithAnnotationSingleParam(Element whereElement, Boolean dynamic);

        List<WhereItemContext> handleRelationColumnSingleParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic);


        List<WhereItemContext> handleBasicTypeMultiParam(Element whereElement, Boolean dynamic);

        List<WhereItemContext> handleObjectTypeNoAnnotationMultiParam(Element whereElement, Boolean dynamic);

        List<WhereItemContext> handleCompositeObjectNoAnnotationMultiParam(Element whereElement, Boolean dynamic);

        List<WhereItemContext> handleObjectTypeWithAnnotationMultiParam(Element whereElement, Boolean dynamic);

        List<WhereItemContext> handleCompositeObjectWithAnnotationMultiParam(Element whereElement, Boolean dynamic);

        List<WhereItemContext> handleRelationColumnMultiParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic);
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
                whereItemContextList = this.singleParamHandle(methodInfo, conditionInfo, whereElement);
            } else {
                whereItemContextList = this.multiParamHandle(methodInfo, conditionInfo, whereElement);
            }
            this.processWhereItem(whereElement, methodInfo, whereItemContextList);
        }

        /**
         * 处理条件，有特殊情况的可以在子类中重写
         * @param whereElement
         * @param methodInfo
         * @param whereItemContextList
         */
        protected void processWhereItem(Element whereElement, MethodInfo methodInfo, List<WhereItemContext> whereItemContextList) {
            if (ObjectUtils.isEmpty(whereItemContextList)) {
                return;
            }
            for (WhereItemContext whereItemContext : whereItemContextList) {
                String testExpression = whereItemContext.getTestExpression();
                Element whereOrIfElement = this.buildWhereOrIfElement(whereElement, methodInfo.getDynamic(), testExpression);
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

        @Override
        public List<WhereItemContext> singleParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            Boolean dynamic = methodInfo.getDynamic();
            ColumnInfo columnInfo = conditionInfo.getColumnInfo();
            MethodParamInfo methodParamInfo = conditionInfo.getMethodParamInfo();

            List<WhereItemContext> whereItemContextList = null;
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                if (methodParamInfo.getBasicType()) {
                    // findById(Long id) findById(@Param("id") Long id)
                    whereItemContextList = this.handleBasicTypeSingleParam(whereElement, dynamic);
                } else {
                    // findById(MultiId id) findById(@Param("id") MultiId id)
                    if (methodParamInfo.getParam() == null) {
                        if (ObjectUtils.isEmpty(columnInfo.getComposites())) {
                            whereItemContextList = this.handleObjectTypeNoAnnotationSingleParam(whereElement, dynamic);
                        } else {
                            whereItemContextList = this.handleCompositeObjectNoAnnotationSingleParam(whereElement, dynamic);
                        }
                    } else {
                        if (ObjectUtils.isEmpty(columnInfo.getComposites())) {
                            whereItemContextList = this.handleObjectTypeWithAnnotationSingleParam(whereElement, dynamic);
                        } else {
                            whereItemContextList = this.handleCompositeObjectWithAnnotationSingleParam(whereElement, dynamic);
                        }
                    }
                }
            }
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                if (relationColumnInfo.getMappedByRelationColumnInfo() == null) {
                    whereItemContextList = this.handleRelationColumnSingleParam(whereElement, relationColumnInfo, dynamic);
                }
            }
            return whereItemContextList;
        }

        @Override
        public List<WhereItemContext> multiParamHandle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            Boolean dynamic = methodInfo.getDynamic();
            ColumnInfo columnInfo = conditionInfo.getColumnInfo();
            MethodParamInfo methodParamInfo = conditionInfo.getMethodParamInfo();

            List<WhereItemContext> whereItemContextList = null;
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                if (methodParamInfo.getBasicType()) {
                    // findById(Long id) findById(@Param("id") Long id)
                    whereItemContextList = this.handleBasicTypeMultiParam(whereElement, dynamic);
                } else {
                    // findById(MultiId id) findById(@Param("id") MultiId id)
                    if (methodParamInfo.getParam() == null) {
                        if (ObjectUtils.isEmpty(columnInfo.getComposites())) {
                            whereItemContextList = this.handleObjectTypeNoAnnotationMultiParam(whereElement, dynamic);
                        } else {
                            whereItemContextList = this.handleCompositeObjectNoAnnotationMultiParam(whereElement, dynamic);
                        }
                    } else {
                        if (ObjectUtils.isEmpty(columnInfo.getComposites())) {
                            whereItemContextList = this.handleObjectTypeWithAnnotationMultiParam(whereElement, dynamic);
                        } else {
                            whereItemContextList = this.handleCompositeObjectWithAnnotationMultiParam(whereElement, dynamic);
                        }
                    }
                }
            }
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                if (relationColumnInfo.getMappedByRelationColumnInfo() == null) {
                    whereItemContextList = this.handleRelationColumnMultiParam(whereElement, relationColumnInfo, dynamic);
                }
            }
            return whereItemContextList;
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

    static class LikeConditionHandler extends AbstractConditionHandler {

        @Override
        public List<WhereItemContext> handleBasicTypeSingleParam(Element whereElement, Boolean dynamic) {
            /**
             *             <if test="@org.apache.commons.lang3.StringUtils@isNotBlank(likeClientCode)">
             *                 <bind name="likeClientCode" value="'%' + likeClientCode + '%'"/>
             *                 and act.client_code like #{likeClientCode}
             *             </if>
             */

            String bindValue = "'%'+" + methodParamInfo.getArgName() + "+'%'";
            Element bindElement = this.buildBindElement(methodParamInfo.getArgName(), bindValue);

            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());

            String paramValueExpression = String.format("#{%1$s}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);

            return Arrays.asList(new WhereItemContext(testExpression, Arrays.asList(bindElement, conditionExpression)));
        }

        @Override
        public List<WhereItemContext> handleObjectTypeNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleObjectTypeWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleRelationColumnSingleParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleBasicTypeMultiParam(Element whereElement, Boolean dynamic) {
            /**
             *             <if test="@org.apache.commons.lang3.StringUtils@isNotBlank(likeClientCode)">
             *                 <bind name="likeClientCode" value="'%' + likeClientCode + '%'"/>
             *                 and act.client_code like #{likeClientCode}
             *             </if>
             */

            String bindValue = "'%'+" + methodParamInfo.getArgName() + "+'%'";
            Element bindElement = this.buildBindElement(methodParamInfo.getArgName(), bindValue);

            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());

            String paramValueExpression = String.format("#{%1$s}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return Arrays.asList(new WhereItemContext(testExpression, Arrays.asList(bindElement, conditionExpression)));
        }

        @Override
        public List<WhereItemContext> handleObjectTypeNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleObjectTypeWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleRelationColumnMultiParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }
    }

    static class InConditionHandler extends AbstractConditionHandler {

        @Override
        public List<WhereItemContext> handleBasicTypeSingleParam(Element whereElement, Boolean dynamic) {
            /**
             *             <if test="terminal == @com.iss.dtg.idms.constant.Terminal@EBANK">
             *                 and act.client_code in
             *                 <foreach item="item" index="index" collection="unDraftClientCodeList" open="(" separator="," close=")">
             *                     #{item}
             * 				</foreach>
             *             </if>
             */

            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());

            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, "");

            Element foreachElement = DocumentHelper.createElement("foreach");
            foreachElement.addAttribute("index", "index");
            foreachElement.addAttribute("item", "item");
            foreachElement.addAttribute("collection", methodParamInfo.getArgName());
            foreachElement.addAttribute("open", "(");
            foreachElement.addAttribute("close", ")");
            foreachElement.addAttribute("separator", ",");
            foreachElement.addText("#{item}");
            return Arrays.asList(new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement)));
        }

        @Override
        public List<WhereItemContext> handleObjectTypeNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleObjectTypeWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleRelationColumnSingleParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleBasicTypeMultiParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<WhereItemContext> handleObjectTypeNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleObjectTypeWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleRelationColumnMultiParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }
    }

    static class BetweenConditionHandler extends AbstractConditionHandler {

        @Override
        public List<WhereItemContext> handleBasicTypeSingleParam(Element whereElement, Boolean dynamic) {
            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            String paramValueExpression = String.format("#{%1$s[0]} and #{%1$s[1]}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return Arrays.asList(new WhereItemContext(testExpression, Arrays.asList(conditionExpression)));
        }

        @Override
        public List<WhereItemContext> handleObjectTypeNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            String testExpression = String.format("%1$s != null", conditionInfo.getColumnName());
            String paramValueExpression = String.format("#{%1$s[0]} and #{%1$s[1]}", conditionInfo.getColumnName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return Arrays.asList(new WhereItemContext(testExpression, Arrays.asList(conditionExpression)));
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            List<WhereItemContext> whereItemContextList = new ArrayList<>();
            for (ColumnInfo columnInfoComposite : columnInfo.getComposites()) {
                String testExpression = String.format("%1$s != null and %1$s.%2$s != null", columnInfo.getJavaColumnName(), columnInfoComposite.getJavaColumnName());
                String paramValueExpression = String.format("#{%1$s.%2$s[0]} and #{%1$s.%2$s[1]}", columnInfo.getJavaColumnName());
                String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
                whereItemContextList.add(new WhereItemContext(testExpression, Arrays.asList(conditionExpression)));
            }
            return whereItemContextList;
        }

        @Override
        public List<WhereItemContext> handleObjectTypeWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleRelationColumnSingleParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleBasicTypeMultiParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<WhereItemContext> handleObjectTypeNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleObjectTypeWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public List<WhereItemContext> handleRelationColumnMultiParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {
            throw new UnsupportedOperationException("");
        }
    }

    static class CommonConditionHandler extends AbstractConditionHandler {

        @Override
        public List<WhereItemContext> handleBasicTypeSingleParam(Element whereElement, Boolean dynamic) {
            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            String paramValueExpression = String.format("#{%1$s}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return Arrays.asList(new WhereItemContext(testExpression, Arrays.asList(conditionExpression)));
        }

        @Override
        public List<WhereItemContext> handleObjectTypeNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            String testExpression = String.format("%1$s != null", columnInfo.getJavaColumnName());
            String paramValueExpression = String.format("#{%1$s}", columnInfo.getJavaColumnName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return Arrays.asList(new WhereItemContext(testExpression, Arrays.asList(conditionExpression)));
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectNoAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            List<WhereItemContext> whereItemContextList = new ArrayList<>();
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
                whereItemContextList.add(new WhereItemContext(testExpression, Arrays.asList(conditionExpression)));
            }
            return whereItemContextList;
        }

        @Override
        public List<WhereItemContext> handleObjectTypeWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            String testExpression = String.format("%1$s.%2$s != null", methodParamInfo.getArgName(), columnInfo.getJavaColumnName());
            String paramValueExpression = String.format("#{%1$s.%2$s}", methodParamInfo.getArgName(), columnInfo.getJavaColumnName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return Arrays.asList(new WhereItemContext(testExpression, Arrays.asList(conditionExpression)));
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectWithAnnotationSingleParam(Element whereElement, Boolean dynamic) {
            List<WhereItemContext> whereItemContextList = new ArrayList<>();
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
                whereItemContextList.add(new WhereItemContext(testExpression, Arrays.asList(conditionExpression)));
            }
            return whereItemContextList;
        }

        @Override
        public List<WhereItemContext> handleRelationColumnSingleParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {
            List<WhereItemContext> whereItemContextList = new ArrayList<>();
            for (ForeignKeyColumnInfo foreignKeyInfo : relationColumnInfo.getInverseForeignKeyColumnInfoList()) {
                ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
                String testExpression = String.format(
                        "%1$s != null and %1$s.%2$s != null",
                        columnInfo.getJavaColumnName(),
                        referencedColumnInfo.getJavaColumnName()
                );
                String paramValueExpression = String.format("#{%1$s.%2$s}", columnInfo.getJavaColumnName(), referencedColumnInfo.getJavaColumnName());
                String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
                whereItemContextList.add(new WhereItemContext(testExpression, Arrays.asList(conditionExpression)));
            }
            return whereItemContextList;
        }

        @Override
        public List<WhereItemContext> handleBasicTypeMultiParam(Element whereElement, Boolean dynamic) {
            String testExpression = String.format("%1$s != null", methodParamInfo.getArgName());
            String paramValueExpression = String.format("#{%1$s}", methodParamInfo.getArgName());
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return Arrays.asList(new WhereItemContext(testExpression, Arrays.asList(conditionExpression)));
        }

        @Override
        public List<WhereItemContext> handleObjectTypeNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {
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
            return Arrays.asList(new WhereItemContext(testExpression, Arrays.asList(conditionExpression)));
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectNoAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            List<WhereItemContext> whereItemContextList = new ArrayList<>();
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
                whereItemContextList.add(new WhereItemContext(testExpression, Arrays.asList(conditionExpression)));
            }
            return whereItemContextList;
        }

        @Override
        public List<WhereItemContext> handleObjectTypeWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {
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
            return Arrays.asList(new WhereItemContext(testExpression, Arrays.asList(conditionExpression)));
        }

        @Override
        public List<WhereItemContext> handleCompositeObjectWithAnnotationMultiParam(Element whereElement, Boolean dynamic) {
            List<WhereItemContext> whereItemContextList = new ArrayList<>();
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
                whereItemContextList.add(new WhereItemContext(testExpression, Arrays.asList(conditionExpression)));
            }
            return whereItemContextList;
        }

        @Override
        public List<WhereItemContext> handleRelationColumnMultiParam(Element whereElement, RelationColumnInfo relationColumnInfo, Boolean dynamic) {
            List<WhereItemContext> whereItemContextList = new ArrayList<>();
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
                whereItemContextList.add(new WhereItemContext(testExpression, Arrays.asList(conditionExpression)));
            }
            return whereItemContextList;
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
