package com.mybatisgx.template;

import com.google.common.collect.Lists;
import com.mybatisgx.annotation.LogicDelete;
import com.mybatisgx.model.*;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.*;

/**
 * 条件模板处理器。修改和删除的条件只能采用方法名定义，不能使用查询实体作为修改和删除方法的条件。
 * @author ccxuef
 * @date 2025/11/5 19:26
 */
public class WhereTemplateHandler {

    public Element execute(EntityInfo entityInfo, MethodInfo methodInfo) {
        ConditionProcessorFactory factory = new ConditionProcessorFactory();
        Element whereElement = DocumentHelper.createElement("where");
        this.handleConditionGroup(methodInfo, whereElement, methodInfo.getConditionInfoList(), factory);
        this.addOptimisticLockCondition(entityInfo, methodInfo, whereElement);
        this.addLogicDeleteCondition(entityInfo, whereElement);
        return whereElement;
    }

    private void addOptimisticLockCondition(EntityInfo entityInfo, MethodInfo methodInfo, Element whereElement) {
        // 查询不需要乐观锁版本条件
        MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
        ColumnInfo lockColumnInfo = entityInfo.getLockColumnInfo();
        if (lockColumnInfo != null) {
            // 只有更新的场景才需要乐观锁，逻辑删除不需要乐观锁，因为逻辑删除直接改变逻辑删除字段，因为不管什么操作都一定需要逻辑删除字段
            if (methodInfo.getSqlCommandType() == SqlCommandType.UPDATE) {
                List<String> argValueCommonPathItemList = new ArrayList<>();
                if (methodInfo.getBatch()) {
                    argValueCommonPathItemList.add(entityParamInfo.getBatchItemName());
                }
                argValueCommonPathItemList.add(lockColumnInfo.getJavaColumnName());
                String valueExpression = StringUtils.join(argValueCommonPathItemList, ".");
                whereElement.addText(String.format(" and %s = #{%s}", lockColumnInfo.getDbColumnName(), valueExpression));
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

        protected final Map<ComparisonOperator, ConditionHandler> conditionHandlers = new HashMap<>();

        public ConditionProcessorFactory() {
            conditionHandlers.put(ComparisonOperator.LT, new CommonConditionHandler());
            conditionHandlers.put(ComparisonOperator.LT_EQ, new CommonConditionHandler());
            conditionHandlers.put(ComparisonOperator.GT, new CommonConditionHandler());
            conditionHandlers.put(ComparisonOperator.GT_EQ, new CommonConditionHandler());
            conditionHandlers.put(ComparisonOperator.LT, new CommonConditionHandler());
            conditionHandlers.put(ComparisonOperator.IN, new InConditionHandler());
            conditionHandlers.put(ComparisonOperator.EQ, new CommonConditionHandler());
            conditionHandlers.put(ComparisonOperator.EQUAL, new CommonConditionHandler());
            conditionHandlers.put(ComparisonOperator.LIKE, new LikeConditionHandler());
            conditionHandlers.put(ComparisonOperator.STARTING_WITH, new StartingLikeConditionHandler());
            conditionHandlers.put(ComparisonOperator.ENDING_WITH, new EndingLikeConditionHandler());
            conditionHandlers.put(ComparisonOperator.BETWEEN, new BetweenConditionHandler());

            conditionHandlers.put(ComparisonOperator.NULL, new NullConditionHandler());
            conditionHandlers.put(ComparisonOperator.IS_NULL, new NullConditionHandler());
            conditionHandlers.put(ComparisonOperator.IS_NOT_NULL, new NullConditionHandler());
            conditionHandlers.put(ComparisonOperator.NOT_NULL, new NullConditionHandler());
        }

        public void process(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            ConditionHandler conditionHandler = conditionHandlers.get(conditionInfo.getComparisonOperator());
            conditionHandler.handle(methodInfo, conditionInfo, whereElement);
        }
    }

    interface ConditionHandler {

        void handle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement);

        WhereItemContext handleSimpleTypeSingleParam(ColumnInfo columnInfo);

        WhereItemContext handleSimpleTypeNoAnnotationSingleParam(ColumnInfo columnInfo);

        WhereItemContext handleComplexTypeNoAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite);

        WhereItemContext handleSimpleTypeWithAnnotationSingleParam(ColumnInfo columnInfo);

        WhereItemContext handleComplexTypeWithAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite);

        WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo);


        WhereItemContext handleSimpleTypeMultiParam(ColumnInfo columnInfo);

        WhereItemContext handleSimpleTypeNoAnnotationMultiParam(ColumnInfo columnInfo);

        WhereItemContext handleComplexTypeNoAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite);

        WhereItemContext handleSimpleTypeWithAnnotationMultiParam(ColumnInfo columnInfo);

        WhereItemContext handleComplexTypeWithAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite);

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
                if (methodParamInfo.getClassCategory() == ClassCategory.SIMPLE) {
                    // findById(Long id) findById(@Param("id") Long id)
                    WhereItemContext whereItemContext = this.handleSimpleTypeSingleParam(columnInfo);
                    whereItemContextList.add(whereItemContext);
                }
                if (methodParamInfo.getClassCategory() == ClassCategory.COMPLEX) {
                    // findById(MultiId id) findById(@Param("id") MultiId id)
                    if (methodParamInfo.getParam() == null) {
                        List<ColumnInfo> columnInfoComposites = methodParamInfo.getColumnInfoList();
                        if (ObjectUtils.isEmpty(columnInfoComposites)) {
                            WhereItemContext whereItemContext = this.handleSimpleTypeNoAnnotationSingleParam(columnInfo);
                            whereItemContextList.add(whereItemContext);
                        } else {
                            for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                                WhereItemContext whereItemContext = this.handleComplexTypeNoAnnotationSingleParam(columnInfo, columnInfoComposite);
                                whereItemContextList.add(whereItemContext);
                            }
                        }
                    } else {
                        List<ColumnInfo> columnInfoComposites = methodParamInfo.getColumnInfoList();
                        if (ObjectUtils.isEmpty(columnInfoComposites)) {
                            WhereItemContext whereItemContext = this.handleSimpleTypeWithAnnotationSingleParam(columnInfo);
                            whereItemContextList.add(whereItemContext);
                        } else {
                            for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                                WhereItemContext whereItemContext = this.handleComplexTypeWithAnnotationSingleParam(columnInfo, columnInfoComposite);
                                whereItemContextList.add(whereItemContext);
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

        public List<WhereItemContext> multiParamHandle() {
            List<WhereItemContext> whereItemContextList = new ArrayList();
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                if (methodParamInfo.getClassCategory() == ClassCategory.SIMPLE) {
                    // findById(Long id) findById(@Param("id") Long id)
                    WhereItemContext whereItemContext = this.handleSimpleTypeMultiParam(columnInfo);
                    whereItemContextList.add(whereItemContext);
                }
                if (methodParamInfo.getClassCategory() == ClassCategory.COMPLEX) {
                    // findById(MultiId id) findById(@Param("id") MultiId id)
                    if (methodParamInfo.getParam() == null) {
                        List<ColumnInfo> columnInfoComposites = methodParamInfo.getColumnInfoList();
                        if (ObjectUtils.isEmpty(columnInfoComposites)) {
                            WhereItemContext whereItemContext = this.handleSimpleTypeNoAnnotationMultiParam(columnInfo);
                            whereItemContextList.add(whereItemContext);
                        } else {
                            for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                                WhereItemContext whereItemContext = this.handleComplexTypeNoAnnotationMultiParam(columnInfo, columnInfoComposite);
                                whereItemContextList.add(whereItemContext);
                            }
                        }
                    } else {
                        List<ColumnInfo> columnInfoComposites = methodParamInfo.getColumnInfoList();
                        if (ObjectUtils.isEmpty(columnInfoComposites)) {
                            WhereItemContext whereItemContext = this.handleSimpleTypeWithAnnotationMultiParam(columnInfo);
                            whereItemContextList.add(whereItemContext);
                        } else {
                            for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                                WhereItemContext whereItemContext = this.handleComplexTypeWithAnnotationMultiParam(columnInfo, columnInfoComposite);
                                whereItemContextList.add(whereItemContext);
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

        protected String getTestExpression(List<String> pathItemList) {
            String[] paths = pathItemList.toArray(new String[pathItemList.size()]);
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

        protected String getParamValueExpression(List<String> pathItemList) {
            return String.format("#{%1$s}", StringUtils.join(pathItemList, "."));
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

        protected List<String> getParamValuePathItemList(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            List<String> argValueCommonPathItemList = Lists.newArrayList(methodParamInfo.getArgValueCommonPathItemList());
            if (columnInfoComposite != null) {
                argValueCommonPathItemList.add(columnInfoComposite.getJavaColumnName());
            }
            return argValueCommonPathItemList;
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
        public WhereItemContext handleSimpleTypeSingleParam(ColumnInfo columnInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, null);
            String testExpression = this.getTestExpression(paramValuePathItemList);
            String bindKey = this.getBindKey(paramValuePathItemList);
            String bindValuePath = this.getBindValuePath(paramValuePathItemList);
            Element likeBindElement = this.buildLikeBindElement(bindKey, bindValuePath);
            String paramValueExpression = this.getParamValueExpression(Arrays.asList(bindKey));
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        @Override
        public WhereItemContext handleSimpleTypeNoAnnotationSingleParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeNoAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, columnInfoComposite);
            String testExpression = this.getTestExpression(paramValuePathItemList);
            String bindKey = this.getBindKey(paramValuePathItemList);
            String bindValuePath = this.getBindValuePath(paramValuePathItemList);
            Element likeBindElement = this.buildLikeBindElement(bindKey, bindValuePath);
            String paramValueExpression = this.getParamValueExpression(Arrays.asList(bindKey));
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        @Override
        public WhereItemContext handleSimpleTypeWithAnnotationSingleParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeWithAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return this.handleComplexTypeNoAnnotationSingleParam(columnInfo, columnInfoComposite);
        }

        @Override
        public WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String bindKey = String.format(
                    "%1$s_%2$s",
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String bindValuePath = String.format(
                    "%1$s.%2$s",
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            Element likeBindElement = this.buildLikeBindElement(bindKey, bindValuePath);
            String paramValueExpression = bindKey;
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        @Override
        public WhereItemContext handleSimpleTypeMultiParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleSimpleTypeNoAnnotationMultiParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeNoAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return this.handleComplexTypeNoAnnotationSingleParam(columnInfo, columnInfoComposite);
        }

        @Override
        public WhereItemContext handleSimpleTypeWithAnnotationMultiParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeWithAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return this.handleComplexTypeNoAnnotationSingleParam(columnInfo, columnInfoComposite);
        }

        @Override
        public WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String bindKey = String.format(
                    "%1$s_%2$s_%3$s",
                    methodParamInfo.getArgName(),
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String bindValuePath = String.format(
                    "%1$s.%2$s.%3$s",
                    methodParamInfo.getArgName(),
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            Element likeBindElement = this.buildLikeBindElement(bindKey, bindValuePath);
            String paramValueExpression = bindKey;
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        private String getBindKey(List<String> pathItemList) {
            return StringUtils.join(pathItemList, "_");
        }

        private String getBindValuePath(List<String> pathItemList) {
            return StringUtils.join(pathItemList, ".");
        }

        protected Element buildLikeBindElement(String bindKey, String bindValuePath) {
            String likeExpression = "%" + bindValuePath + "%";
            return this.buildBindElement(bindKey, likeExpression);
        }
    }

    static class StartingLikeConditionHandler extends LikeConditionHandler {

        @Override
        protected Element buildLikeBindElement(String bindKey, String bindValuePath) {
            String likeExpression = "%" + bindValuePath;
            return this.buildBindElement(bindKey, likeExpression);
        }
    }

    static class EndingLikeConditionHandler extends LikeConditionHandler {

        @Override
        protected Element buildLikeBindElement(String bindKey, String bindValuePath) {
            String likeExpression = bindValuePath + "%";
            return this.buildBindElement(bindKey, likeExpression);
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
        public WhereItemContext handleSimpleTypeSingleParam(ColumnInfo columnInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, null);
            String testExpression = this.getTestExpression(paramValuePathItemList);
            String paramValueExpression = this.getParamValueExpression(paramValuePathItemList);
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, "");
            Element foreachElement = this.buildForeachElement(paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement));
        }

        @Override
        public WhereItemContext handleSimpleTypeNoAnnotationSingleParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeNoAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, columnInfoComposite);
            String testExpression = this.getTestExpression(paramValuePathItemList);
            String paramValueExpression = this.getParamValueExpression(paramValuePathItemList);
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, "");
            Element foreachElement = this.buildForeachElement(paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement));
        }

        @Override
        public WhereItemContext handleSimpleTypeWithAnnotationSingleParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeWithAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return this.handleComplexTypeNoAnnotationSingleParam(columnInfo, columnInfoComposite);
        }

        @Override
        public WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "%1$s.%2$s",
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, "");
            Element foreachElement = this.buildForeachElement(paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression, foreachElement));
        }

        @Override
        public WhereItemContext handleSimpleTypeMultiParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleSimpleTypeNoAnnotationMultiParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeNoAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return this.handleComplexTypeNoAnnotationSingleParam(columnInfo, columnInfoComposite);
        }

        @Override
        public WhereItemContext handleSimpleTypeWithAnnotationMultiParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeWithAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return this.handleComplexTypeNoAnnotationSingleParam(columnInfo, columnInfoComposite);
        }

        @Override
        public WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "%1$s.%2$s.%3$s",
                    methodParamInfo.getArgName(),
                    relationColumnInfo.getJavaColumnName(),
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
        public WhereItemContext handleSimpleTypeSingleParam(ColumnInfo columnInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, null);
            String testExpression = this.getTestExpression(paramValuePathItemList);
            String paramValueExpression = this.getParamValueExpression(paramValuePathItemList);
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleSimpleTypeNoAnnotationSingleParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeNoAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, columnInfoComposite);
            String testExpression = this.getTestExpression(paramValuePathItemList);
            String paramValueExpression = this.getParamValueExpression(paramValuePathItemList);
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleSimpleTypeWithAnnotationSingleParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeWithAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return this.handleComplexTypeNoAnnotationSingleParam(columnInfo, columnInfoComposite);
        }

        @Override
        public WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s[0]} and #{%1$s.%2$s[1]}",
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleSimpleTypeMultiParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleSimpleTypeNoAnnotationMultiParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeNoAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return handleComplexTypeNoAnnotationSingleParam(columnInfo, columnInfoComposite);
        }

        @Override
        public WhereItemContext handleSimpleTypeWithAnnotationMultiParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeWithAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return handleComplexTypeNoAnnotationSingleParam(columnInfo, columnInfoComposite);
        }

        @Override
        public WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s.%3$s[0]} and #{%1$s.%2$s.%3$s[1]}",
                    methodParamInfo.getArgName(),
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        protected String getParamValueExpression(List<String> pathItemList) {
            String path = StringUtils.join(pathItemList, ".");
            return String.format("#{%1$s[0]} and #{%1$s[1]}", path);
        }
    }

    static class CommonConditionHandler extends AbstractConditionHandler {

        @Override
        public WhereItemContext handleSimpleTypeSingleParam(ColumnInfo columnInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, null);
            String testExpression = this.getTestExpression(paramValuePathItemList);
            String paramValueExpression = this.getParamValueExpression(paramValuePathItemList);
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleSimpleTypeNoAnnotationSingleParam(ColumnInfo columnInfo) {
            return handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeNoAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, columnInfoComposite);
            String testExpression = this.getTestExpression(paramValuePathItemList);
            String paramValueExpression = this.getParamValueExpression(paramValuePathItemList);
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleSimpleTypeWithAnnotationSingleParam(ColumnInfo columnInfo) {
            return handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeWithAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return this.handleComplexTypeNoAnnotationSingleParam(columnInfo, columnInfoComposite);
        }

        @Override
        public WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null",
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s}",
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleSimpleTypeMultiParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleSimpleTypeNoAnnotationMultiParam(ColumnInfo columnInfo) {
            return handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeNoAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return handleComplexTypeNoAnnotationSingleParam(columnInfoComposite, columnInfoComposite);
        }

        @Override
        public WhereItemContext handleSimpleTypeWithAnnotationMultiParam(ColumnInfo columnInfo) {
            return handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeWithAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return handleComplexTypeNoAnnotationSingleParam(columnInfoComposite, columnInfoComposite);
        }

        @Override
        public WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String testExpression = String.format(
                    "%1$s != null and %1$s.%2$s != null and %1$s.%2$s.%3$s != null",
                    methodParamInfo.getArgName(),
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String paramValueExpression = String.format(
                    "#{%1$s.%2$s.%3$s}",
                    methodParamInfo.getArgName(),
                    relationColumnInfo.getJavaColumnName(),
                    referencedColumnInfo.getJavaColumnName()
            );
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }
    }

    static class NullConditionHandler extends AbstractConditionHandler {

        @Override
        public WhereItemContext handleSimpleTypeSingleParam(ColumnInfo columnInfo) {
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfo, "");
            return new WhereItemContext(null, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleSimpleTypeNoAnnotationSingleParam(ColumnInfo columnInfo) {
            return handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeNoAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, columnInfoComposite, "");
            return new WhereItemContext(null, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleSimpleTypeWithAnnotationSingleParam(ColumnInfo columnInfo) {
            return handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeWithAnnotationSingleParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return this.handleComplexTypeNoAnnotationSingleParam(columnInfo, columnInfoComposite);
        }

        @Override
        public WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, "");
            return new WhereItemContext(null, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleSimpleTypeMultiParam(ColumnInfo columnInfo) {
            return this.handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleSimpleTypeNoAnnotationMultiParam(ColumnInfo columnInfo) {
            return handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeNoAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return handleComplexTypeNoAnnotationSingleParam(columnInfoComposite, columnInfoComposite);
        }

        @Override
        public WhereItemContext handleSimpleTypeWithAnnotationMultiParam(ColumnInfo columnInfo) {
            return handleSimpleTypeSingleParam(columnInfo);
        }

        @Override
        public WhereItemContext handleComplexTypeWithAnnotationMultiParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            return handleComplexTypeNoAnnotationSingleParam(columnInfoComposite, columnInfoComposite);
        }

        @Override
        public WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyColumnInfo foreignKeyInfo) {
            String conditionExpression = this.getConditionExpression(logicOperator, comparisonOperator, relationColumnInfo, "");
            return new WhereItemContext(null, Arrays.asList(conditionExpression));
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
