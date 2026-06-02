package com.mybatisgx.dsl.mgxql;

import com.google.common.collect.Maps;
import com.mybatisgx.dsl.mgxql.model.ConditionExpression;
import com.mybatisgx.dsl.mgxql.model.ConditionNode;
import com.mybatisgx.dsl.mgxql.model.FieldReference;
import com.mybatisgx.dsl.mgxql.model.FromClause;
import com.mybatisgx.dsl.mgxql.model.FromEntity;
import com.mybatisgx.dsl.mgxql.model.GroupByClause;
import com.mybatisgx.dsl.mgxql.model.HavingClause;
import com.mybatisgx.dsl.mgxql.model.HavingCondition;
import com.mybatisgx.dsl.mgxql.model.JoinEntity;
import com.mybatisgx.dsl.mgxql.model.JoinType;
import com.mybatisgx.dsl.mgxql.model.LimitClause;
import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import com.mybatisgx.dsl.mgxql.model.OrderByClause;
import com.mybatisgx.dsl.mgxql.model.OrderByItem;
import com.mybatisgx.dsl.mgxql.model.SelectItem;
import com.mybatisgx.dsl.mgxql.model.SelectItemType;
import com.mybatisgx.dsl.mgxql.model.WhereClause;
import com.mybatisgx.dsl.mgxql.syntax.MgxqlParser;
import com.mybatisgx.dsl.mgxql.syntax.MgxqlParserBaseVisitor;
import com.mybatisgx.model.ComparisonOperator;
import com.mybatisgx.model.ConditionOriginType;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.LogicOperator;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.MethodParamInfo;
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
                    // select * 或 entity.*
                    item.setType(SelectItemType.COLUMN_ALL);
                    MgxqlParser.Select_column_allContext columnAll = selectItem.select_column_all();
                    if (columnAll.entity_name_alias() != null) {
                        item.setEntityAlias(columnAll.entity_name_alias().getText());
                    }
                    item.setFieldName("*");
                } else if (selectItem.select_column_custom() != null) {
                    // 自定义列：select id, name 或 entity.name
                    item.setType(SelectItemType.COLUMN);
                    MgxqlParser.Select_column_customContext columnCustom = selectItem.select_column_custom();
                    if (columnCustom.entity_name_alias() != null) {
                        item.setEntityAlias(columnCustom.entity_name_alias().getText());
                    }
                    item.setFieldName(columnCustom.field_name().getText());
                } else if (selectItem.aggregate_function() != null) {
                    // 聚合函数：count/max/min/avg
                    MgxqlParser.Aggregate_functionContext aggregateFunction = selectItem.aggregate_function();
                    parseAggregateFunction(item, aggregateFunction);
                }
                statement.addSelectItem(item);
            }
        }

        private void parseAggregateFunction(SelectItem item, MgxqlParser.Aggregate_functionContext aggregateFunction) {
            if (aggregateFunction.select_count() != null) {
                item.setType(SelectItemType.COUNT);
                MgxqlParser.Select_countContext countCtx = aggregateFunction.select_count();
                if (countCtx.number() != null) {
                    item.setAggregateArg(countCtx.number().getText());
                } else if (countCtx.select_column_all() != null) {
                    item.setAggregateArg("*");
                } else if (countCtx.field_name() != null) {
                    item.setAggregateArg(countCtx.field_name().getText());
                }
            } else if (aggregateFunction.select_max() != null) {
                item.setType(SelectItemType.MAX);
                item.setAggregateArg(aggregateFunction.select_max().field_name().getText());
            } else if (aggregateFunction.select_min() != null) {
                item.setType(SelectItemType.MIN);
                item.setAggregateArg(aggregateFunction.select_min().field_name().getText());
            } else if (aggregateFunction.select_avg() != null) {
                item.setType(SelectItemType.AVG);
                item.setAggregateArg(aggregateFunction.select_avg().field_name().getText());
            }
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
            List<MgxqlParser.Select_entityContext> entities = fromClauseCtx.select_entity();
            List<MgxqlParser.Select_entity_aliasContext> aliases = fromClauseCtx.select_entity_alias();

            if (!entities.isEmpty()) {
                MgxqlParser.Select_entityContext primaryEntityCtx = entities.get(0);
                String primaryAlias = (!aliases.isEmpty() && aliases.get(0) != null) ? aliases.get(0).getText() : null;
                fromClause.setPrimaryEntity(new FromEntity(primaryEntityCtx.getText(), primaryAlias));
            }

            // 解析LEFT JOIN实体
            List<MgxqlParser.Select_left_joinContext> leftJoins = fromClauseCtx.select_left_join();
            if (leftJoins != null) {
                // JOIN实体从entities列表的第2个开始
                for (int i = 0; i < leftJoins.size(); i++) {
                    int entityIndex = i + 1;
                    if (entityIndex < entities.size()) {
                        String joinEntityName = entities.get(entityIndex).getText();
                        int aliasIndex = entityIndex;
                        String joinAlias = (aliasIndex < aliases.size() && aliases.get(aliasIndex) != null)
                                ? aliases.get(aliasIndex).getText() : null;
                        fromClause.addJoinEntity(new JoinEntity(joinEntityName, joinAlias, JoinType.LEFT));
                    }
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
            for (MgxqlParser.Order_by_itemContext orderByItem : orderByClauseCtx.order_by_item()) {
                FieldReference fieldRef = parseFieldReference(orderByItem.entity_field_access_chain());
                String direction = orderByItem.order_by_direction() != null
                        ? orderByItem.order_by_direction().getText() : "asc";
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
            return node instanceof MgxqlParser.LimitContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql分页: {}", node.getText());
            MgxqlStatement statement = context.getMgxqlStatement();
            MgxqlParser.LimitContext limitCtx = (MgxqlParser.LimitContext) node;

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
            for (MgxqlParser.Entity_field_access_chainContext chainCtx : groupByCtx.entity_field_access_chain()) {
                groupByClause.addField(parseFieldReference(chainCtx));
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
            for (MgxqlParser.Aggregate_functionContext aggFuncCtx : havingCtx.aggregate_function()) {
                HavingCondition condition = new HavingCondition();
                SelectItem aggItem = new SelectItem();
                new SelectItemHandler().parseAggregateFunction(aggItem, aggFuncCtx);
                condition.setAggregateFunction(aggItem);
                condition.setOperator(ComparisonOperator.EQ);
                havingClause.addCondition(condition);
            }

            // 解析having中的运算符和参数
            List<MgxqlParser.Having_comparison_op_paramContext> havingOps = havingCtx.having_comparison_op_param();
            for (int i = 0; i < havingOps.size() && i < havingClause.getConditions().size(); i++) {
                MgxqlParser.Having_comparison_op_paramContext opCtx = havingOps.get(i);
                HavingCondition condition = havingClause.getConditions().get(i);
                condition.setOperator(MGXQL_OPERATOR_MAP.getOrDefault(opCtx.relational_op().getText(), ComparisonOperator.EQ));
                condition.setParamValuePath(parseParamValuePath(opCtx.where_param_value_field_access_chain()));
            }

            statement.setHavingClause(havingClause);
        }
    }

    /**
     * 解析entity_field_access_chain为FieldReference
     */
    static FieldReference parseFieldReference(MgxqlParser.Entity_field_access_chainContext chainCtx) {
        FieldReference fieldRef = new FieldReference();
        if (chainCtx.dot() != null) {
            fieldRef.setEntityAlias(chainCtx.entity_name_alias().getText());
            fieldRef.setFieldName(chainCtx.field_name().getText());
        } else {
            fieldRef.setFieldName(chainCtx.entity_name_alias().getText());
        }
        return fieldRef;
    }

    /**
     * 解析参数值访问链
     */
    static List<String> parseParamValuePath(MgxqlParser.Where_param_value_field_access_chainContext accessChain) {
        MgxqlParser.Param_value_field_access_chainContext chainCtx = accessChain.param_value_field_access_chain();
        List<String> pathItems = new ArrayList<>();
        for (MgxqlParser.Field_nameContext fieldNameCtx : chainCtx.field_name()) {
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
        public ConditionExpression visitCondition_expression(MgxqlParser.Condition_expressionContext ctx) {
            LOGGER.debug("处理mgxql条件表达式: {}", ctx.getText());
            ConditionExpression expression = new ConditionExpression(LogicOperator.NULL);
            MgxqlParser.Or_expressionContext orExpressionCtx = ctx.or_expression();
            LogicOperator currentOrOp = null;

            for (int i = 0; i < orExpressionCtx.getChildCount(); i++) {
                ParseTree child = orExpressionCtx.getChild(i);
                if (child instanceof MgxqlParser.Logic_orContext) {
                    currentOrOp = LogicOperator.OR;
                }
                if (child instanceof MgxqlParser.And_expressionContext) {
                    List<ConditionNode> andNodes = parseAndExpression((MgxqlParser.And_expressionContext) child);
                    if (currentOrOp != null && !expression.getNodes().isEmpty()) {
                        // 有OR关系时，将AND节点组作为嵌套表达式添加
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

        private List<ConditionNode> parseAndExpression(MgxqlParser.And_expressionContext andExpressionCtx) {
            List<ConditionNode> nodes = new ArrayList<>();
            LogicOperator currentAndOp = null;

            for (int i = 0; i < andExpressionCtx.getChildCount(); i++) {
                ParseTree child = andExpressionCtx.getChild(i);
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
            MgxqlParser.Condition_expressionContext subExprCtx = termCtx.condition_expression();
            if (subExprCtx != null) {
                // 嵌套括号表达式
                node.setLeftBracket("(");
                node.setRightBracket(")");
                node.setSubExpression(this.visitCondition_expression(subExprCtx));
                return node;
            }

            // 基础条件：field_comparison_op
            MgxqlParser.Field_comparison_opContext fieldOpCtx = termCtx.field_comparison_op();
            if (fieldOpCtx != null) {
                parseFieldComparisonOp(node, fieldOpCtx);
            }
            return node;
        }

        private void parseFieldComparisonOp(ConditionNode node, MgxqlParser.Field_comparison_opContext fieldOpCtx) {
            // 解析左侧字段名
            MgxqlParser.Where_param_name_field_access_chainContext nameChain = fieldOpCtx.where_param_name_field_access_chain();
            MgxqlParser.Param_name_field_access_chainContext chainCtx = nameChain.param_name_field_access_chain();
            if (chainCtx.dot() != null) {
                node.setFieldAlias(chainCtx.entity_name_alias().getText());
                node.setFieldName(chainCtx.field_name().getText());
            } else {
                node.setFieldName(chainCtx.entity_name_alias().getText());
            }

            // 解析运算符和参数
            MgxqlParser.Field_comparison_op_paramContext paramOpCtx = fieldOpCtx.field_comparison_op_param();
            if (paramOpCtx != null) {
                // 关系运算符或匹配运算符
                if (paramOpCtx.relational_op() != null) {
                    node.setOperator(MGXQL_OPERATOR_MAP.get(paramOpCtx.relational_op().getText()));
                }
                if (paramOpCtx.matching_op() != null) {
                    MgxqlParser.Matching_opContext matchingOp = paramOpCtx.matching_op();
                    if (matchingOp.comparison_op_not() != null) {
                        node.setNotOperator(ComparisonOperator.NOT);
                    }
                    String token = matchingOp.getText();
                    if (token.startsWith("not")) {
                        token = token.substring(3).trim();
                    }
                    node.setOperator(MGXQL_OPERATOR_MAP.get(token));
                }
                // 解析右侧参数路径
                if (paramOpCtx.where_param_value_field_access_chain() != null) {
                    node.setParamValuePath(parseParamValuePath(paramOpCtx.where_param_value_field_access_chain()));
                }
            }

            // NULL运算符：is null / is not null
            MgxqlParser.Field_comparison_op_not_paramContext notParamCtx = fieldOpCtx.field_comparison_op_not_param();
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
