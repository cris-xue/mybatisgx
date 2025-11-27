package com.mybatisgx.model.handler;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.mybatisgx.model.*;
import com.mybatisgx.syntax.MethodNameParser;
import com.mybatisgx.syntax.MethodNameParserBaseVisitor;
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

/**
 * mybatisgx语法处理器
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class MybatisgxSyntaxNewHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisgxSyntaxNewHandler.class);

    /**
     * 语法节点处理器接口
     * @author 薛承城
     * @date 2025/11/17 10:47
     */
    public interface SyntaxNodeHandler {

        int getOrder();

        boolean support(ParseTree node);

        void handle(ParseTree node, ParserContext context);
    }

    public static class SqlCommandTypeHandler implements SyntaxNodeHandler {

        private static final Map<Class<?>, SqlCommandType> COMMAND_MAPPINGS = Maps.newHashMap();

        static {
            COMMAND_MAPPINGS.put(MethodNameParser.Insert_clauseContext.class, SqlCommandType.INSERT);
            COMMAND_MAPPINGS.put(MethodNameParser.Delete_clauseContext.class, SqlCommandType.DELETE);
            COMMAND_MAPPINGS.put(MethodNameParser.Update_clauseContext.class, SqlCommandType.UPDATE);
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return COMMAND_MAPPINGS.containsKey(node.getClass());
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            SqlCommandType commandType = COMMAND_MAPPINGS.get(node.getClass());
            context.getMethodInfo().setSqlCommandType(commandType);
        }
    }

    public static class SelectItemHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MethodNameParser.Select_itemContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            LOGGER.info("处理查询: {}", node.getText());
            MethodInfo methodInfo = context.getMethodInfo();
            methodInfo.setSqlCommandType(SqlCommandType.SELECT);
            MethodNameParser.Select_itemContext selectItemContext = (MethodNameParser.Select_itemContext) node;

            SelectItemInfo selectItemInfo = new SelectItemInfo();
            if (selectItemContext.select_column() != null) {
                selectItemInfo.setSelectItemType(SelectItemType.COLUMN);
            }
            if (selectItemContext.select_count() != null) {
                selectItemInfo.setSelectItemType(SelectItemType.COUNT);
            }
            if (selectItemContext.select_exist() != null) {
                throw new UnsupportedOperationException("暂不支持exist");
            }
            methodInfo.setSelectItemInfo(selectItemInfo);

            if (selectItemContext.limit() != null) {
                MethodNameParser.LimitContext limitContext = selectItemContext.limit();
                MethodNameParser.Limit_topContext limitTopContext = limitContext.limit_top();
                if (limitTopContext != null) {
                    String limitCount = StringUtils.remove(limitTopContext.getText(), "Top");
                    SelectPageInfo selectPageInfo = new SelectPageInfo(0, Integer.parseInt(limitCount));
                    methodInfo.setSelectPageInfo(selectPageInfo);
                } else {
                    throw new UnsupportedOperationException("暂不支持" + limitContext.getText());
                }
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
            LOGGER.info("处理where条件: {}", node.getText());
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
            LOGGER.info("排序函数: {}", node.getText());
            List<SelectOrderByInfo> selectOrderByInfoList = new ArrayList<>();
            MethodNameParser.Order_by_clauseContext orderByClauseContext = (MethodNameParser.Order_by_clauseContext) node;
            for (MethodNameParser.Order_by_item_clauseContext orderByItem : orderByClauseContext.order_by_item_clause()) {
                selectOrderByInfoList.add(new SelectOrderByInfo(orderByItem.field_clause().getText(), orderByItem.order_by_direction().getText()));
            }
            context.getMethodInfo().setSelectOrderByInfoList(selectOrderByInfoList);
        }
    }

    public static class WhereClauseVisitor extends MethodNameParserBaseVisitor<List<ConditionInfo>> {

        private Integer conditionIndex = 0;
        private ParserContext context;
        private ConditionTermParser conditionTermParser;

        public WhereClauseVisitor(ParserContext context) {
            this.context = context;
            this.conditionTermParser = new ConditionTermParser(context);
        }

        @Override
        public List<ConditionInfo> visitCondition_expression(MethodNameParser.Condition_expressionContext ctx) {
            LOGGER.info("处理条件表达式: {}", ctx.getText());
            List<ConditionInfo> conditionInfoList = new ArrayList();
            MethodNameParser.Or_expressionContext orExpressionContext = ctx.or_expression();
            MethodNameParser.Logic_op_orContext logicOpOrContext = null;
            for (int i = 0; i < orExpressionContext.getChildCount(); i++) {
                ParseTree parseTree = orExpressionContext.getChild(i);
                if (parseTree instanceof MethodNameParser.Logic_op_orContext) {
                    logicOpOrContext = (MethodNameParser.Logic_op_orContext) parseTree;
                }
                if (parseTree instanceof MethodNameParser.And_expressionContext) {
                    List<ConditionInfo> andConditionInfoList = this.parseAndExpression((MethodNameParser.And_expressionContext) parseTree, logicOpOrContext);
                    conditionInfoList.addAll(andConditionInfoList);
                }
            }
            return conditionInfoList;
        }

        private List<ConditionInfo> parseAndExpression(MethodNameParser.And_expressionContext andExpressionContext, MethodNameParser.Logic_op_orContext logicOpOrContext) {
            List<ConditionInfo> conditionInfoList = new ArrayList();
            MethodNameParser.Logic_op_andContext logicOpAndContext = null;
            for (int i = 0; i < andExpressionContext.getChildCount(); i++) {
                ParseTree parseTree = andExpressionContext.getChild(i);
                if (parseTree instanceof MethodNameParser.Condition_termContext) {
                    MethodNameParser.Condition_termContext conditionTermContext = (MethodNameParser.Condition_termContext) parseTree;
                    MethodNameParser.Condition_expressionContext conditionExpressionContext = conditionTermContext.condition_expression();
                    if (conditionExpressionContext != null) {
                        List<ConditionInfo> childConditionInfoList = this.visitCondition_expression(conditionExpressionContext);
                        ConditionInfo conditionInfo = new ConditionInfo(conditionIndex++, context.conditionOriginType, context.methodParamInfo);
                        this.setLogicOperator(conditionInfo, logicOpOrContext, logicOpAndContext);
                        this.handleBrackets(conditionTermContext, conditionInfo);
                        conditionInfo.setConditionInfoList(childConditionInfoList);
                        conditionInfoList.add(conditionInfo);
                    }
                    MethodNameParser.Field_comparison_op_clauseContext fieldComparisonOpClauseContext = conditionTermContext.field_comparison_op_clause();
                    if (fieldComparisonOpClauseContext != null) {
                        ConditionInfo conditionInfo = this.conditionTermParser.parse(conditionIndex++, fieldComparisonOpClauseContext);
                        conditionInfo.setOriginSegment(conditionTermContext.getText());
                        this.setLogicOperator(conditionInfo, logicOpOrContext, logicOpAndContext);
                        conditionInfoList.add(conditionInfo);
                    }
                }
                if (parseTree instanceof MethodNameParser.Logic_op_andContext) {
                    logicOpAndContext = (MethodNameParser.Logic_op_andContext) parseTree;
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
                                      MethodNameParser.Logic_op_orContext logicOpOrClauseContext,
                                      MethodNameParser.Logic_op_andContext logicOpAndClauseContext) {
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
            LOGGER.info("处理条件单元: {}", ctx.getText());
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
            this.strategies.put(MethodNameParser.Field_clauseContext.class, new FieldStrategyHandler());
            this.strategies.put(MethodNameParser.Comparison_opContext.class, new ComparisonOperatorStrategyHandler());
            this.strategies.put(MethodNameParser.Comparison_not_opContext.class, new ComparisonNotOperatorStrategyHandler());
            this.strategies.put(MethodNameParser.Comparison_null_opContext.class, new ComparisonNullOperatorStrategyHandler());

        }

        public ConditionInfo parse(int index, MethodNameParser.Field_comparison_op_clauseContext ctx) {
            LOGGER.info("处理条件: {}", ctx.getText());
            ConditionInfo conditionInfo = new ConditionInfo(index, context.conditionOriginType, context.methodParamInfo);
            if (context.conditionOriginType == ConditionOriginType.ENTITY_FIELD) {
                String conditionColumnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, ctx.getText());
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
            String token = node.getText();
            String conditionColumnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, token);
            ColumnInfo columnInfo = context.getEntityInfo().getColumnInfo(conditionColumnName);
            if (columnInfo == null) {
                throw new RuntimeException("方法条件或者实体中条件与数据库库实体无法对应：" + conditionColumnName);
            }
            if (context.getConditionOriginType() == ConditionOriginType.METHOD_NAME) {
                conditionInfo.setColumnName(conditionColumnName);
            }
            conditionInfo.setColumnInfo(columnInfo);
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

        private EntityInfo entityInfo;
        private MethodInfo methodInfo;
        private MethodParamInfo methodParamInfo;
        private ConditionOriginType conditionOriginType;
        private String methodName;

        public ParserContext(EntityInfo entityInfo, MethodInfo methodInfo,
                             MethodParamInfo methodParamInfo, ConditionOriginType conditionOriginType,
                             String methodName) {
            this.entityInfo = entityInfo;
            this.methodInfo = methodInfo;
            this.methodParamInfo = methodParamInfo;
            this.conditionOriginType = conditionOriginType;
            this.methodName = methodName;
        }

        public EntityInfo getEntityInfo() {
            return entityInfo;
        }

        public void setEntityInfo(EntityInfo entityInfo) {
            this.entityInfo = entityInfo;
        }

        public MethodInfo getMethodInfo() {
            return methodInfo;
        }

        public void setMethodInfo(MethodInfo methodInfo) {
            this.methodInfo = methodInfo;
        }

        public MethodParamInfo getMethodParamInfo() {
            return methodParamInfo;
        }

        public void setMethodParamInfo(MethodParamInfo methodParamInfo) {
            this.methodParamInfo = methodParamInfo;
        }

        public ConditionOriginType getConditionOriginType() {
            return conditionOriginType;
        }

        public void setConditionOriginType(ConditionOriginType conditionOriginType) {
            this.conditionOriginType = conditionOriginType;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }
    }
}
