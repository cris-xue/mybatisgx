package com.mybatisgx.dsl.mgxql;

import com.google.common.collect.Maps;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.dsl.mgxql.syntax.MgxqlParser;
import com.mybatisgx.dsl.mgxql.syntax.MgxqlParserBaseVisitor;
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
        MGXQL_OPERATOR_MAP.put("!=", ComparisonOperator.NOT_EQ);
        // 匹配运算符
        MGXQL_OPERATOR_MAP.put("in", ComparisonOperator.IN);
        MGXQL_OPERATOR_MAP.put("like", ComparisonOperator.LIKE);
        // left like / right like 算子名已退役（LIKE 改字面量 %:name% / :name% / %:name，design task 3.x）：模糊方向由 like_pattern 的 PERCENT 位置驱动（parseMatchingValue 设 STARTING_WITH/ENDING_WITH/LIKE）
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

    public static class DeleteStatementHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Delete_statementContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql delete语句: {}", node.getText());
            context.getMgxqlStatement().setCommandType(SqlCommandType.DELETE);
        }
    }

    public static class UpdateStatementHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Update_statementContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql update语句: {}", node.getText());
            context.getMgxqlStatement().setCommandType(SqlCommandType.UPDATE);
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
            SelectStatement statement = context.getMgxqlStatement();
            MgxqlParser.Select_item_clauseContext selectItemClauseContext = (MgxqlParser.Select_item_clauseContext) node;

            for (MgxqlParser.Select_itemContext selectItem : selectItemClauseContext.select_item()) {
                SelectItem item = new SelectItem();
                if (selectItem.select_column_all() != null) {
                    item.setType(SelectItemType.COLUMN_ALL);
                    MgxqlParser.Select_column_allContext columnAll = selectItem.select_column_all();
                    String allAlias = columnAll.entity_name_alias() != null
                            ? stripBackticks(columnAll.entity_name_alias().getText()) : null;
                    item.setFieldRef(new FieldReference(allAlias, "*"));
                } else if (selectItem.select_column_custom() != null) {
                    item.setType(SelectItemType.COLUMN);
                    MgxqlParser.Select_column_customContext columnCustom = selectItem.select_column_custom();
                    FieldReference fieldRef = parseFieldReference(columnCustom.field_reference());
                    item.setFieldRef(fieldRef);
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

            if (funcNameCtx.select_max() != null) {
                item.setType(SelectItemType.MAX);
            } else if (funcNameCtx.select_min() != null) {
                item.setType(SelectItemType.MIN);
            } else if (funcNameCtx.select_avg() != null) {
                item.setType(SelectItemType.AVG);
            } else if (funcNameCtx.select_sum() != null) {
                item.setType(SelectItemType.SUM);
            } else if (funcNameCtx.select_count() != null) {
                item.setType(SelectItemType.COUNT);
            }

            if (funcArgCtx.field_reference() != null) {
                item.setArgumentKind(AggregateArgumentKind.FIELD);
                item.setFieldRef(parseFieldReference(funcArgCtx.field_reference()));
            } else if (funcArgCtx.number() != null) {
                item.setArgumentKind(AggregateArgumentKind.NUMBER);
                FieldReference fieldRef = new FieldReference();
                fieldRef.setFieldName(funcArgCtx.number().getText());
                item.setFieldRef(fieldRef);
            } else if (funcArgCtx.select_asterisk() != null) {
                item.setArgumentKind(AggregateArgumentKind.ASTERISK);
                FieldReference fieldRef = new FieldReference();
                fieldRef.setFieldName("*");
                item.setFieldRef(fieldRef);
            }
        }
    }

    public static class ModifyEntityClauseHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 2;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MgxqlParser.Modify_entityContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理mgxql modify entity子句: {}", node.getText());
            ModifyStatement statement = context.getMgxqlStatement();
            MgxqlParser.Modify_entityContext modifyEntityContext = (MgxqlParser.Modify_entityContext) node;
            ModifyEntity modifyEntity = new ModifyEntity();
            modifyEntity.setEntityName(modifyEntityContext.getText());
            statement.setModifyEntity(modifyEntity);
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
            SelectStatement statement = context.getMgxqlStatement();
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
                    JoinEntity joinEntity = new JoinEntity(joinEntityName, joinAlias, JoinType.LEFT);
                    MgxqlParser.Select_on_expressionContext onExprCtx = joinEntityCtx.select_on_expression();
                    if (onExprCtx != null) {
                        joinEntity.setOnLeftAlias(stripBackticks(onExprCtx.entity_name_alias(0).getText()));
                        joinEntity.setOnRightAlias(stripBackticks(onExprCtx.entity_name_alias(1).getText()));
                    }
                    fromClause.addJoinEntity(joinEntity);
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
            WhereExpression rootExpression = whereClauseVisitor.visit(node);
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
            SelectStatement statement = context.getMgxqlStatement();
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
            SelectStatement statement = context.getMgxqlStatement();
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
            SelectStatement statement = context.getMgxqlStatement();
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
            SelectStatement statement = context.getMgxqlStatement();
            MgxqlParser.Having_clauseContext havingCtx = (MgxqlParser.Having_clauseContext) node;

            MgxqlParser.Having_or_expressionContext havingOrExpr = havingCtx.having_or_expression();
            HavingExpression expression = parseHavingOrExpression(havingOrExpr);
            statement.setHavingExpression(expression);
        }

        private HavingExpression parseHavingOrExpression(MgxqlParser.Having_or_expressionContext ctx) {
            HavingExpression expression = new HavingExpression(LogicOperator.NULL);
            LogicOperator currentOrOp = null;

            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree child = ctx.getChild(i);
                if (child instanceof MgxqlParser.Logic_orContext) {
                    currentOrOp = LogicOperator.OR;
                    expression.setLogicOperator(LogicOperator.OR);
                }
                if (child instanceof MgxqlParser.Having_and_expressionContext) {
                    List<HavingConditionNode> andNodes = parseHavingAndExpression((MgxqlParser.Having_and_expressionContext) child);
                    if (currentOrOp != null && !expression.getNodes().isEmpty()) {
                        for (HavingConditionNode node : andNodes) {
                            node.setLogicOperator(currentOrOp);
                            expression.addNode(node);
                        }
                        currentOrOp = null;
                    } else {
                        for (HavingConditionNode node : andNodes) {
                            expression.addNode(node);
                        }
                    }
                }
            }
            return expression;
        }

        private List<HavingConditionNode> parseHavingAndExpression(MgxqlParser.Having_and_expressionContext ctx) {
            List<HavingConditionNode> nodes = new ArrayList<>();
            LogicOperator currentAndOp = null;

            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree child = ctx.getChild(i);
                if (child instanceof MgxqlParser.Logic_andContext) {
                    currentAndOp = LogicOperator.AND;
                }
                if (child instanceof MgxqlParser.Having_termContext) {
                    HavingConditionNode node = parseHavingTerm((MgxqlParser.Having_termContext) child);
                    if (currentAndOp != null) {
                        node.setLogicOperator(currentAndOp);
                        currentAndOp = null;
                    }
                    nodes.add(node);
                }
            }
            return nodes;
        }

        private HavingConditionNode parseHavingTerm(MgxqlParser.Having_termContext termCtx) {
            HavingConditionNode node = new HavingConditionNode();

            MgxqlParser.Having_or_expressionContext subExprCtx = termCtx.having_or_expression();
            if (subExprCtx != null) {
                node.setSubExpression(this.parseHavingOrExpression(subExprCtx));
                return node;
            }

            MgxqlParser.Having_comparisonContext comparisonCtx = termCtx.having_comparison();
            if (comparisonCtx != null) {
                parseHavingComparison(node, comparisonCtx);
            }
            return node;
        }

        private void parseHavingComparison(HavingConditionNode node, MgxqlParser.Having_comparisonContext compCtx) {
            SelectItem aggItem = new SelectItem();
            new SelectItemHandler().parseAggregateFunction(aggItem, compCtx.aggregate_function());
            node.setLeftSide(buildAggregateExpression(aggItem));

            node.setOperator(MGXQL_OPERATOR_MAP.getOrDefault(compCtx.relational_op().getText(), ComparisonOperator.EQ));

            MgxqlParser.Having_valueContext havingValueCtx = compCtx.having_value();
            if (havingValueCtx.parameter_reference() != null) {
                node.setParamValuePath(parseParameterReference(havingValueCtx.parameter_reference()));
            } else if (havingValueCtx.number() != null) {
                node.setLiteralValue(Integer.parseInt(havingValueCtx.number().getText()));
            }
        }

        private com.mybatisgx.dsl.mgxql.model.expression.HavingAggregateExpression buildAggregateExpression(SelectItem aggItem) {
            AggregateFunction function;
            switch (aggItem.getType()) {
                case COUNT: function = AggregateFunction.COUNT; break;
                case MAX: function = AggregateFunction.MAX; break;
                case MIN: function = AggregateFunction.MIN; break;
                case AVG: function = AggregateFunction.AVG; break;
                case SUM: function = AggregateFunction.SUM; break;
                default: throw new MybatisgxException("不支持的聚合函数类型: " + aggItem.getType());
            }
            String argument = null;
            FieldReference fieldRef = aggItem.getFieldRef();
            if (fieldRef != null) {
                if (fieldRef.getEntityAlias() != null) {
                    argument = fieldRef.getEntityAlias() + "." + fieldRef.getFieldName();
                } else {
                    argument = fieldRef.getFieldName();
                }
            }
            com.mybatisgx.dsl.mgxql.model.expression.HavingAggregateExpression expression =
                    new com.mybatisgx.dsl.mgxql.model.expression.HavingAggregateExpression(function, argument);
            expression.setArgumentKind(aggItem.getArgumentKind());
            return expression;
        }
    }

    static String stripBackticks(String text) {
        if (text != null && text.startsWith("`") && text.endsWith("`")) {
            return text.substring(1, text.length() - 1);
        }
        return text;
    }

    /**
     * 解析Field_referenceContext为FieldReference
     */
    static FieldReference parseFieldReference(MgxqlParser.Field_referenceContext fieldRefCtx) {
        FieldReference fieldRef = new FieldReference();
        if (fieldRefCtx.dot() != null && fieldRefCtx.dot().getChildCount() > 0) {
            fieldRef.setEntityAlias(stripBackticks(fieldRefCtx.entity_name_alias().getText()));
            fieldRef.setFieldName(stripBackticks(fieldRefCtx.field_name().getText()));
        } else {
            fieldRef.setFieldName(stripBackticks(fieldRefCtx.field_name().getText()));
        }
        return fieldRef;
    }

    /**
     * 解析Parameter_referenceContext为参数路径列表
     */
    static List<String> parseParameterReference(MgxqlParser.Parameter_referenceContext paramRefCtx) {
        List<String> pathItems = new ArrayList<>();
        for (MgxqlParser.Field_nameContext fieldNameCtx : paramRefCtx.field_name()) {
            pathItems.add(stripBackticks(fieldNameCtx.getText()));
        }
        return pathItems;
    }

    /**
     * WHERE子句访问器，构建ConditionExpression树形结构
     */
    public static class WhereClauseVisitor extends MgxqlParserBaseVisitor<WhereExpression> {

        private AtomicInteger conditionIndex = new AtomicInteger(0);
        private ParserContext context;

        public WhereClauseVisitor(ParserContext context) {
            this.context = context;
        }

        @Override
        public WhereExpression visitWhere_clause(MgxqlParser.Where_clauseContext ctx) {
            LOGGER.debug("处理mgxql where条件: {}", ctx.getText());
            return buildSequence(ctx.where_sequence());
        }

        /**
         * 构建扁平 WhereElement 序列（design Q7）：遍历 where_item，每项取前置 connector（NULL/AND/OR）+ atom 分派。
         */
        private WhereExpression buildSequence(MgxqlParser.Where_sequenceContext seqCtx) {
            WhereExpression expression = new WhereExpression(LogicOperator.NULL);
            for (MgxqlParser.Where_itemContext itemCtx : seqCtx.where_item()) {
                LogicOperator connector = readConnector(itemCtx.logic_and(), itemCtx.logic_or());
                WhereElement element = buildAtom(itemCtx.where_atom());
                element.setLogicOperator(connector);
                expression.addNode(element);
            }
            return expression;
        }

        /**
         * 序列原子分派：普通条件 / 括号分组 / 动态门块（if/bracket/choose）。
         */
        private WhereElement buildAtom(MgxqlParser.Where_atomContext atomCtx) {
            // 普通条件
            MgxqlParser.Condition_comparisonContext compCtx = atomCtx.condition_comparison();
            if (compCtx != null) {
                WhereConditionNode node = new WhereConditionNode();
                parseConditionComparison(node, compCtx);
                return node;
            }
            // 括号分组（复用 WhereConditionNode.subExpression，design Q2）
            MgxqlParser.Bracket_groupContext groupCtx = atomCtx.bracket_group();
            if (groupCtx != null) {
                WhereConditionNode node = new WhereConditionNode();
                node.setLeftBracket("(");
                node.setRightBracket(")");
                node.setSubExpression(buildSequence(groupCtx.where_sequence()));
                return node;
            }
            // 动态门块
            if (atomCtx.if_directive() != null) {
                return buildIfDirective(atomCtx.if_directive());
            }
            if (atomCtx.bracket_directive() != null) {
                return buildBracketDirective(atomCtx.bracket_directive());
            }
            if (atomCtx.choose_directive() != null) {
                return buildChooseDirective(atomCtx.choose_directive());
            }
            throw new MybatisgxException("mgxql 语法错误: 无法识别的 WHERE 原子 %s", atomCtx.getText());
        }

        private IfDirectiveNode buildIfDirective(MgxqlParser.If_directiveContext ctx) {
            IfDirectiveNode node = new IfDirectiveNode();
            node.setGuard(buildGuard(ctx.guard_or_expression()));
            node.setBody(buildBodySequence(ctx.body_sequence()));
            node.setLogicOperator(readConnector(ctx.block_prefix() != null ? ctx.block_prefix().logic_and() : null,
                    ctx.block_prefix() != null ? ctx.block_prefix().logic_or() : null));
            return node;
        }

        private BracketDirectiveNode buildBracketDirective(MgxqlParser.Bracket_directiveContext ctx) {
            BracketDirectiveNode node = new BracketDirectiveNode();
            node.setBody(buildBodySequence(ctx.body_sequence()));
            MgxqlParser.Block_prefixContext prefix = ctx.block_prefix();
            node.setLogicOperator(readConnector(prefix != null ? prefix.logic_and() : null,
                    prefix != null ? prefix.logic_or() : null));
            return node;
        }

        private ChooseNode buildChooseDirective(MgxqlParser.Choose_directiveContext ctx) {
            ChooseNode node = new ChooseNode();
            List<WhenNode> whens = new ArrayList<>();
            for (MgxqlParser.When_directiveContext whenCtx : ctx.when_directive()) {
                WhenNode when = new WhenNode();
                when.setGuard(buildGuard(whenCtx.guard_or_expression()));
                when.setBody(buildBodySequence(whenCtx.body_sequence()));
                when.setLogicOperator(readConnector(whenCtx.block_prefix() != null ? whenCtx.block_prefix().logic_and() : null,
                        whenCtx.block_prefix() != null ? whenCtx.block_prefix().logic_or() : null));
                whens.add(when);
            }
            node.setWhens(whens);
            if (ctx.otherwise_directive() != null) {
                WhereExpression otherwise = new WhereExpression(LogicOperator.NULL);
                MgxqlParser.Body_sequenceContext bodyCtx = ctx.otherwise_directive().body_sequence();
                if (bodyCtx != null) {
                    otherwise = buildBodySequence(bodyCtx);
                }
                node.setOtherwise(otherwise);
            }
            return node;
        }

        /**
         * body 序列（design D2 平面、禁嵌套动态门）：body_atom 只含普通条件 + 括号分组。
         */
        private WhereExpression buildBodySequence(MgxqlParser.Body_sequenceContext seqCtx) {
            WhereExpression expression = new WhereExpression(LogicOperator.NULL);
            for (MgxqlParser.Body_itemContext itemCtx : seqCtx.body_item()) {
                LogicOperator connector = readConnector(itemCtx.logic_and(), itemCtx.logic_or());
                WhereElement element = buildBodyAtom(itemCtx.body_atom());
                element.setLogicOperator(connector);
                expression.addNode(element);
            }
            return expression;
        }

        private WhereElement buildBodyAtom(MgxqlParser.Body_atomContext atomCtx) {
            MgxqlParser.Condition_comparisonContext compCtx = atomCtx.condition_comparison();
            if (compCtx != null) {
                WhereConditionNode node = new WhereConditionNode();
                parseConditionComparison(node, compCtx);
                return node;
            }
            // body 内括号分组（body_atom 的 left_bracket/body_sequence/right_bracket）
            if (atomCtx.body_sequence() != null) {
                WhereConditionNode node = new WhereConditionNode();
                node.setLeftBracket("(");
                node.setRightBracket(")");
                node.setSubExpression(buildBodySequence(atomCtx.body_sequence()));
                return node;
            }
            throw new MybatisgxException("mgxql 语法错误: 无法识别的动态门 body 原子 %s", atomCtx.getText());
        }

        /**
         * guard 独立文法序列化回字符串（design D8）：保留 ==，逻辑连接规范化为 && / ||（and→&&、or→||）。
         * guard 不做参数校验，仅按结构文本化。
         */
        private String buildGuard(MgxqlParser.Guard_or_expressionContext ctx) {
            return buildGuardOr(ctx);
        }

        private String buildGuardOr(MgxqlParser.Guard_or_expressionContext ctx) {
            List<MgxqlParser.Guard_and_expressionContext> ands = ctx.guard_and_expression();
            List<MgxqlParser.Guard_logic_orContext> ors = ctx.guard_logic_or();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ands.size(); i++) {
                if (i > 0) {
                    sb.append(" ").append(normalizeLogicOr(ors.get(i - 1))).append(" ");
                }
                sb.append(buildGuardAnd(ands.get(i)));
            }
            return sb.toString();
        }

        private String buildGuardAnd(MgxqlParser.Guard_and_expressionContext ctx) {
            List<MgxqlParser.Guard_termContext> terms = ctx.guard_term();
            List<MgxqlParser.Guard_logic_andContext> ands = ctx.guard_logic_and();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < terms.size(); i++) {
                if (i > 0) {
                    sb.append(" ").append(normalizeLogicAnd(ands.get(i - 1))).append(" ");
                }
                sb.append(buildGuardTerm(terms.get(i)));
            }
            return sb.toString();
        }

        private String buildGuardTerm(MgxqlParser.Guard_termContext ctx) {
            MgxqlParser.Guard_or_expressionContext sub = ctx.guard_or_expression();
            if (sub != null) {
                return "(" + buildGuardOr(sub) + ")";
            }
            return buildGuardComparison(ctx.guard_comparison());
        }

        private String buildGuardComparison(MgxqlParser.Guard_comparisonContext ctx) {
            List<MgxqlParser.Guard_operandContext> operands = ctx.guard_operand();
            MgxqlParser.Guard_relational_opContext rel = ctx.guard_relational_op();
            MgxqlParser.Guard_null_opContext nullOp = ctx.guard_null_op();
            StringBuilder sb = new StringBuilder();
            if (rel != null && operands.size() >= 2) {
                sb.append(buildGuardOperand(operands.get(0))).append(" ")
                        .append(rel.getText()).append(" ")
                        .append(buildGuardOperand(operands.get(1)));
            } else if (nullOp != null && !operands.isEmpty()) {
                sb.append(buildGuardOperand(operands.get(0))).append(" ").append(nullOp.getText());
            } else if (!operands.isEmpty()) {
                sb.append(buildGuardOperand(operands.get(0)));
            }
            return sb.toString();
        }

        private String buildGuardOperand(MgxqlParser.Guard_operandContext ctx) {
            if (ctx.field_reference() != null) {
                return ctx.field_reference().getText();
            }
            if (ctx.parameter_reference() != null) {
                return ctx.parameter_reference().getText();
            }
            if (ctx.number() != null) {
                return ctx.number().getText();
            }
            if (ctx.STRING_LITERAL() != null) {
                return ctx.STRING_LITERAL().getText();
            }
            return ctx.getText();
        }

        // guard 逻辑连接规范化（design D8）：guard_logic_or（|| / or）→ ||，guard_logic_and（&& / and）→ &&
        private String normalizeLogicOr(MgxqlParser.Guard_logic_orContext ctx) {
            return "||";
        }

        private String normalizeLogicAnd(MgxqlParser.Guard_logic_andContext ctx) {
            return "&&";
        }

        private LogicOperator readConnector(MgxqlParser.Logic_andContext andCtx, MgxqlParser.Logic_orContext orCtx) {
            if (orCtx != null) {
                return LogicOperator.OR;
            }
            if (andCtx != null) {
                return LogicOperator.AND;
            }
            return LogicOperator.NULL;
        }

        private void parseConditionComparison(WhereConditionNode node, MgxqlParser.Condition_comparisonContext compCtx) {
            // ? 前缀可选条件已退役（design D3）：condition_comparison 不再含 question_mark，optional 字段已移除。
            // 解析左侧字段引用
            FieldReference fieldRef = parseFieldReference(compCtx.field_reference());
            node.setFieldRef(fieldRef);

            // 解析运算符和右值
            MgxqlParser.Condition_comparison_paramContext paramCtx = compCtx.condition_comparison_param();
            if (paramCtx != null) {
                // 关系运算符：relational_op condition_value
                if (paramCtx.relational_op() != null) {
                    node.setOperator(MGXQL_OPERATOR_MAP.get(paramCtx.relational_op().getText()));
                    MgxqlParser.Condition_valueContext condValueCtx = paramCtx.condition_value();
                    if (condValueCtx.parameter_reference() != null) {
                        node.setParamValuePath(parseParameterReference(condValueCtx.parameter_reference()));
                        node.setIndex(conditionIndex.getAndIncrement());
                    } else if (condValueCtx.number() != null) {
                        node.setConditionValue(Integer.parseInt(condValueCtx.number().getText()));
                    }
                }
                // 匹配运算符：matching_op matching_value（LIKE 模式 / IN 集合 / 裸参）
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
                    parseMatchingValue(node, paramCtx.matching_value());
                }
            }

            // NULL运算符：is null / is not null
            MgxqlParser.Condition_comparison_not_paramContext notParamCtx = compCtx.condition_comparison_not_param();
            if (notParamCtx != null) {
                MgxqlParser.Comparison_op_nullContext nullOpCtx = notParamCtx.comparison_op_null();
                node.setOperator(MGXQL_OPERATOR_MAP.get(nullOpCtx.getText()));
            }
        }

        /**
         * matching 右值消费（LIKE 模式 / IN 集合 / 裸参）。LIKE 模式按 PERCENT 位置设算子语义，IN 集合记 CollectionInfo（design D6）。
         */
        private void parseMatchingValue(WhereConditionNode node, MgxqlParser.Matching_valueContext ctx) {
            // 裸参数（name like :name）：直接记 paramValuePath
            if (ctx.parameter_reference() != null) {
                node.setParamValuePath(parseParameterReference(ctx.parameter_reference()));
                node.setIndex(conditionIndex.getAndIncrement());
                return;
            }
            // LIKE 模式（%:name / :name% / %:name%）：按 PERCENT 位置设算子（ENDING_WITH 前模糊 / STARTING_WITH 后模糊 / LIKE 全模糊）
            if (ctx.like_pattern() != null) {
                MgxqlParser.Like_patternContext likeCtx = ctx.like_pattern();
                node.setParamValuePath(parseParameterReference(likeCtx.parameter_reference()));
                node.setIndex(conditionIndex.getAndIncrement());
                int percentCount = likeCtx.PERCENT().size();
                String likeText = likeCtx.getText();
                boolean startsWithPercent = likeText.startsWith("%");
                boolean endsWithPercent = likeText.endsWith("%");
                if (percentCount == 1 && startsWithPercent) {
                    node.setOperator(ComparisonOperator.ENDING_WITH);
                } else if (percentCount == 1 && endsWithPercent) {
                    node.setOperator(ComparisonOperator.STARTING_WITH);
                } else {
                    node.setOperator(ComparisonOperator.LIKE);
                }
                return;
            }
            // IN 集合：简单 (:idList) 或复杂 (item:coll)=>[item.id]（CollectionInfo 三字段填充，design D6，task 5.3）
            if (ctx.in_collection() != null) {
                MgxqlParser.In_collectionContext inCtx = ctx.in_collection();
                if (inCtx.simple_collection() != null) {
                    node.setParamValuePath(parseParameterReference(inCtx.simple_collection().parameter_reference()));
                    node.setIndex(conditionIndex.getAndIncrement());
                    // 简单集合：CollectionInfo 由绑定阶段默认（itemName=item, collectionName=参数名, valueExpr=#{item}）
                } else if (inCtx.complex_collection() != null) {
                    MgxqlParser.Complex_collectionContext complex = inCtx.complex_collection();
                    List<String> collectionPath = parseParameterReference(complex.parameter_reference());
                    node.setParamValuePath(collectionPath);
                    node.setIndex(conditionIndex.getAndIncrement());
                    // 复杂集合三字段（design D6）：itemName=item_name、collectionName=参数名（去冒号）、valueExpr=迭代值字段链（如 item.id）
                    String itemName = complex.item_name().getText();
                    String collectionName = String.join(".", collectionPath);
                    String valueExpr = complex.value_expr_list().getText();
                    CollectionInfo collectionInfo = new CollectionInfo();
                    collectionInfo.setItemName(itemName);
                    collectionInfo.setCollectionName(collectionName);
                    collectionInfo.setValueExpr(valueExpr);
                    node.setCollectionInfo(collectionInfo);
                }
            }
        }

        @Override
        protected boolean shouldVisitNextChild(RuleNode node, WhereExpression currentResult) {
            return node instanceof MgxqlParser.Where_clauseContext && currentResult == null;
        }
    }

    /**
     * 解析器上下文，携带MgxqlStatement模型
     */
    public static class ParserContext {

        private MgxqlStatement mgxqlStatement;

        public ParserContext(MgxqlStatement mgxqlStatement) {
            this.mgxqlStatement = mgxqlStatement;
        }

        public <T extends MgxqlStatement> T getMgxqlStatement() {
            return (T) mgxqlStatement;
        }
    }
}
