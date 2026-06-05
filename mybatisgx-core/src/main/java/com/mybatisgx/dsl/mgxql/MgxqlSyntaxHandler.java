package com.mybatisgx.dsl.mgxql;

import com.google.common.collect.Maps;
import com.mybatisgx.dsl.mgxql.model.SelectItemType;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.dsl.mgxql.syntax.MgxqlParser;
import com.mybatisgx.dsl.mgxql.syntax.MgxqlParserBaseVisitor;
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

/**
 * mgxql语法处理器，负责将ANTLR语法树转换为MgxqlStatement模型
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
        MGXQL_OPERATOR_MAP.put("left like", ComparisonOperator.STARTING_WITH);
        MGXQL_OPERATOR_MAP.put("right like", ComparisonOperator.ENDING_WITH);
        MGXQL_OPERATOR_MAP.put("between", ComparisonOperator.BETWEEN);
        MGXQL_OPERATOR_MAP.put("not", ComparisonOperator.NOT);
        // NULL运算符
        MGXQL_OPERATOR_MAP.put("is null", ComparisonOperator.IS_NULL);
        MGXQL_OPERATOR_MAP.put("is not null", ComparisonOperator.IS_NOT_NULL);
    }

    /**
     * 语法节点处理器接口
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
            LOGGER.debug("处理mgxql select语句: {}", node.getText());
            context.getMgxqlStatement().setCommandType(SqlCommandType.SELECT);
        }
    }

    public static class SelectItemHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 1;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Select_item_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql查询项: {}", node.getText());
            MgxqlStatement statement = context.getMgxqlStatement();
            MgxqlParser.Select_item_clauseContext selectItemClauseContext = (MgxqlParser.Select_item_clauseContext) node;

            for (MgxqlParser.Select_itemContext selectItem : selectItemClauseContext.select_item()) {
                SelectItem item = new SelectItem();
                if (selectItem.select_column_all() != null) {
                    item.setType(SelectItemType.COLUMN_ALL);
                    MgxqlParser.Select_column_allContext columnAll = selectItem.select_column_all();
                    if (columnAll.entity_name_alias() != null) {
                        item.setEntityAlias(columnAll.entity_name_alias().getText());
                    }
                    item.setFieldName("*");
                } else if (selectItem.select_column_custom() != null) {
                    item.setType(SelectItemType.COLUMN);
                    MgxqlParser.Select_column_customContext columnCustom = selectItem.select_column_custom();
                    FieldReference fieldRef = parseFieldReference(columnCustom.field_reference());
                    item.setEntityAlias(fieldRef.getEntityAlias());
                    item.setFieldName(fieldRef.getFieldName());
                } else if (selectItem.aggregate_function() != null) {
                    MgxqlParser.Aggregate_functionContext aggregateFunction = selectItem.aggregate_function();
                    parseAggregateFunction(item, aggregateFunction);
                }
                statement.addSelectItem(item);
            }
        }

        private void parseAggregateFunction(SelectItem item, MgxqlParser.Aggregate_functionContext aggregateFunction) {
            MgxqlParser.Aggregate_function_nameContext funcNameCtx = aggregateFunction.aggregate_function_name();
            MgxqlParser.Aggregate_function_argumentContext funcArgCtx = aggregateFunction.aggregate_function_argument();
            String argText = parseFieldReference(funcArgCtx.field_reference()).getFieldName();

            if (funcNameCtx.select_count() != null) {
                item.setType(SelectItemType.COUNT);
            } else if (funcNameCtx.select_max() != null) {
                item.setType(SelectItemType.MAX);
            } else if (funcNameCtx.select_min() != null) {
                item.setType(SelectItemType.MIN);
            } else if (funcNameCtx.select_avg() != null) {
                item.setType(SelectItemType.AVG);
            } else if (funcNameCtx.select_sum() != null) {
                item.setType(SelectItemType.SUM);
            }
            item.setAggregateArg(argText);
        }
    }

    public static class SelectFromClauseHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 2;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Select_from_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql from子句: {}", node.getText());
            MgxqlStatement statement = context.getMgxqlStatement();
            MgxqlParser.Select_from_clauseContext fromClauseCtx = (MgxqlParser.Select_from_clauseContext) node;

            FromClause fromClause = new FromClause();

            // 解析主实体
            MgxqlParser.Select_primary_entityContext primaryEntityCtx = fromClauseCtx.select_primary_entity();
            String primaryEntityName = primaryEntityCtx.select_entity().getText();
            String primaryAlias = primaryEntityCtx.select_entity_alias() != null ? primaryEntityCtx.select_entity_alias().getText() : null;
            fromClause.setPrimaryEntity(new FromEntity(primaryEntityName, primaryAlias));

            // 解析LEFT JOIN实体
            List<MgxqlParser.Select_join_entityContext> joinEntities = fromClauseCtx.select_join_entity();
            if (joinEntities != null) {
                for (MgxqlParser.Select_join_entityContext joinEntityCtx : joinEntities) {
                    String joinEntityName = joinEntityCtx.select_entity().getText();
                    String joinAlias = joinEntityCtx.select_entity_alias() != null ? joinEntityCtx.select_entity_alias().getText() : null;
                    fromClause.addJoinEntity(new JoinEntity(joinEntityName, joinAlias, JoinType.LEFT));
                }
            }

            statement.setFromClause(fromClause);
        }
    }

    public static class WhereClauseHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 3;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Where_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql where条件: {}", node.getText());
            MgxqlStatement statement = context.getMgxqlStatement();
            WhereClauseVisitor whereClauseVisitor = new WhereClauseVisitor(context);
            ConditionExpression rootExpression = whereClauseVisitor.visit(node);
            statement.setWhereClause(new WhereClause(rootExpression));
        }
    }

    public static class OrderByHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 5;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Order_by_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql排序: {}", node.getText());
            MgxqlStatement statement = context.getMgxqlStatement();
            MgxqlParser.Order_by_clauseContext orderByClauseCtx = (MgxqlParser.Order_by_clauseContext) node;

            OrderByClause orderByClause = new OrderByClause();
            for (MgxqlParser.Order_by_expressionContext orderByExpr : orderByClauseCtx.order_by_expression()) {
                FieldReference fieldRef = parseFieldReference(orderByExpr.field_reference());
                String direction = orderByExpr.order_by_direction() != null ? orderByExpr.order_by_direction().getText() : "asc";
                orderByClause.addItem(new OrderByItem(fieldRef, direction));
            }
            statement.setOrderByClause(orderByClause);
        }
    }

    public static class LimitHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 6;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Limit_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql分页: {}", node.getText());
            MgxqlStatement statement = context.getMgxqlStatement();
            MgxqlParser.Limit_clauseContext limitCtx = (MgxqlParser.Limit_clauseContext) node;

            int offset = Integer.parseInt(limitCtx.offset().getText());
            int size = Integer.parseInt(limitCtx.size().getText());
            statement.setLimitClause(new LimitClause(offset, size));
        }
    }

    public static class GroupByHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 7;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Group_by_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql group by: {}", node.getText());
            MgxqlStatement statement = context.getMgxqlStatement();
            MgxqlParser.Group_by_clauseContext groupByCtx = (MgxqlParser.Group_by_clauseContext) node;

            GroupByClause groupByClause = new GroupByClause();
            MgxqlParser.Group_by_expressionContext groupByExprCtx = groupByCtx.group_by_expression();
            for (MgxqlParser.Field_referenceContext fieldRefCtx : groupByExprCtx.field_reference()) {
                groupByClause.addField(parseFieldReference(fieldRefCtx));
            }
            statement.setGroupByClause(groupByClause);
        }
    }

    public static class HavingHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 8;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Having_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql having: {}", node.getText());
            MgxqlStatement statement = context.getMgxqlStatement();
            MgxqlParser.Having_clauseContext havingCtx = (MgxqlParser.Having_clauseContext) node;

            HavingClause havingClause = new HavingClause();
            MgxqlParser.Having_or_expressionContext havingOrExpr = havingCtx.having_or_expression();
            parseHavingOrExpression(havingOrExpr, havingClause);

            statement.setHavingClause(havingClause);
        }

        private void parseHavingOrExpression(MgxqlParser.Having_or_expressionContext ctx, HavingClause havingClause) {
            for (MgxqlParser.Having_and_expressionContext andExpr : ctx.having_and_expression()) {
                parseHavingAndExpression(andExpr, havingClause);
            }
        }

        private void parseHavingAndExpression(MgxqlParser.Having_and_expressionContext ctx, HavingClause havingClause) {
            for (MgxqlParser.Having_termContext term : ctx.having_term()) {
                MgxqlParser.Having_comparisonContext comparisonCtx = term.having_comparison();
                if (comparisonCtx != null) {
                    HavingCondition condition = new HavingCondition();
                    // 解析聚合函数
                    SelectItem aggItem = new SelectItem();
                    new SelectItemHandler().parseAggregateFunction(aggItem, comparisonCtx.aggregate_function());
                    condition.setAggregateFunction(aggItem);
                    // 解析运算符
                    condition.setOperator(MGXQL_OPERATOR_MAP.getOrDefault(
                            comparisonCtx.relational_op().getText(), ComparisonOperator.EQ));
                    // 解析比较值
                    MgxqlParser.Having_valueContext havingValueCtx = comparisonCtx.having_value();
                    if (havingValueCtx.parameter_reference() != null) {
                        condition.setParamValuePath(parseParameterReference(havingValueCtx.parameter_reference()));
                    } else if (havingValueCtx.number() != null) {
                        condition.setHavingValue(Integer.parseInt(havingValueCtx.number().getText()));
                    }
                    havingClause.addCondition(condition);
                }
            }
        }
    }

    /**
     * 解析Field_referenceContext为FieldReference
     */
    static FieldReference parseFieldReference(MgxqlParser.Field_referenceContext fieldRefCtx) {
        FieldReference fieldRef = new FieldReference();
        if (fieldRefCtx.dot() != null && fieldRefCtx.dot().size() > 0) {
            fieldRef.setEntityAlias(fieldRefCtx.entity_name_alias().getText());
            fieldRef.setFieldName(fieldRefCtx.field_name().getText());
        } else {
            fieldRef.setFieldName(fieldRefCtx.field_name().getText());
        }
        return fieldRef;
    }

    /**
     * 解析Parameter_referenceContext为参数路径列表
     */
    static List<String> parseParameterReference(MgxqlParser.Parameter_referenceContext paramRefCtx) {
        List<String> pathItems = new ArrayList<>();
        for (MgxqlParser.Field_nameContext fieldNameCtx : paramRefCtx.field_name()) {
            pathItems.add(fieldNameCtx.getText());
        }
        return pathItems;
    }

    /**
     * WHERE子句访问器，构建ConditionExpression树形结构
     */
    public static class WhereClauseVisitor extends MgxqlParserBaseVisitor<ConditionExpression> {

        private ParserContext context;

        public WhereClauseVisitor(ParserContext context) {
            this.context = context;
        }

        @Override
        public ConditionExpression visitCondition_or_expression(MgxqlParser.Condition_or_expressionContext ctx) {
            LOGGER.debug("处理mgxql条件表达式: {}", ctx.getText());
            ConditionExpression expression = new ConditionExpression(LogicOperator.NULL);
            LogicOperator currentOrOp = null;

            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree child = ctx.getChild(i);
                if (child instanceof MgxqlParser.Logic_orContext) {
                    currentOrOp = LogicOperator.OR;
                }
                if (child instanceof MgxqlParser.Condition_and_expressionContext) {
                    List<ConditionNode> andNodes = parseAndExpression((MgxqlParser.Condition_and_expressionContext) child);
                    if (currentOrOp != null && !expression.getNodes().isEmpty()) {
                        for (ConditionNode node : andNodes) {
                            node.setLogicOperator(currentOrOp);
                            expression.addNode(node);
                        }
                        currentOrOp = null;
                    } else {
                        for (ConditionNode node : andNodes) {
                            expression.addNode(node);
                        }
                    }
                }
            }
            return expression;
        }

        private List<ConditionNode> parseAndExpression(MgxqlParser.Condition_and_expressionContext andExprCtx) {
            List<ConditionNode> nodes = new ArrayList<>();
            LogicOperator currentAndOp = null;

            for (int i = 0; i < andExprCtx.getChildCount(); i++) {
                ParseTree child = andExprCtx.getChild(i);
                if (child instanceof MgxqlParser.Logic_andContext) {
                    currentAndOp = LogicOperator.AND;
                }
                if (child instanceof MgxqlParser.Condition_termContext) {
                    MgxqlParser.Condition_termContext termCtx = (MgxqlParser.Condition_termContext) child;
                    ConditionNode node = parseConditionTerm(termCtx);
                    if (currentAndOp != null) {
                        node.setLogicOperator(currentAndOp);
                        currentAndOp = null;
                    }
                    nodes.add(node);
                }
            }
            return nodes;
        }

        private ConditionNode parseConditionTerm(MgxqlParser.Condition_termContext termCtx) {
            ConditionNode node = new ConditionNode();

            // 检查是否是括号表达式
            MgxqlParser.Condition_or_expressionContext subExprCtx = termCtx.condition_or_expression();
            if (subExprCtx != null) {
                node.setLeftBracket("(");
                node.setRightBracket(")");
                node.setSubExpression(this.visitCondition_or_expression(subExprCtx));
                return node;
            }

            // 基础条件：condition_comparison
            MgxqlParser.Condition_comparisonContext compCtx = termCtx.condition_comparison();
            if (compCtx != null) {
                parseConditionComparison(node, compCtx);
            }
            return node;
        }

        private void parseConditionComparison(ConditionNode node, MgxqlParser.Condition_comparisonContext compCtx) {
            // 解析 ? 前缀（可选条件）
            node.setOptional(compCtx.question_mark() != null);

            // 解析左侧字段引用
            FieldReference fieldRef = parseFieldReference(compCtx.field_reference());
            node.setFieldAlias(fieldRef.getEntityAlias());
            node.setFieldName(fieldRef.getFieldName());

            // 解析运算符和参数
            MgxqlParser.Condition_comparison_paramContext paramCtx = compCtx.condition_comparison_param();
            if (paramCtx != null) {
                // 关系运算符或匹配运算符
                if (paramCtx.relational_op() != null) {
                    node.setOperator(MGXQL_OPERATOR_MAP.get(paramCtx.relational_op().getText()));
                }
                if (paramCtx.matching_op() != null) {
                    MgxqlParser.Matching_opContext matchingOp = paramCtx.matching_op();
                    if (matchingOp.comparison_op_not() != null) {
                        node.setNotOperator(ComparisonOperator.NOT);
                    }
                    String token = matchingOp.getText();
                    if (token.startsWith("not")) {
                        token = token.substring(3).trim();
                    }
                    node.setOperator(MGXQL_OPERATOR_MAP.get(token));
                }
                // 解析右侧条件值
                MgxqlParser.Condition_valueContext condValueCtx = paramCtx.condition_value();
                if (condValueCtx.parameter_reference() != null) {
                    node.setParamValuePath(parseParameterReference(condValueCtx.parameter_reference()));
                } else if (condValueCtx.number() != null) {
                    node.setConditionValue(Integer.parseInt(condValueCtx.number().getText()));
                }
            }

            // NULL运算符：is null / is not null
            MgxqlParser.Condition_comparison_not_paramContext notParamCtx = compCtx.condition_comparison_not_param();
            if (notParamCtx != null) {
                MgxqlParser.Comparison_op_nullContext nullOpCtx = notParamCtx.comparison_op_null();
                node.setOperator(MGXQL_OPERATOR_MAP.get(nullOpCtx.getText()));
            }
        }

        @Override
        protected boolean shouldVisitNextChild(RuleNode node, ConditionExpression currentResult) {
            return node instanceof MgxqlParser.Where_clauseContext && currentResult == null;
        }
    }

    /**
     * 解析器上下文，携带MgxqlStatement模型
     */
    public static class ParserContext {

        private CommonTokenStream tokens;
        private ParseTree root;
        private EntityInfo entityInfo;
        private MethodInfo methodInfo;
        private MethodParamInfo methodParamInfo;
        private ConditionOriginType conditionOriginType;
        private String statementExpression;
        private MgxqlStatement mgxqlStatement;

        public ParserContext(
                CommonTokenStream tokens,
                ParseTree root,
                EntityInfo entityInfo,
                MethodInfo methodInfo,
                MethodParamInfo methodParamInfo,
                ConditionOriginType conditionOriginType,
                String statementExpression,
                MgxqlStatement mgxqlStatement
        ) {
            this.tokens = tokens;
            this.root = root;
            this.entityInfo = entityInfo;
            this.methodInfo = methodInfo;
            this.methodParamInfo = methodParamInfo;
            this.conditionOriginType = conditionOriginType;
            this.statementExpression = statementExpression;
            this.mgxqlStatement = mgxqlStatement;
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

        public MgxqlStatement getMgxqlStatement() {
            return mgxqlStatement;
        }
    }
}
