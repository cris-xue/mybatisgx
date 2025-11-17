package com.mybatisgx.model.handler;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.mybatisgx.model.*;
import com.mybatisgx.syntax.MethodNameLexer;
import com.mybatisgx.syntax.MethodNameParser;
import com.mybatisgx.syntax.MethodNameParserBaseVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * mybatisgx语法处理器
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class MybatisgxSyntaxHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisgxSyntaxHandler.class);

    public void execute(EntityInfo entityInfo, MethodInfo methodInfo, MethodParamInfo methodParamInfo, ConditionOriginType conditionOriginType, String methodName) {
        ConditionContext conditionContext = new ConditionContext();
        conditionContext.setEntityInfo(entityInfo);
        conditionContext.setMethodInfo(methodInfo);
        conditionContext.setMethodParamInfo(methodParamInfo);
        conditionContext.setConditionOriginType(conditionOriginType);

        CharStream charStream = CharStreams.fromString(methodName);
        MethodNameLexer methodNameLexer = new MethodNameLexer(charStream);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);
        ParseTree sqlStatementContext = methodNameParser.sql_statement();
        this.getTokens(conditionContext, sqlStatementContext);
    }

    private void getTokens(ConditionContext conditionContext, ParseTree parseTree) {
        MethodInfo methodInfo = conditionContext.getMethodInfo();
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            if (parseTreeChild instanceof TerminalNodeImpl) {
                // methodNameInfo.addMethodNameWhereInfo(token);
            } else if (parseTreeChild instanceof MethodNameParser.Field_clauseContext) {
                // methodNameInfo.addMethodNameWhereInfo(token);
            } else if (parseTreeChild instanceof MethodNameParser.Insert_clauseContext) {
                methodInfo.setSqlCommandType(SqlCommandType.INSERT);
            } else if (parseTreeChild instanceof MethodNameParser.Delete_clauseContext) {
                methodInfo.setSqlCommandType(SqlCommandType.DELETE);
            } else if (parseTreeChild instanceof MethodNameParser.Update_clauseContext) {
                methodInfo.setSqlCommandType(SqlCommandType.UPDATE);
            } else if (parseTreeChild instanceof MethodNameParser.Select_clauseContext) {
                methodInfo.setSqlCommandType(SqlCommandType.SELECT);
            } else if (parseTreeChild instanceof MethodNameParser.Aggregate_operation_clauseContext) {
                System.out.println("聚合函数暂未实现");
            } else if (parseTreeChild instanceof MethodNameParser.Where_clauseContext) {
                List<ConditionInfo> conditionInfoList = this.parseWhere(conditionContext, parseTreeChild);
                methodInfo.setConditionInfoList(conditionInfoList);
            } else if (parseTreeChild instanceof MethodNameParser.Group_by_clauseContext) {
                System.out.println("group by暂未实现");
            } else if (parseTreeChild instanceof MethodNameParser.Order_by_clauseContext) {
                System.out.println("order by暂未实现");
            } else if (parseTreeChild instanceof MethodNameParser.EndContext) {
                return;
            } else {
                this.getTokens(conditionContext, parseTreeChild);
            }
        }
    }

    private List<ConditionInfo> parseWhere(ConditionContext conditionContext, ParseTree whereClause) {
        List<ConditionInfo> conditionInfoList = new ArrayList<>();
        int childCount = whereClause.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree whereChildItem = whereClause.getChild(i);
            if (whereChildItem instanceof MethodNameParser.Where_start_clauseContext) {
                if (i != 0) {
                    throw new RuntimeException("语法错误，条件必须以By开头");
                }
            } else if (whereChildItem instanceof MethodNameParser.Condition_group_clauseContext) {
                conditionInfoList = this.parseConditionGroup(conditionContext, whereChildItem);
            } else if (whereChildItem instanceof MethodNameParser.Ignore_reserved_word_clauseContext) {
                LOGGER.info("忽略保留字：{}", whereChildItem.getText());
            } else {
                throw new RuntimeException("不支持的语法");
            }
        }
        return conditionInfoList;
    }

    private List<ConditionInfo> parseConditionGroup(ConditionContext conditionContext, ParseTree conditionGroup) {
        MethodParamInfo methodParamInfo = conditionContext.getMethodParamInfo();
        ConditionOriginType conditionOriginType = conditionContext.getConditionOriginType();

        List<ConditionInfo> conditionInfoList = new ArrayList<>();
        Integer conditionCount = 0;
        int childCount = conditionGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree conditionGroupChild = conditionGroup.getChild(i);
            if (conditionGroupChild instanceof MethodNameParser.Condition_item_clauseContext) {
                ConditionInfo conditionInfo = new ConditionInfo(conditionCount++, conditionOriginType, methodParamInfo);
                conditionInfo.setOriginSegment(conditionGroupChild.getText());
                this.parseConditionItem(conditionContext, conditionInfo, conditionGroupChild);
                conditionInfoList.add(conditionInfo);
            } else {
                throw new RuntimeException("不支持的语法");
            }
        }
        return conditionInfoList;
    }

    private void parseConditionItem(ConditionContext conditionContext, ConditionInfo conditionInfo, ParseTree conditionItem) {
        EntityInfo entityInfo = conditionContext.getEntityInfo();
        ConditionOriginType conditionOriginType = conditionContext.getConditionOriginType();

        int childCount = conditionItem.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree conditionItemChild = conditionItem.getChild(i);
            String token = conditionItemChild.getText();
            if (conditionItemChild instanceof MethodNameParser.Logic_op_clauseContext) {
                conditionInfo.setLogicOperator(LogicOperator.getLogicOperator(token));
            } else if (conditionItemChild instanceof MethodNameParser.Left_bracket_clauseContext) {
                conditionInfo.setLeftBracket("(");
            } else if (conditionItemChild instanceof MethodNameParser.Right_bracket_clauseContext) {
                conditionInfo.setRightBracket(")");
            } else if (conditionItemChild instanceof MethodNameParser.Field_comparison_op_clauseContext) {
                if (conditionOriginType == ConditionOriginType.ENTITY_FIELD) {
                    String conditionColumnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, token);
                    conditionInfo.setColumnName(conditionColumnName);
                }
                this.parseConditionItem(conditionContext, conditionInfo, conditionItemChild);
            } else if (conditionItemChild instanceof MethodNameParser.Field_clauseContext) {
                String conditionColumnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, token);
                ColumnInfo columnInfo = entityInfo.getColumnInfo(conditionColumnName);
                if (columnInfo == null) {
                    throw new RuntimeException("方法条件或者实体中条件与数据库库实体无法对应：" + conditionColumnName);
                }
                if (conditionOriginType == ConditionOriginType.METHOD_NAME) {
                    conditionInfo.setColumnName(conditionColumnName);
                }
                conditionInfo.setColumnInfo(columnInfo);
            } else if (conditionItemChild instanceof MethodNameParser.Comparison_op_clauseContext) {
                conditionInfo.setComparisonOperator(ComparisonOperator.getComparisonOperator(token));
            } else if (conditionItemChild instanceof MethodNameParser.Condition_group_clauseContext) {
                List<ConditionInfo> conditionInfoList = this.parseConditionGroup(conditionContext, conditionItemChild);
                ConditionGroupInfo conditionGroupInfo = new ConditionGroupInfo();
                conditionGroupInfo.setConditionInfoList(conditionInfoList);
                conditionInfo.setConditionGroupInfo(conditionGroupInfo);
            } else {
                LOGGER.error("不支持的语法:{} -- {}", conditionItem.getText(), token);
                throw new RuntimeException("不支持的语法");
            }
        }
    }

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

    private static class SqlCommandTypeHandler implements SyntaxNodeHandler {

        private static final Map<Class<?>, SqlCommandType> COMMAND_MAPPINGS = Maps.newHashMap();

        static {
            COMMAND_MAPPINGS.put(MethodNameParser.Insert_clauseContext.class, SqlCommandType.INSERT);
            COMMAND_MAPPINGS.put(MethodNameParser.Delete_clauseContext.class, SqlCommandType.DELETE);
            COMMAND_MAPPINGS.put(MethodNameParser.Update_clauseContext.class, SqlCommandType.UPDATE);
            COMMAND_MAPPINGS.put(MethodNameParser.Select_clauseContext.class, SqlCommandType.SELECT);
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

    private static class WhereClauseHandler implements SyntaxNodeHandler {

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
            WhereClauseVisitor visitor = new WhereClauseVisitor(context);
            List<ConditionInfo> conditions = visitor.visit((MethodNameParser.Where_clauseContext) node);
            context.getMethodInfo().setConditionInfoList(conditions);
        }
    }

    private static class AggregateHandler implements SyntaxNodeHandler {

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean support(ParseTree node) {
            return node instanceof MethodNameParser.Aggregate_operation_clauseContext;
        }

        @Override
        public void handle(ParseTree node, ParserContext context) {
            // 聚合函数处理逻辑
            LOGGER.info("处理聚合函数: {}", node.getText());
        }
    }

    private static class WhereClauseVisitor extends MethodNameParserBaseVisitor<List<ConditionInfo>> {

        private ParserContext context;
        private ConditionParser conditionParser;

        public WhereClauseVisitor(ParserContext context) {
            this.context = context;
            this.conditionParser = new ConditionParser(context);
        }

        @Override
        public List<ConditionInfo> visitCondition_group_clause(MethodNameParser.Condition_group_clauseContext ctx) {
            return ctx.condition_item_clause().stream()
                    .map(this::parseConditionItem)
                    .collect(Collectors.toList());
        }

        private ConditionInfo parseConditionItem(MethodNameParser.Condition_item_clauseContext ctx) {
            return conditionParser.parse(ctx);
        }
    }

    private static class ConditionParser {

        private ParserContext context;
        private Map<Class<?>, ConditionStrategy> strategies = new HashMap<>();

        public ConditionParser(ParserContext context) {
            this.context = context;
            /*this.strategies.put(MethodNameParser.Field_clauseContext.class, new FieldConditionStrategy());
            this.strategies.put(MethodNameParser.Comparison_op_clauseContext.class, new ComparisonStrategy());
            this.strategies.put(MethodNameParser.Logic_op_clauseContext.class, new LogicOperatorStrategy());*/
        }

        public ConditionInfo parse(MethodNameParser.Condition_item_clauseContext ctx) {
            ConditionInfo conditionInfo = null; // new ConditionInfo();

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

    // 策略接口
    public interface ConditionStrategy {
        void apply(ParseTree node, ConditionInfo conditionInfo, ParserContext context);
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

    public static class ConditionContext {

        private EntityInfo entityInfo;
        private MethodInfo methodInfo;
        private MethodParamInfo methodParamInfo;
        private ConditionOriginType conditionOriginType;

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
    }
}
