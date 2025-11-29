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
            List<ConditionInfo> childConditionInfoList = conditionInfo.getConditionInfoList();
            if (ObjectUtils.isNotEmpty(childConditionInfoList)) {
                // 处理分组的括号
                whereElement.addText(String.format(" %s %s", conditionInfo.getLogicOperator(), conditionInfo.getLeftBracket()));
                this.handleConditionGroup(methodInfo, whereElement, childConditionInfoList, factory);
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

        WhereItemContext handleSimpleTypeParam(ColumnInfo columnInfo);

        WhereItemContext handleComplexTypeParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite);

        WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo);

        WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo);
    }

    static abstract class AbstractConditionHandler implements ConditionHandler {

        protected ConditionInfo conditionInfo;
        protected ColumnInfo columnInfo;
        protected Integer columnInfoCompositeIndex;
        protected MethodParamInfo methodParamInfo;
        protected LogicOperator logicOperator;
        protected ComparisonOperator comparisonOperator;

        @Override
        public void handle(MethodInfo methodInfo, ConditionInfo conditionInfo, Element whereElement) {
            this.conditionInfo = conditionInfo;
            this.columnInfo = conditionInfo.getColumnInfo();
            this.columnInfoCompositeIndex = 0;
            this.methodParamInfo = conditionInfo.getMethodParamInfo();
            this.logicOperator = conditionInfo.getLogicOperator();
            this.comparisonOperator = conditionInfo.getComparisonOperator();

            List<WhereItemContext> whereItemContextList = new ArrayList();
            int paramCount = methodInfo.getMethodParamInfoList().size();
            if (this.conditionInfo.getComparisonOperator().isNullComparisonOperator()) {
                this.noParamHandle(whereItemContextList);
            } else if (paramCount == 1) {
                this.singleParamHandle(whereItemContextList);
            } else {
                this.multiParamHandle(whereItemContextList);
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

        public void noParamHandle(List<WhereItemContext> whereItemContextList) {
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                WhereItemContext whereItemContext = this.handleSimpleTypeParam(columnInfo);
                whereItemContextList.add(whereItemContext);
            }
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                if (relationColumnInfo.getMappedByRelationColumnInfo() == null) {
                    for (ForeignKeyInfo foreignKeyInfo : relationColumnInfo.getInverseForeignKeyInfoList()) {
                        WhereItemContext whereItemContext = this.handleRelationColumnMultiParam(relationColumnInfo, foreignKeyInfo);
                        whereItemContextList.add(whereItemContext);
                    }
                }
            }
        }

        public void singleParamHandle(List<WhereItemContext> whereItemContextList) {
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                ClassCategory classCategory = this.getParamClassCategory();
                if (classCategory == ClassCategory.SIMPLE) {
                    WhereItemContext whereItemContext = this.handleSimpleTypeParam(columnInfo);
                    whereItemContextList.add(whereItemContext);
                }
                if (classCategory == ClassCategory.COMPLEX) {
                    for (ColumnInfo columnInfoComposite : methodParamInfo.getColumnInfoList()) {
                        this.columnInfoCompositeIndex++;
                        WhereItemContext whereItemContext = this.handleComplexTypeParam(columnInfo, columnInfoComposite);
                        whereItemContextList.add(whereItemContext);
                    }
                }
            }
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                if (relationColumnInfo.getMappedByRelationColumnInfo() == null) {
                    for (ForeignKeyInfo foreignKeyInfo : relationColumnInfo.getInverseForeignKeyInfoList()) {
                        this.columnInfoCompositeIndex++;
                        WhereItemContext whereItemContext = this.handleRelationColumnSingleParam(relationColumnInfo, foreignKeyInfo);
                        whereItemContextList.add(whereItemContext);
                    }
                }
            }
        }

        public void multiParamHandle(List<WhereItemContext> whereItemContextList) {
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class, ColumnInfo.class)) {
                ClassCategory classCategory = this.getParamClassCategory();
                if (classCategory == ClassCategory.SIMPLE) {
                    WhereItemContext whereItemContext = this.handleSimpleTypeParam(columnInfo);
                    whereItemContextList.add(whereItemContext);
                }
                if (classCategory == ClassCategory.COMPLEX) {
                    for (ColumnInfo columnInfoComposite : methodParamInfo.getColumnInfoList()) {
                        this.columnInfoCompositeIndex++;
                        WhereItemContext whereItemContext = this.handleComplexTypeParam(columnInfo, columnInfoComposite);
                        whereItemContextList.add(whereItemContext);
                    }
                }
            }
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                if (relationColumnInfo.getMappedByRelationColumnInfo() == null) {
                    for (ForeignKeyInfo foreignKeyInfo : relationColumnInfo.getInverseForeignKeyInfoList()) {
                        this.columnInfoCompositeIndex++;
                        WhereItemContext whereItemContext = this.handleRelationColumnMultiParam(relationColumnInfo, foreignKeyInfo);
                        whereItemContextList.add(whereItemContext);
                    }
                }
            }
        }

        private ClassCategory getParamClassCategory() {
            if (methodParamInfo.getClassCategory() == ClassCategory.SIMPLE) {
                // findById(Long id) findById(@Param("id") Long id)
                return ClassCategory.SIMPLE;
            }
            if (methodParamInfo.getClassCategory() == ClassCategory.COMPLEX) {
                // findById(MultiId id) findById(@Param("id") MultiId id)
                if (ObjectUtils.isEmpty(methodParamInfo.getColumnInfoList())) {
                    return ClassCategory.SIMPLE;
                } else {
                    return ClassCategory.COMPLEX;
                }
            }
            throw new RuntimeException("columnInfoClassCategory is null");
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
         * @param columnInfo
         * @param paramValueExpression #{userId} or #{user.userId}
         * @return and user_id = #{userId} or and user_id = #{user.userId}
         */
        protected String getConditionExpression(ColumnInfo columnInfo, String paramValueExpression) {
            LogicOperator logicOperator = this.logicOperator;
            if (this.columnInfoCompositeIndex >= 2) {
                logicOperator = LogicOperator.AND;
            }
            List<String> expressionItemList = Lists.newArrayList(
                    logicOperator.getValue(),
                    columnInfo.getDbColumnName(),
                    comparisonOperator.getValue(),
                    paramValueExpression
            );
            ComparisonOperator comparisonNotOperator = this.conditionInfo.getComparisonNotOperator();
            if (comparisonNotOperator != null) {
                expressionItemList.add(2, comparisonNotOperator.getValue());
            }
            return StringUtils.SPACE + StringUtils.join(expressionItemList, StringUtils.SPACE);
        }

        protected Element buildWhereOrIfElement(Element whereElement, Boolean dynamic, String testExpression) {
            if (this.comparisonOperator.isNullComparisonOperator()) {
                return whereElement;
            }
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
        public WhereItemContext handleSimpleTypeParam(ColumnInfo columnInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, null);
            return this.buildWhereItemContext(columnInfo, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleComplexTypeParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, columnInfoComposite);
            return this.buildWhereItemContext(columnInfoComposite, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo) {
            throw new UnsupportedOperationException("多对多关系字段不支持模糊查询");
        }

        @Override
        public WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo) {
            throw new UnsupportedOperationException("多对多关系字段不支持模糊查询");
        }

        /**
         * 构建WhereItemContext
         * @param columnInfo 数据库列字段信息
         * @param paramValuePathItemList 参数值路径
         * @return
         */
        private WhereItemContext buildWhereItemContext(ColumnInfo columnInfo, List<String> paramValuePathItemList) {
            String testExpression = this.getTestExpression(paramValuePathItemList);
            String bindKey = this.getBindKey(paramValuePathItemList);
            String bindValuePath = this.getBindValuePath(paramValuePathItemList);
            Element likeBindElement = this.buildLikeBindElement(bindKey, bindValuePath);
            String paramValueExpression = this.getParamValueExpression(Arrays.asList(bindKey));
            String conditionExpression = this.getConditionExpression(columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(likeBindElement, conditionExpression));
        }

        private String getBindKey(List<String> pathItemList) {
            return StringUtils.join(pathItemList, "_");
        }

        private String getBindValuePath(List<String> pathItemList) {
            return StringUtils.join(pathItemList, ".");
        }

        protected Element buildLikeBindElement(String bindKey, String bindValuePath) {
            String likeExpression = "'%'+" + bindValuePath + "+'%'";
            return this.buildBindElement(bindKey, likeExpression);
        }
    }

    static class StartingLikeConditionHandler extends LikeConditionHandler {

        @Override
        protected Element buildLikeBindElement(String bindKey, String bindValuePath) {
            String likeExpression = "'%'+" + bindValuePath;
            return this.buildBindElement(bindKey, likeExpression);
        }
    }

    static class EndingLikeConditionHandler extends LikeConditionHandler {

        @Override
        protected Element buildLikeBindElement(String bindKey, String bindValuePath) {
            String likeExpression = bindValuePath + "+'%'";
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
        public WhereItemContext handleSimpleTypeParam(ColumnInfo columnInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, null);
            return this.buildWhereItemContext(columnInfo, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleComplexTypeParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, columnInfoComposite);
            return this.buildWhereItemContext(columnInfoComposite, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(relationColumnInfo, foreignKeyInfo.getReferencedColumnInfo());
            return this.buildWhereItemContext(foreignKeyInfo.getColumnInfo(), paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(relationColumnInfo, foreignKeyInfo.getReferencedColumnInfo());
            return this.buildWhereItemContext(foreignKeyInfo.getColumnInfo(), paramValuePathItemList);
        }

        /**
         * 构建WhereItemContext
         * @param columnInfo 数据库列字段信息
         * @param paramValuePathItemList 参数值路径
         * @return
         */
        private WhereItemContext buildWhereItemContext(ColumnInfo columnInfo, List<String> paramValuePathItemList) {
            String testExpression = this.getTestExpression(paramValuePathItemList);
            String paramValueExpression = this.getParamValueExpression(paramValuePathItemList);
            String conditionExpression = this.getConditionExpression(columnInfo, "");
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
        public WhereItemContext handleSimpleTypeParam(ColumnInfo columnInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, null);
            return this.buildWhereItemContext(columnInfo, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleComplexTypeParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, columnInfoComposite);
            return this.buildWhereItemContext(columnInfoComposite, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(relationColumnInfo, foreignKeyInfo.getReferencedColumnInfo());
            return this.buildWhereItemContext(foreignKeyInfo.getColumnInfo(), paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(relationColumnInfo, foreignKeyInfo.getReferencedColumnInfo());
            return this.buildWhereItemContext(foreignKeyInfo.getColumnInfo(), paramValuePathItemList);
        }

        @Override
        protected String getParamValueExpression(List<String> pathItemList) {
            String path = StringUtils.join(pathItemList, ".");
            return String.format("#{%1$s[0]} and #{%1$s[1]}", path);
        }

        /**
         * 构建WhereItemContext
         * @param columnInfo 数据库列字段信息
         * @param paramValuePathItemList 参数值路径
         * @return
         */
        private WhereItemContext buildWhereItemContext(ColumnInfo columnInfo, List<String> paramValuePathItemList) {
            String testExpression = this.getTestExpression(paramValuePathItemList);
            String paramValueExpression = this.getParamValueExpression(paramValuePathItemList);
            String conditionExpression = this.getConditionExpression(columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }
    }

    static class CommonConditionHandler extends AbstractConditionHandler {

        @Override
        public WhereItemContext handleSimpleTypeParam(ColumnInfo columnInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, null);
            return this.buildWhereItemContext(columnInfo, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleComplexTypeParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(columnInfo, columnInfoComposite);
            return this.buildWhereItemContext(columnInfoComposite, paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(relationColumnInfo, foreignKeyInfo.getReferencedColumnInfo());
            return this.buildWhereItemContext(foreignKeyInfo.getColumnInfo(), paramValuePathItemList);
        }

        @Override
        public WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo) {
            List<String> paramValuePathItemList = this.getParamValuePathItemList(relationColumnInfo, foreignKeyInfo.getReferencedColumnInfo());
            return this.buildWhereItemContext(foreignKeyInfo.getColumnInfo(), paramValuePathItemList);
        }

        /**
         * 构建WhereItemContext
         * @param columnInfo 数据库列字段信息
         * @param paramValuePathItemList 参数值路径
         * @return
         */
        private WhereItemContext buildWhereItemContext(ColumnInfo columnInfo, List<String> paramValuePathItemList) {
            String testExpression = this.getTestExpression(paramValuePathItemList);
            String paramValueExpression = this.getParamValueExpression(paramValuePathItemList);
            String conditionExpression = this.getConditionExpression(columnInfo, paramValueExpression);
            return new WhereItemContext(testExpression, Arrays.asList(conditionExpression));
        }
    }

    static class NullConditionHandler extends AbstractConditionHandler {

        @Override
        public WhereItemContext handleSimpleTypeParam(ColumnInfo columnInfo) {
            String conditionExpression = this.getConditionExpression(columnInfo, "");
            return new WhereItemContext(null, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleComplexTypeParam(ColumnInfo columnInfo, ColumnInfo columnInfoComposite) {
            String conditionExpression = this.getConditionExpression(columnInfoComposite, "");
            return new WhereItemContext(null, Arrays.asList(conditionExpression));
        }

        @Override
        public WhereItemContext handleRelationColumnSingleParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo) {
            throw new UnsupportedOperationException("不支持单参数关系字段");
        }

        @Override
        public WhereItemContext handleRelationColumnMultiParam(RelationColumnInfo relationColumnInfo, ForeignKeyInfo foreignKeyInfo) {
            throw new UnsupportedOperationException("不支持多参数关系字段");
        }
    }

    static class TemplateParamContext {

        private ConditionInfo conditionInfo;
        private ColumnInfo columnInfo;
        private ColumnInfo columnInfoComposite;
        private MethodParamInfo methodParamInfo;
        private LogicOperator logicOperator;
        private ComparisonOperator comparisonOperator;

        public TemplateParamContext(ConditionInfo conditionInfo) {
            this.conditionInfo = conditionInfo;
            this.columnInfo = conditionInfo.getColumnInfo();
            this.methodParamInfo = conditionInfo.getMethodParamInfo();
            this.logicOperator = conditionInfo.getLogicOperator();
            this.comparisonOperator = conditionInfo.getComparisonOperator();
        }

        public ColumnInfo getColumnInfo() {
            return columnInfo;
        }

        public MethodParamInfo getMethodParamInfo() {
            return methodParamInfo;
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
