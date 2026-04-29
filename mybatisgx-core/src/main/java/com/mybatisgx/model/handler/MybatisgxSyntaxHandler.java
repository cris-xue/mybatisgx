package com.mybatisgx.model.handler;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * mybatisgx语法处理器
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class MybatisgxSyntaxHandler {

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
            MethodInfo methodInfo = context.getMethodInfo();
            MethodNameParser.Select_item_clauseContext selectItemContext = (MethodNameParser.Select_item_clauseContext) node;

            SelectItemInfo selectItemInfo = new SelectItemInfo();
            if (selectItemContext.select_column() != null) {
                selectItemInfo.setSelectItemType(SelectItemType.COLUMN);
            }
            if (selectItemContext.select_count() != null) {
                selectItemInfo.setSelectItemType(SelectItemType.COUNT);
            }
            methodInfo.setSelectItemInfo(selectItemInfo);
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
            MethodInfo methodInfo = context.getMethodInfo();
            MethodNameParser.LimitContext limitContext = (MethodNameParser.LimitContext) node;

            MethodNameParser.Limit_topContext limitTopContext = limitContext.limit_top();
            if (limitTopContext != null) {
                String limitCount = StringUtils.remove(limitTopContext.getText(), "Top");
                SelectPageInfo selectPageInfo = new SelectPageInfo(0, Integer.parseInt(limitCount));
                methodInfo.setSelectPageInfo(selectPageInfo);
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
            return node instanceof MethodNameParser.Order_by_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.debug("排序函数: {}", node.getText());
            List<SelectOrderByInfo> selectOrderByInfoList = new ArrayList<>();
            MethodNameParser.Order_by_clauseContext orderByClauseContext = (MethodNameParser.Order_by_clauseContext) node;
            for (MethodNameParser.Order_by_itemContext orderByItem : orderByClauseContext.order_by_item()) {
                selectOrderByInfoList.add(new SelectOrderByInfo(orderByItem.field().getText(), orderByItem.order_by_direction().getText()));
            }
            context.getMethodInfo().setSelectOrderByInfoList(selectOrderByInfoList);
        }
    }

    public static class WhereClauseVisitor extends MethodNameParserBaseVisitor<List<ConditionInfo>> {

        private AtomicInteger conditionIndex = new AtomicInteger(0);
        private ParserContext context;
        private ConditionTermParser conditionTermParser;

        public WhereClauseVisitor(ParserContext context) {
            this.context = context;
            this.conditionTermParser = new ConditionTermParser(context);
        }

        @Override
        public List<ConditionInfo> visitCondition_expression(MethodNameParser.Condition_expressionContext ctx) {
            LOGGER.debug("处理条件表达式: {}", ctx.getText());
            List<ConditionInfo> conditionInfoList = new ArrayList();
            MethodNameParser.Or_expressionContext orExpressionContext = ctx.or_expression();
            MethodNameParser.Logic_orContext logicOrContext = null;
            for (int i = 0; i < orExpressionContext.getChildCount(); i++) {
                ParseTree parseTree = orExpressionContext.getChild(i);
                if (parseTree instanceof MethodNameParser.Logic_orContext) {
                    logicOrContext = (MethodNameParser.Logic_orContext) parseTree;
                }
                if (parseTree instanceof MethodNameParser.And_expressionContext) {
                    List<ConditionInfo> andConditionInfoList = this.parseAndExpression((MethodNameParser.And_expressionContext) parseTree, logicOrContext);
                    conditionInfoList.addAll(andConditionInfoList);
                }
            }
            return conditionInfoList;
        }

        public Integer getConditionIndex() {
            return conditionIndex.getAndIncrement();
        }

        private List<ConditionInfo> parseAndExpression(MethodNameParser.And_expressionContext andExpressionContext, MethodNameParser.Logic_orContext logicOrContext) {
            List<ConditionInfo> conditionInfoList = new ArrayList();
            MethodNameParser.Logic_andContext logicAndContext = null;
            for (int i = 0; i < andExpressionContext.getChildCount(); i++) {
                ParseTree parseTree = andExpressionContext.getChild(i);
                if (parseTree instanceof MethodNameParser.Condition_termContext) {
                    MethodNameParser.Condition_termContext conditionTermContext = (MethodNameParser.Condition_termContext) parseTree;
                    MethodNameParser.Condition_expressionContext conditionExpressionContext = conditionTermContext.condition_expression();
                    if (conditionExpressionContext != null) {
                        List<ConditionInfo> childConditionInfoList = this.visitCondition_expression(conditionExpressionContext);
                        ConditionInfo conditionInfo = new ConditionInfo(this.getConditionIndex(), context.conditionOriginType, context.methodParamInfo);
                        this.setLogicOperator(conditionInfo, logicOrContext, logicAndContext);
                        this.handleBrackets(conditionTermContext, conditionInfo);
                        conditionInfo.setConditionInfoList(childConditionInfoList);
                        conditionInfoList.add(conditionInfo);
                    }
                    MethodNameParser.Field_comparison_opContext fieldComparisonOpContext = conditionTermContext.field_comparison_op();
                    if (fieldComparisonOpContext != null) {
                        ConditionInfo conditionInfo = this.conditionTermParser.parse(this.getConditionIndex(), fieldComparisonOpContext);
                        conditionInfo.setOriginSegment(conditionTermContext.getText());
                        this.setLogicOperator(conditionInfo, logicOrContext, logicAndContext);
                        conditionInfoList.add(conditionInfo);
                    }
                }
                if (parseTree instanceof MethodNameParser.Logic_andContext) {
                    logicAndContext = (MethodNameParser.Logic_andContext) parseTree;
                }
            }
            return conditionInfoList;
        }

        private void handleBrackets(MethodNameParser.Condition_termContext conditionTermContext, ConditionInfo conditionInfo) {
            MethodNameParser.Left_bracketContext leftBracketClauseContext = conditionTermContext.left_bracket();
            if (leftBracketClauseContext != null) {
                conditionInfo.setLeftBracket(leftBracketClauseContext.LEFT_BRACKET().getText());
            }
            MethodNameParser.Right_bracketContext rightBracketClauseContext = conditionTermContext.right_bracket();
            if (rightBracketClauseContext != null) {
                conditionInfo.setRightBracket(rightBracketClauseContext.RIGHT_BRACKET().getText());
            }
        }

        private void setLogicOperator(ConditionInfo conditionInfo,
                                      MethodNameParser.Logic_orContext logicOpOrClauseContext,
                                      MethodNameParser.Logic_andContext logicOpAndClauseContext) {
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
        public List<ConditionInfo> visitCondition_term(MethodNameParser.Condition_termContext ctx) {
            LOGGER.debug("处理条件单元: {}", ctx.getText());
            return super.visitCondition_term(ctx);
        }

        @Override
        protected boolean shouldVisitNextChild(RuleNode node, List<ConditionInfo> currentResult) {
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

        public ConditionInfo parse(int index, MethodNameParser.Field_comparison_opContext ctx) {
            LOGGER.debug("处理条件: {}", ctx.getText());
            ConditionInfo conditionInfo = new ConditionInfo(index, context.conditionOriginType, context.methodParamInfo);
            if (context.conditionOriginType == ConditionOriginType.ENTITY_FIELD) {
                String conditionColumnName = ctx.getText();
                conditionColumnName = FieldNameUtils.upperCamelToLowerCamel(conditionColumnName.replace("$", ""));
                conditionInfo.setColumnName(conditionColumnName);
            }
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

    private static class FieldStrategyHandler implements ConditionStrategy {

        @Override
        public void apply(ParseTree node, ConditionInfo conditionInfo, ParserContext context) {
            String conditionColumnName = this.getFullTokenJavaColumn(node, context);
            ColumnInfo columnInfo = context.getEntityInfo().getColumnInfo(conditionColumnName);

            // 解决方法名末尾存在 Selective 的情况
            int rootStopIndex = ((MethodNameParser.Sql_statementContext) context.getRoot()).getStop().getStopIndex();
            int fieldStopIndex = ((MethodNameParser.FieldContext) node).getStop().getStopIndex();
            if (fieldStopIndex == rootStopIndex && columnInfo == null) {
                conditionColumnName = this.getEndTokenJavaColumn(node, context);
                columnInfo = context.getEntityInfo().getColumnInfo(conditionColumnName);
            }
            // 解决字段中存在操作符的情况
            /*if (columnInfo == null) {
                conditionColumnName = this.getOperJavaColumn(node, context);
                columnInfo = context.getEntityInfo().getColumnInfo(conditionColumnName);
            }*/

            if (columnInfo == null) {
                throw new MybatisgxException("%s 方法条件字段或者实体条件字段在 %s 实体类中不存在", conditionColumnName, context.getEntityInfo().getClazzName());
            }
            if (context.getConditionOriginType() == ConditionOriginType.METHOD_NAME
                    || context.getConditionOriginType() == ConditionOriginType.STATEMENT_METHOD_NAME) {
                conditionInfo.setColumnName(conditionColumnName);
            }
            conditionInfo.setColumnInfo(columnInfo);
        }

        private String getFullTokenJavaColumn(ParseTree node, ParserContext context) {
            MethodNameParser.FieldContext fieldContext = (MethodNameParser.FieldContext) node;
            MethodNameParser.Field_identifierContext fieldIdentifierContext = fieldContext.field_identifier();
            if (fieldIdentifierContext != null) {
                String token = fieldIdentifierContext.getText();
                return this.tokenToJavaColumn(token);
            }
            MethodNameParser.Escaped_identifierContext escapedIdentifierContext = fieldContext.escaped_identifier();
            if (escapedIdentifierContext != null) {
                String token = escapedIdentifierContext.getText();
                return this.tokenToJavaColumn(token.replace("$", ""));
            }
            return StringUtils.EMPTY;
        }

        private String getEndTokenJavaColumn(ParseTree node, ParserContext context) {
            /*List<MethodNameParser.Field_identifierContext> terminalNodeList = ((MethodNameParser.FieldContext) node).field_identifier();
            for (int i = terminalNodeList.size() - 1; i > 0; i--) {
                TerminalNode terminalNode = terminalNodeList.get(i);
                if (END_SEMANTIC.contains(terminalNode.getText())) {
                    int startTokenIndex = ((MethodNameParser.FieldContext) node).getStart().getTokenIndex();
                    int stopTokenIndex = terminalNode.getSymbol().getTokenIndex();
                    String aaa = context.getTokens().get(startTokenIndex, stopTokenIndex - 1).stream().map(t -> t.getText()).collect(Collectors.joining(""));
                    return this.tokenToJavaColumn(aaa);
                }
            }*/
            return "";
        }

        private String getComparisonJavaColumn(ParseTree node, ParserContext context) {
            /*List<TerminalNode> terminalNodeList = ((MethodNameParser.FieldContext) node).field_identifier();
            for (int i = terminalNodeList.size() - 1; i > 0; i--) {
                TerminalNode terminalNode = terminalNodeList.get(i);
                if (END_SEMANTIC.contains(terminalNode.getText())) {
                    int startTokenIndex = ((MethodNameParser.FieldContext) node).getStart().getTokenIndex();
                    int stopTokenIndex = terminalNode.getSymbol().getTokenIndex();
                    String aaa = context.getTokens().get(startTokenIndex, stopTokenIndex - 1).stream().map(t -> t.getText()).collect(Collectors.joining(""));
                    return this.tokenToJavaColumn(aaa);
                }
            }*/
            return "";
        }

        private String tokenToJavaColumn(String token) {
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, token);
        }
    }

    private static class ComparisonOperatorStrategyHandler implements ConditionStrategy {

        @Override
        public void apply(ParseTree node, ConditionInfo conditionInfo, ParserContext context) {
            String token = node.getText();
            conditionInfo.setComparisonOperator(ComparisonOperator.getComparisonOperator(token));
        }
    }

    private static class ComparisonNotOperatorStrategyHandler implements ConditionStrategy {

        @Override
        public void apply(ParseTree node, ConditionInfo conditionInfo, ParserContext context) {
            String token = node.getText();
            conditionInfo.setComparisonNotOperator(ComparisonOperator.getComparisonOperator(token));
        }
    }

    private static class ComparisonNullOperatorStrategyHandler implements ConditionStrategy {

        @Override
        public void apply(ParseTree node, ConditionInfo conditionInfo, ParserContext context) {
            String token = node.getText();
            conditionInfo.setComparisonOperator(ComparisonOperator.getComparisonOperator(token));
        }
    }

    public static class ParserContext {

        private CommonTokenStream tokens;
        private ParseTree root;
        private EntityInfo entityInfo;
        private MethodInfo methodInfo;
        private MethodParamInfo methodParamInfo;
        private ConditionOriginType conditionOriginType;
        private String methodName;

        public ParserContext(
                CommonTokenStream tokens,
                ParseTree root,
                EntityInfo entityInfo,
                MethodInfo methodInfo,
                MethodParamInfo methodParamInfo,
                ConditionOriginType conditionOriginType,
                String methodName
        ) {
            this.tokens = tokens;
            this.root = root;
            this.entityInfo = entityInfo;
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
