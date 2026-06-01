package com.mybatisgx.dsl.method;

import com.google.common.collect.Maps;
import com.mybatisgx.dsl.method.model.MgxqlContext;
import com.mybatisgx.model.*;
import com.mybatisgx.model.handler.MybatisgxSyntaxHandler;
import com.mybatisgx.syntax.MethodNameParser;
import com.mybatisgx.syntax.MethodNameParserBaseVisitor;
import com.mybatisgx.utils.FieldNameUtils;
import org.antlr.v4.runtime.CommonTokenStream;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisgxSyntaxHandler.class);

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
            MgxqlContext mgxqlContext = context.getMgxqlContext();
            MethodNameParser.Select_item_clauseContext selectItemContext = (MethodNameParser.Select_item_clauseContext) node;
            if (selectItemContext.select_column() != null) {
                mgxqlContext.setSelectItemType(SelectItemType.COLUMN);
            }
            if (selectItemContext.select_count() != null) {
                mgxqlContext.setSelectItemType(SelectItemType.COUNT);
            }
        }
    }

    public static class SelectFromHandler implements SyntaxNodeHandler {

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
            MgxqlContext mgxqlContext = context.getMgxqlContext();
            SqlCommandType sqlCommandType = context.getMethodInfo().getSqlCommandType();
            if (sqlCommandType != SqlCommandType.SELECT) {
                return;
            }

            MethodInfo methodInfo = context.getMethodInfo();
            Class<?> returnType = methodInfo.getMapperInfo().getEntityClass();
            mgxqlContext.setFrom(String.format("from %s", returnType.getSimpleName()));
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
            MgxqlContext mgxqlContext = context.getMgxqlContext();
            MethodNameParser.LimitContext limitContext = (MethodNameParser.LimitContext) node;

            MethodNameParser.Limit_topContext limitTopContext = limitContext.limit_top();
            if (limitTopContext != null) {
                String limitCount = StringUtils.remove(limitTopContext.getText(), "Top");
                List<String> limitList = Arrays.asList("limit", limitCount);
                mgxqlContext.setLimitList(limitList);
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
            List<String> whereList = whereClauseVisitor.visit(node);
            whereList.add(0, "where");
            context.getMgxqlContext().setWhereList(whereList);
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
            List<String> orderByList = new ArrayList<>();
            orderByList.add("order by");
            MethodNameParser.Order_by_clauseContext orderByClauseContext = (MethodNameParser.Order_by_clauseContext) node;
            for (MethodNameParser.Order_by_itemContext orderByItem : orderByClauseContext.order_by_item()) {
                orderByList.add(orderByItem.field().getText());
                orderByList.add(orderByItem.order_by_direction().getText());
            }
            context.getMgxqlContext().setOrderByList(orderByList);
        }
    }

    public static class WhereClauseVisitor extends MethodNameParserBaseVisitor<List<String>> {

        private ConditionTermParser conditionTermParser;

        public WhereClauseVisitor(ParserContext context) {
            this.conditionTermParser = new ConditionTermParser(context);
        }

        @Override
        public List<String> visitCondition_expression(MethodNameParser.Condition_expressionContext ctx) {
            LOGGER.debug("处理条件表达式: {}", ctx.getText());
            List<String> whereList = new ArrayList();
            MethodNameParser.Or_expressionContext orExpressionContext = ctx.or_expression();
            MethodNameParser.Logic_orContext logicOrContext = null;
            for (int i = 0; i < orExpressionContext.getChildCount(); i++) {
                ParseTree parseTree = orExpressionContext.getChild(i);
                if (parseTree instanceof MethodNameParser.Logic_orContext) {
                    logicOrContext = (MethodNameParser.Logic_orContext) parseTree;
                }
                if (parseTree instanceof MethodNameParser.And_expressionContext) {
                    List<String> andWhereList = this.parseAndExpression((MethodNameParser.And_expressionContext) parseTree, logicOrContext);
                    whereList.addAll(andWhereList);
                }
            }
            return whereList;
        }

        private List<String> parseAndExpression(MethodNameParser.And_expressionContext andExpressionContext, MethodNameParser.Logic_orContext logicOrContext) {
            List<String> whereList = new ArrayList();
            MethodNameParser.Logic_andContext logicAndContext = null;
            for (int i = 0; i < andExpressionContext.getChildCount(); i++) {
                ParseTree parseTree = andExpressionContext.getChild(i);
                if (parseTree instanceof MethodNameParser.Condition_termContext) {
                    MethodNameParser.Condition_termContext conditionTermContext = (MethodNameParser.Condition_termContext) parseTree;
                    MethodNameParser.Condition_expressionContext conditionExpressionContext = conditionTermContext.condition_expression();
                    if (conditionExpressionContext != null) {
                        List<String> childWhereList = this.visitCondition_expression(conditionExpressionContext);
                        this.setLogicOperator(childWhereList, logicOrContext, logicAndContext);
                        whereList.addAll(childWhereList);
                    }
                    MethodNameParser.Field_comparison_opContext fieldComparisonOpContext = conditionTermContext.field_comparison_op();
                    if (fieldComparisonOpContext != null) {
                        List<String> whereTermList = this.conditionTermParser.parse(fieldComparisonOpContext);
                        this.setLogicOperator(whereList, logicOrContext, logicAndContext);
                        whereList.addAll(whereTermList);
                    }
                }
                if (parseTree instanceof MethodNameParser.Logic_andContext) {
                    logicAndContext = (MethodNameParser.Logic_andContext) parseTree;
                }
            }
            return whereList;
        }

        private void setLogicOperator(List<String> whereList, MethodNameParser.Logic_orContext logicOpOrClauseContext, MethodNameParser.Logic_andContext logicOpAndClauseContext) {
            if (logicOpOrClauseContext != null) {
                whereList.add(LogicOperator.OR.getValue());
            } else if (logicOpAndClauseContext != null) {
                whereList.add(LogicOperator.AND.getValue());
            } else {
                // 如果没有逻辑操作符，可能是第一个条件，保持默认值
            }
        }

        @Override
        public List<String> visitCondition_term(MethodNameParser.Condition_termContext ctx) {
            LOGGER.debug("处理条件单元: {}", ctx.getText());
            return super.visitCondition_term(ctx);
        }

        @Override
        protected boolean shouldVisitNextChild(RuleNode node, List<String> currentResult) {
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

        public List<String> parse(MethodNameParser.Field_comparison_opContext ctx) {
            LOGGER.debug("处理条件: {}", ctx.getText());
            List<String> whereList = new ArrayList();
            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree child = ctx.getChild(i);
                ConditionStrategy strategy = strategies.get(child.getClass());
                if (strategy != null) {
                    String whereName = strategy.apply(child, context);
                    whereList.add(whereName);
                }
            }
            return whereList;
        }
    }

    /**
     * 策略接口
     */
    public interface ConditionStrategy {

        String apply(ParseTree node, ParserContext context);
    }

    private static class FieldStrategyHandler implements ConditionStrategy {

        @Override
        public String apply(ParseTree node, ParserContext context) {
            return this.getFullTokenJavaColumn(node);
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
        public String apply(ParseTree node, ParserContext context) {
            return node.getText();
        }
    }

    private static class ComparisonNotOperatorStrategyHandler implements ConditionStrategy {

        @Override
        public String apply(ParseTree node, ParserContext context) {
            return node.getText();
        }
    }

    private static class ComparisonNullOperatorStrategyHandler implements ConditionStrategy {

        @Override
        public String apply(ParseTree node, ParserContext context) {
            return node.getText();
        }
    }

    public static class ParserContext {

        private CommonTokenStream tokens;
        private ParseTree root;
        private EntityInfo entityInfo;
        private MgxqlContext mgxqlContext;
        private MethodInfo methodInfo;
        private MethodParamInfo methodParamInfo;
        private ConditionOriginType conditionOriginType;
        private String methodName;

        public ParserContext(
                CommonTokenStream tokens,
                ParseTree root,
                EntityInfo entityInfo,
                MgxqlContext mgxqlContext,
                MethodInfo methodInfo,
                MethodParamInfo methodParamInfo,
                ConditionOriginType conditionOriginType,
                String methodName
        ) {
            this.tokens = tokens;
            this.root = root;
            this.entityInfo = entityInfo;
            this.mgxqlContext = mgxqlContext;
            this.methodInfo = methodInfo;
            this.methodParamInfo = methodParamInfo;
            this.conditionOriginType = conditionOriginType;
            this.methodName = methodName;
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

        public MgxqlContext getMgxqlContext() {
            return mgxqlContext;
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

        public String getMethodName() {
            return methodName;
        }
    }
}
