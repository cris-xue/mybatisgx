package com.mybatisgx.dsl.mgxql;

import com.google.common.collect.Maps;
import com.mybatisgx.dsl.mgxql.syntax.MgxqlParser;
import com.mybatisgx.model.*;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * mgxql语法处理器
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class MgxqlSyntaxHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MgxqlSyntaxHandler.class);

    private static final Map<Class<?>, SqlCommandType> COMMAND_MAPPINGS = Maps.newHashMap();

    static {
        COMMAND_MAPPINGS.put(MgxqlParser.Insert_statementContext.class, SqlCommandType.INSERT);
        COMMAND_MAPPINGS.put(MgxqlParser.Delete_statementContext.class, SqlCommandType.DELETE);
        COMMAND_MAPPINGS.put(MgxqlParser.Update_statementContext.class, SqlCommandType.UPDATE);
        COMMAND_MAPPINGS.put(MgxqlParser.Select_statementContext.class, SqlCommandType.SELECT);
    }

    /**
     * MGXQL比较运算符映射表（SQL符号/关键字 → ComparisonOperator）
     */
    static final Map<String, ComparisonOperator> MGXQL_OPERATOR_MAP = new HashMap<>();

    static {
        // 关系运算符
        MGXQL_OPERATOR_MAP.put("<", ComparisonOperator.LT);
        MGXQL_OPERATOR_MAP.put("<=", ComparisonOperator.LT_EQ);
        MGXQL_OPERATOR_MAP.put(">", ComparisonOperator.GT);
        MGXQL_OPERATOR_MAP.put(">=", ComparisonOperator.GT_EQ);
        MGXQL_OPERATOR_MAP.put("=", ComparisonOperator.EQ);
        MGXQL_OPERATOR_MAP.put("!=", ComparisonOperator.NOT);
        // 匹配运算符
        MGXQL_OPERATOR_MAP.put("in", ComparisonOperator.IN);
        MGXQL_OPERATOR_MAP.put("like", ComparisonOperator.LIKE);
        MGXQL_OPERATOR_MAP.put("left like", ComparisonOperator.LIKE);
        MGXQL_OPERATOR_MAP.put("right like", ComparisonOperator.LIKE);
        MGXQL_OPERATOR_MAP.put("between", ComparisonOperator.BETWEEN);
        MGXQL_OPERATOR_MAP.put("not", ComparisonOperator.NOT);
        // NULL运算符
        MGXQL_OPERATOR_MAP.put("is null", ComparisonOperator.IS_NULL);
        MGXQL_OPERATOR_MAP.put("is not null", ComparisonOperator.IS_NOT_NULL);
    }

    /**
     * 语法节点处理器接口
     *
     * @author 薛承城
     * @date 2025/11/17 10:47
     */
    public interface SyntaxNodeHandler {

        int getOrder();

        boolean support(ParseTree node);

        void handle(ParseTree node, ParserContext context);
    }

    public static class SelectStatementHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Select_statementContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {

        }
    }

    public static class SelectItemHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Select_item_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql查询项: {}", node.getText());
            MethodInfo methodInfo = context.getMethodInfo();
            MgxqlParser.Select_item_clauseContext selectItemClauseContext = (MgxqlParser.Select_item_clauseContext) node;

            SelectItemInfo selectItemInfo = new SelectItemInfo();
            for (MgxqlParser.Select_itemContext selectItem : selectItemClauseContext.select_item()) {
                if (selectItem.select_column_all() != null) {
                    // select * 或 entity.*
                    selectItemInfo.setSelectItemType(SelectItemType.COLUMN);
                } else if (selectItem.select_column_custom() != null) {
                    // 自定义列：select id, name 或 entity.name
                    selectItemInfo.setSelectItemType(SelectItemType.COLUMN);
                    selectItemInfo.setExpression(selectItem.select_column_custom().getText());
                } else if (selectItem.aggregate_function() != null) {
                    // 聚合函数：count/max/min/avg
                    MgxqlParser.Aggregate_functionContext aggregateFunction = selectItem.aggregate_function();
                    if (aggregateFunction.select_count() != null) {
                        selectItemInfo.setSelectItemType(SelectItemType.COUNT);
                        selectItemInfo.setExpression("count");
                    } else if (aggregateFunction.select_max() != null) {
                        selectItemInfo.setExpression("max");
                    } else if (aggregateFunction.select_min() != null) {
                        selectItemInfo.setExpression("min");
                    } else if (aggregateFunction.select_avg() != null) {
                        selectItemInfo.setExpression("avg");
                    }
                }
            }
            methodInfo.setSelectItemInfo(selectItemInfo);
        }
    }

    public static class SelectFromClauseHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Select_from_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql from子句: {}", node.getText());
            // from子句包含实体名、别名和left join信息，由后续SQL构建阶段处理
        }
    }

    public static class WhereClauseHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Where_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql where条件: {}", node.getText());
            WhereClauseVisitor whereClauseVisitor = new WhereClauseVisitor(context);
            List<ConditionInfo> conditionInfoList = whereClauseVisitor.visit(node);
            context.getMethodInfo().setConditionInfoList(conditionInfoList);
        }
    }

    public static class OrderByHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Order_by_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql排序: {}", node.getText());
            List<SelectOrderByInfo> selectOrderByInfoList = new ArrayList<>();
            MgxqlParser.Order_by_clauseContext orderByClauseContext = (MgxqlParser.Order_by_clauseContext) node;
            for (MgxqlParser.Order_by_itemContext orderByItem : orderByClauseContext.order_by_item()) {
                String field = orderByItem.entity_field_access_chain().getText();
                String direction = orderByItem.order_by_direction() != null ? orderByItem.order_by_direction().getText() : "asc";
                selectOrderByInfoList.add(new SelectOrderByInfo(field, direction));
            }
            context.getMethodInfo().setSelectOrderByInfoList(selectOrderByInfoList);
        }
    }

    public static class LimitHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.LimitContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql分页: {}", node.getText());
            MethodInfo methodInfo = context.getMethodInfo();
            MgxqlParser.LimitContext limitContext = (MgxqlParser.LimitContext) node;

            int offset = Integer.parseInt(limitContext.offset().getText());
            int size = Integer.parseInt(limitContext.size().getText());
            MethodRowLimitInfo methodRowLimitInfo = new MethodRowLimitInfo(offset, size);
            methodInfo.setMethodRowLimitInfo(methodRowLimitInfo);
        }
    }

    public static class GroupByHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Group_by_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql group by: {}", node.getText());
            // group by子句处理，包含entity_field_access_chain列表
        }
    }

    public static class HavingHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Having_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql having: {}", node.getText());
            // having子句处理，包含聚合函数条件
        }
    }

    public static class WhereClauseVisitor extends MgxqlParserBaseVisitor<List<ConditionInfo>> {

        private AtomicInteger conditionIndex = new AtomicInteger(0);
        private ParserContext context;
        private ConditionTermParser conditionTermParser;

        public WhereClauseVisitor(ParserContext context) {
            this.context = context;
            this.conditionTermParser = new ConditionTermParser(context);
        }

        @Override
        public List<ConditionInfo> visitCondition_expression(MgxqlParser.Condition_expressionContext ctx) {
            LOGGER.debug("处理mgxql条件表达式: {}", ctx.getText());
            List<ConditionInfo> conditionInfoList = new ArrayList<>();
            MgxqlParser.Or_expressionContext orExpressionContext = ctx.or_expression();
            MgxqlParser.Logic_orContext logicOrContext = null;
            for (int i = 0; i < orExpressionContext.getChildCount(); i++) {
                ParseTree parseTree = orExpressionContext.getChild(i);
                if (parseTree instanceof MgxqlParser.Logic_orContext) {
                    logicOrContext = (MgxqlParser.Logic_orContext) parseTree;
                }
                if (parseTree instanceof MgxqlParser.And_expressionContext) {
                    List<ConditionInfo> andConditionInfoList = this.parseAndExpression((MgxqlParser.And_expressionContext) parseTree, logicOrContext);
                    conditionInfoList.addAll(andConditionInfoList);
                }
            }
            return conditionInfoList;
        }

        public Integer getConditionIndex() {
            return conditionIndex.getAndIncrement();
        }

        private List<ConditionInfo> parseAndExpression(MgxqlParser.And_expressionContext andExpressionContext, MgxqlParser.Logic_orContext logicOrContext) {
            List<ConditionInfo> conditionInfoList = new ArrayList<>();
            MgxqlParser.Logic_andContext logicAndContext = null;
            for (int i = 0; i < andExpressionContext.getChildCount(); i++) {
                ParseTree parseTree = andExpressionContext.getChild(i);
                if (parseTree instanceof MgxqlParser.Condition_termContext) {
                    MgxqlParser.Condition_termContext conditionTermContext = (MgxqlParser.Condition_termContext) parseTree;
                    MgxqlParser.Condition_expressionContext conditionExpressionContext = conditionTermContext.condition_expression();
                    if (conditionExpressionContext != null) {
                        List<ConditionInfo> childConditionInfoList = this.visitCondition_expression(conditionExpressionContext);
                        ConditionInfo conditionInfo = new ConditionInfo(this.getConditionIndex(), context.conditionOriginType, context.methodParamInfo);
                        this.setLogicOperator(conditionInfo, logicOrContext, logicAndContext);
                        this.handleBrackets(conditionTermContext, conditionInfo);
                        conditionInfo.setConditionInfoList(childConditionInfoList);
                        conditionInfoList.add(conditionInfo);
                    }
                    MgxqlParser.Field_comparison_opContext fieldComparisonOpContext = conditionTermContext.field_comparison_op();
                    if (fieldComparisonOpContext != null) {
                        ConditionInfo conditionInfo = this.conditionTermParser.parse(this.getConditionIndex(), fieldComparisonOpContext);
                        conditionInfo.setOriginSegment(conditionTermContext.getText());
                        this.setLogicOperator(conditionInfo, logicOrContext, logicAndContext);
                        conditionInfoList.add(conditionInfo);
                    }
                }
                if (parseTree instanceof MgxqlParser.Logic_andContext) {
                    logicAndContext = (MgxqlParser.Logic_andContext) parseTree;
                }
            }
            return conditionInfoList;
        }

        private void handleBrackets(MgxqlParser.Condition_termContext conditionTermContext, ConditionInfo conditionInfo) {
            MgxqlParser.Left_bracketContext leftBracketClauseContext = conditionTermContext.left_bracket();
            if (leftBracketClauseContext != null) {
                conditionInfo.setLeftBracket(leftBracketClauseContext.LEFT_BRACKET().getText());
            }
            MgxqlParser.Right_bracketContext rightBracketClauseContext = conditionTermContext.right_bracket();
            if (rightBracketClauseContext != null) {
                conditionInfo.setRightBracket(rightBracketClauseContext.RIGHT_BRACKET().getText());
            }
        }

        private void setLogicOperator(ConditionInfo conditionInfo, MgxqlParser.Logic_orContext logicOpOrClauseContext, MgxqlParser.Logic_andContext logicOpAndClauseContext) {
            if (logicOpOrClauseContext != null) {
                conditionInfo.setLogicOperator(LogicOperator.OR);
            } else if (logicOpAndClauseContext != null) {
                conditionInfo.setLogicOperator(LogicOperator.AND);
            } else {
                // 如果没有逻辑操作符，可能是第一个条件，保持默认值
                conditionInfo.setLogicOperator(LogicOperator.NULL);
            }
        }

        @Override
        public List<ConditionInfo> visitCondition_term(MgxqlParser.Condition_termContext ctx) {
            LOGGER.debug("处理mgxql条件单元: {}", ctx.getText());
            return super.visitCondition_term(ctx);
        }

        @Override
        protected boolean shouldVisitNextChild(RuleNode node, List<ConditionInfo> currentResult) {
            return node instanceof MgxqlParser.Where_clauseContext && currentResult == null;
        }
    }

    public static class ConditionTermParser {

        private ParserContext context;
        private Map<Class<?>, ConditionStrategy> strategies = new HashMap<>();

        public ConditionTermParser(ParserContext context) {
            this.context = context;
            // 字段访问链（左侧字段名）
            this.strategies.put(MgxqlParser.Where_param_name_field_access_chainContext.class, new FieldAccessChainStrategyHandler());
            // 关系运算符：<, <=, >, >=, =, !=
            this.strategies.put(MgxqlParser.Relational_opContext.class, new RelationalOperatorStrategyHandler());
            // 匹配运算符：like, in, between, left like, right like（含not前缀）
            this.strategies.put(MgxqlParser.Matching_opContext.class, new MatchingOperatorStrategyHandler());
            // NULL运算符：is null, is not null
            this.strategies.put(MgxqlParser.Comparison_op_nullContext.class, new ComparisonNullOperatorStrategyHandler());
            // 参数值访问链（右侧参数引用）
            this.strategies.put(MgxqlParser.Where_param_value_field_access_chainContext.class, new ParamValueAccessChainStrategyHandler());
        }

        public ConditionInfo parse(int index, MgxqlParser.Field_comparison_opContext ctx) {
            LOGGER.debug("处理mgxql条件: {}", ctx.getText());
            ConditionInfo conditionInfo = new ConditionInfo(index, context.conditionOriginType, context.methodParamInfo);
            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree child = ctx.getChild(i);
                ConditionStrategy strategy = strategies.get(child.getClass());
                if (strategy != null) {
                    strategy.apply(child, conditionInfo, context);
                }
            }
            return conditionInfo;
        }
    }

    /**
     * 策略接口
     */
    public interface ConditionStrategy {

        void apply(ParseTree node, ConditionInfo conditionInfo, ParserContext context);
    }

    /**
     * 字段访问链策略处理器，提取条件左侧的字段名
     */
    private static class FieldAccessChainStrategyHandler implements ConditionStrategy {

        @Override
        public void apply(ParseTree node, ConditionInfo conditionInfo, ParserContext context) {
            MgxqlParser.Where_param_name_field_access_chainContext accessChain = (MgxqlParser.Where_param_name_field_access_chainContext) node;
            MgxqlParser.Param_name_field_access_chainContext chainContext = accessChain.param_name_field_access_chain();
            // 提取字段名：可能是 entityAlias.fieldName 或单独的 fieldName
            String fieldName;
            if (chainContext.dot() != null) {
                fieldName = chainContext.field_name().getText();
            } else {
                fieldName = chainContext.entity_name_alias().getText();
            }
            conditionInfo.setColumnName(fieldName);
        }
    }

    /**
     * 关系运算符策略处理器，处理 <, <=, >, >=, =, !=
     */
    private static class RelationalOperatorStrategyHandler implements ConditionStrategy {

        @Override
        public void apply(ParseTree node, ConditionInfo conditionInfo, ParserContext context) {
            String token = node.getText();
            ComparisonOperator operator = MGXQL_OPERATOR_MAP.get(token);
            if (operator != null) {
                conditionInfo.setComparisonOperator(operator);
            }
        }
    }

    /**
     * 匹配运算符策略处理器，处理 like, in, between, left like, right like（含not前缀）
     */
    private static class MatchingOperatorStrategyHandler implements ConditionStrategy {

        @Override
        public void apply(ParseTree node, ConditionInfo conditionInfo, ParserContext context) {
            MgxqlParser.Matching_opContext matchingOpContext = (MgxqlParser.Matching_opContext) node;
            // 检查是否有not前缀
            if (matchingOpContext.comparison_op_not() != null) {
                conditionInfo.setComparisonNotOperator(ComparisonOperator.NOT);
            }
            // 获取实际的匹配运算符文本（不含not前缀）
            String token = node.getText();
            if (token.startsWith("not")) {
                token = token.substring(3).trim();
            }
            ComparisonOperator operator = MGXQL_OPERATOR_MAP.get(token);
            if (operator != null) {
                conditionInfo.setComparisonOperator(operator);
            }
        }
    }

    /**
     * NULL运算符策略处理器，处理 is null, is not null
     */
    private static class ComparisonNullOperatorStrategyHandler implements ConditionStrategy {

        @Override
        public void apply(ParseTree node, ConditionInfo conditionInfo, ParserContext context) {
            String token = node.getText();
            ComparisonOperator operator = MGXQL_OPERATOR_MAP.get(token);
            if (operator != null) {
                conditionInfo.setComparisonOperator(operator);
            }
        }
    }

    /**
     * 参数值访问链策略处理器，提取条件右侧的参数引用路径
     */
    private static class ParamValueAccessChainStrategyHandler implements ConditionStrategy {

        @Override
        public void apply(ParseTree node, ConditionInfo conditionInfo, ParserContext context) {
            MgxqlParser.Where_param_value_field_access_chainContext accessChain = (MgxqlParser.Where_param_value_field_access_chainContext) node;
            MgxqlParser.Param_value_field_access_chainContext chainContext = accessChain.param_value_field_access_chain();
            // 提取参数值路径，如 name 或 role.menu.name
            List<String> pathItems = new ArrayList<>();
            for (MgxqlParser.Field_nameContext fieldNameContext : chainContext.field_name()) {
                pathItems.add(fieldNameContext.getText());
            }
            conditionInfo.setParamValueCommonPathItemList(pathItems);
        }
    }

    public static class ParserContext {

        private CommonTokenStream tokens;
        private ParseTree root;
        private EntityInfo entityInfo;
        private MethodInfo methodInfo;
        private MethodParamInfo methodParamInfo;
        private ConditionOriginType conditionOriginType;
        private String statementExpression;

        public ParserContext(
                CommonTokenStream tokens,
                ParseTree root,
                EntityInfo entityInfo,
                MethodInfo methodInfo,
                MethodParamInfo methodParamInfo,
                ConditionOriginType conditionOriginType,
                String statementExpression
        ) {
            this.tokens = tokens;
            this.root = root;
            this.entityInfo = entityInfo;
            this.methodInfo = methodInfo;
            this.methodParamInfo = methodParamInfo;
            this.conditionOriginType = conditionOriginType;
            this.statementExpression = statementExpression;
        }

        public CommonTokenStream getTokens() {
            return tokens;
        }

        public ParseTree getRoot() {
            return root;
        }

        public EntityInfo getEntityInfo() {
            return entityInfo;
        }

        public MethodInfo getMethodInfo() {
            return methodInfo;
        }

        public MethodParamInfo getMethodParamInfo() {
            return methodParamInfo;
        }

        public ConditionOriginType getConditionOriginType() {
            return conditionOriginType;
        }

        public String getStatementExpression() {
            return statementExpression;
        }
    }
}
