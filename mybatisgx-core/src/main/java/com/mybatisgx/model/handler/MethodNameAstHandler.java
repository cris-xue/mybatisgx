package com.mybatisgx.model.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.model.*;
import com.mybatisgx.model.*;
import com.mybatisgx.syntax.MethodNameLexer;
import com.mybatisgx.syntax.MethodNameParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MethodNameAstHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodNameAstHandler.class);

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
