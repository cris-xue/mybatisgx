package com.mybatisgx.dsl.method;

import com.google.common.collect.Maps;
import com.mybatisgx.dsl.method.model.*;
import com.mybatisgx.dsl.method.syntax.MethodNameParser;
import com.mybatisgx.dsl.method.syntax.MethodNameParserBaseVisitor;
import com.mybatisgx.model.ComparisonOperator;
import com.mybatisgx.model.LogicOperator;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.utils.FieldNameUtils;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * mybatisgx语法处理器
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class MethodSyntaxHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodSyntaxHandler.class);

    private static final Map<Class<?>, SqlCommandType> COMMAND_MAPPINGS = Maps.newHashMap();

    static {
        COMMAND_MAPPINGS.put(MethodNameParser.Insert_statementContext.class, SqlCommandType.INSERT);
        COMMAND_MAPPINGS.put(MethodNameParser.Delete_statementContext.class, SqlCommandType.DELETE);
        COMMAND_MAPPINGS.put(MethodNameParser.Update_statementContext.class, SqlCommandType.UPDATE);
        COMMAND_MAPPINGS.put(MethodNameParser.Select_statementContext.class, SqlCommandType.SELECT);
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

    public static class ModifyStatementHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MethodNameParser.Delete_statementContext || node instanceof MethodNameParser.Update_statementContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            ModifyStatement modifyStatement = context.getBaseStatement();
            MethodInfo methodInfo = context.getMethodInfo();
            Class<?> entityClass = methodInfo.getMapperInfo().getEntityClass();
            modifyStatement.setEntity(entityClass.getSimpleName());
        }
    }

    public static class SelectStatementHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MethodNameParser.Select_statementContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            QueryStatement queryStatement = context.getBaseStatement();
            MethodInfo methodInfo = context.getMethodInfo();
            Class<?> entityClass = methodInfo.getMapperInfo().getEntityClass();
            queryStatement.setFrom(String.format("from %s", entityClass.getSimpleName()));
        }
    }

    public static class SelectItemHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MethodNameParser.Select_item_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理查询: {}", node.getText());
            QueryStatement queryStatement = context.getBaseStatement();
            MethodNameParser.Select_item_clauseContext selectItemContext = (MethodNameParser.Select_item_clauseContext) node;
            if (selectItemContext.select_column() != null) {
                queryStatement.setSelectItemType(SelectItemType.COLUMN);
            }
            if (selectItemContext.select_count() != null) {
                queryStatement.setSelectItemType(SelectItemType.COUNT);
            }
        }
    }

    public static class BusinessSemanticHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MethodNameParser.Business_semanticContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("忽略业务语义: {}", node.getText());
        }
    }

    public static class LimitHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MethodNameParser.LimitContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("分页处理: {}", node.getText());
            QueryStatement queryStatement = context.getBaseStatement();
            MethodNameParser.LimitContext limitContext = (MethodNameParser.LimitContext) node;

            MethodNameParser.Limit_topContext limitTopContext = limitContext.limit_top();
            if (limitTopContext != null) {
                String limitCount = StringUtils.remove(limitTopContext.getText(), "Top");
                List<String> limitList = Arrays.asList("limit", limitCount);
                queryStatement.setLimitList(limitList);
            }
        }
    }

    public static class WhereClauseHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MethodNameParser.Where_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("处理where条件: {}", node.getText());
            WhereClauseVisitor whereClauseVisitor = new WhereClauseVisitor(context);

            List<ConditionTerm> conditionTermList = whereClauseVisitor.visit(node);
            Condition condition = new Condition("where", conditionTermList);

            BaseStatement baseStatement = context.getBaseStatement();
            baseStatement.setCondition(condition);
        }
    }

    public static class OrderByHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MethodNameParser.Order_by_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("排序函数: {}", node.getText());

            List<OrderByTerm> orderByTermList = new ArrayList<>();
            MethodNameParser.Order_by_clauseContext orderByClauseContext = (MethodNameParser.Order_by_clauseContext) node;
            for (MethodNameParser.Order_by_itemContext orderByItem : orderByClauseContext.order_by_item()) {
                OrderByTerm orderByTerm = new OrderByTerm();
                orderByTerm.setFieldName(FieldNameUtils.upperCamelToLowerCamel(orderByItem.field().getText()));
                orderByTerm.setDirection(FieldNameUtils.upperCamelToLowerCamel(orderByItem.order_by_direction().getText()));
                orderByTermList.add(orderByTerm);
            }

            OrderBy orderBy = new OrderBy();
            orderBy.setStart("order by");
            orderBy.setOrderByTermList(orderByTermList);

            QueryStatement queryStatement = context.getBaseStatement();
            queryStatement.setOrderBy(orderBy);
        }
    }

    public static class WhereClauseVisitor extends MethodNameParserBaseVisitor<List<ConditionTerm>> {

        private ConditionTermParser conditionTermParser;

        public WhereClauseVisitor(ParserContext context) {
            this.conditionTermParser = new ConditionTermParser(context);
        }

        @Override
        public List<ConditionTerm> visitCondition_expression(MethodNameParser.Condition_expressionContext ctx) {
            LOGGER.debug("处理条件表达式: {}", ctx.getText());
            List<ConditionTerm> conditionTermList = new ArrayList();
            MethodNameParser.Or_expressionContext orExpressionContext = ctx.or_expression();
            MethodNameParser.Logic_orContext logicOrContext = null;
            for (int i = 0; i < orExpressionContext.getChildCount(); i++) {
                ParseTree parseTree = orExpressionContext.getChild(i);
                if (parseTree instanceof MethodNameParser.Logic_orContext) {
                    logicOrContext = (MethodNameParser.Logic_orContext) parseTree;
                }
                if (parseTree instanceof MethodNameParser.And_expressionContext) {
                    List<ConditionTerm> andConditionTermList = this.parseAndExpression((MethodNameParser.And_expressionContext) parseTree, logicOrContext);
                    conditionTermList.addAll(andConditionTermList);
                }
            }
            return conditionTermList;
        }

        private List<ConditionTerm> parseAndExpression(MethodNameParser.And_expressionContext andExpressionContext, MethodNameParser.Logic_orContext logicOrContext) {
            List<ConditionTerm> conditionTermList = new ArrayList();
            MethodNameParser.Logic_andContext logicAndContext = null;
            for (int i = 0; i < andExpressionContext.getChildCount(); i++) {
                ParseTree parseTree = andExpressionContext.getChild(i);
                if (parseTree instanceof MethodNameParser.Condition_termContext) {
                    MethodNameParser.Condition_termContext conditionTermContext = (MethodNameParser.Condition_termContext) parseTree;
                    MethodNameParser.Condition_expressionContext conditionExpressionContext = conditionTermContext.condition_expression();
                    if (conditionExpressionContext != null) {
                        List<ConditionTerm> childConditionTermList = this.visitCondition_expression(conditionExpressionContext);
                        // this.setLogicOperator(childConditionTermList, logicOrContext, logicAndContext);
                        conditionTermList.addAll(childConditionTermList);
                    }
                    MethodNameParser.Field_comparison_opContext fieldComparisonOpContext = conditionTermContext.field_comparison_op();
                    if (fieldComparisonOpContext != null) {
                        ConditionTerm conditionTerm = this.conditionTermParser.parse(fieldComparisonOpContext);
                        this.setLogicOperator(conditionTerm, logicOrContext, logicAndContext);
                        conditionTermList.add(conditionTerm);
                    }
                }
                if (parseTree instanceof MethodNameParser.Logic_andContext) {
                    logicAndContext = (MethodNameParser.Logic_andContext) parseTree;
                }
            }
            return conditionTermList;
        }

        private void setLogicOperator(ConditionTerm conditionTerm, MethodNameParser.Logic_orContext logicOpOrClauseContext, MethodNameParser.Logic_andContext logicOpAndClauseContext) {
            if (logicOpOrClauseContext != null) {
                conditionTerm.setLogicOperator(LogicOperator.OR);
            } else if (logicOpAndClauseContext != null) {
                conditionTerm.setLogicOperator(LogicOperator.AND);
            } else {
                conditionTerm.setLogicOperator(LogicOperator.NULL);
            }
        }

        @Override
        public List<ConditionTerm> visitCondition_term(MethodNameParser.Condition_termContext ctx) {
            LOGGER.debug("处理条件单元: {}", ctx.getText());
            return super.visitCondition_term(ctx);
        }

        @Override
        protected boolean shouldVisitNextChild(RuleNode node, List<ConditionTerm> currentResult) {
            return node instanceof MethodNameParser.Where_clauseContext && currentResult == null;
        }
    }

    public static class ConditionTermParser {

        private ParserContext context;
        private Map<Class<?>, ConditionStrategy> strategies = new HashMap<>();

        public ConditionTermParser(ParserContext context) {
            this.context = context;
            this.strategies.put(MethodNameParser.FieldContext.class, new FieldStrategyHandler());
            this.strategies.put(MethodNameParser.Comparison_opContext.class, new ComparisonOperatorStrategyHandler());
            this.strategies.put(MethodNameParser.Comparison_op_notContext.class, new ComparisonNotOperatorStrategyHandler());
            this.strategies.put(MethodNameParser.Comparison_op_nullContext.class, new ComparisonNullOperatorStrategyHandler());
        }

        public ConditionTerm parse(MethodNameParser.Field_comparison_opContext ctx) {
            LOGGER.debug("处理条件: {}", ctx.getText());
            ConditionTerm conditionTerm = new ConditionTerm();
            this.parse(ctx.field(), conditionTerm);
            this.parse(this.getComparisonOp(ctx), conditionTerm);
            return conditionTerm;
        }

        private void parse(ParseTree ctx, ConditionTerm conditionTerm) {
            Class<?> ComparisonOpClass = ctx != null ? ctx.getClass() : MethodNameParser.Comparison_opContext.class;
            ConditionStrategy strategy = strategies.get(ComparisonOpClass);
            if (strategy != null) {
                strategy.apply(ctx, context, conditionTerm);
            }
        }

        private ParseTree getComparisonOp(MethodNameParser.Field_comparison_opContext ctx) {
            MethodNameParser.Comparison_opContext comparisonOpContext = ctx.comparison_op();
            MethodNameParser.Comparison_op_notContext comparisonOpNotContext = ctx.comparison_op_not();
            MethodNameParser.Comparison_op_nullContext comparisonOpNullContext = ctx.comparison_op_null();
            if (comparisonOpContext != null) {
                return comparisonOpContext;
            }
            if (comparisonOpNotContext != null) {
                return comparisonOpNotContext;
            }
            if (comparisonOpNullContext != null) {
                return comparisonOpNullContext;
            }
            return null;
        }
    }

    /**
     * 策略接口
     */
    public interface ConditionStrategy {

        void apply(ParseTree node, ParserContext context, ConditionTerm conditionTerm);
    }

    private static class FieldStrategyHandler implements ConditionStrategy {

        @Override
        public void apply(ParseTree node, ParserContext context, ConditionTerm conditionTerm) {
            String fieldName = this.getFullTokenJavaColumn(node);
            conditionTerm.setFieldName(fieldName);
            conditionTerm.setValue(String.format("%s%s", ":", fieldName));
        }

        private String getFullTokenJavaColumn(ParseTree node) {
            MethodNameParser.FieldContext fieldContext = (MethodNameParser.FieldContext) node;
            MethodNameParser.Field_identifierContext fieldIdentifierContext = fieldContext.field_identifier();
            if (fieldIdentifierContext != null) {
                String token = fieldIdentifierContext.getText();
                return FieldNameUtils.upperCamelToLowerCamel(token);
            }
            MethodNameParser.Escaped_identifierContext escapedIdentifierContext = fieldContext.escaped_identifier();
            if (escapedIdentifierContext != null) {
                String token = escapedIdentifierContext.getText();
                return FieldNameUtils.upperCamelToLowerCamel(token.replace("$", ""));
            }
            return StringUtils.EMPTY;
        }
    }

    private static class ComparisonOperatorStrategyHandler implements ConditionStrategy {

        @Override
        public void apply(ParseTree node, ParserContext context, ConditionTerm conditionTerm) {
            String token = node == null ? "" : node.getText();
            ComparisonOperator comparisonOperator = ComparisonOperator.getComparisonOperator(token);
            conditionTerm.setComparisonOperator(comparisonOperator);
        }
    }

    private static class ComparisonNotOperatorStrategyHandler implements ConditionStrategy {

        @Override
        public void apply(ParseTree node, ParserContext context, ConditionTerm conditionTerm) {
            ComparisonOperator comparisonOperator = ComparisonOperator.getComparisonOperator(node.getText());
            conditionTerm.setComparisonOperator(comparisonOperator);
        }
    }

    private static class ComparisonNullOperatorStrategyHandler implements ConditionStrategy {

        @Override
        public void apply(ParseTree node, ParserContext context, ConditionTerm conditionTerm) {
            ComparisonOperator comparisonOperator = ComparisonOperator.getComparisonOperator(node.getText());
            conditionTerm.setComparisonOperator(comparisonOperator);
        }
    }

    public static class ParserContext {

        private SqlCommandType sqlCommandType;
        private BaseStatement baseStatement;
        private MethodInfo methodInfo;
        private String methodName;

        public ParserContext(BaseStatement baseStatement, MethodInfo methodInfo, String methodName) {
            this.baseStatement = baseStatement;
            this.methodInfo = methodInfo;
            this.methodName = methodName;
        }

        public <T extends BaseStatement> T getBaseStatement() {
            return (T) baseStatement;
        }

        public MethodInfo getMethodInfo() {
            return methodInfo;
        }

        public String getMethodName() {
            return methodName;
        }
    }
}
