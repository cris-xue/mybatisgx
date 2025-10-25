package com.lc.mybatisx.model.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.syntax.method.name.MethodNameLexer;
import com.lc.mybatisx.syntax.method.name.MethodNameParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodNameAstHandler {

    private static final Map<String, String> TOKEN_MAP = new HashMap<>();

    static {
        TOKEN_MAP.put("By", "");
        TOKEN_MAP.put("And", "and");
        TOKEN_MAP.put("Or", "or");

        TOKEN_MAP.put("Lt", "<");
        TOKEN_MAP.put("Lteq", "<=");
        TOKEN_MAP.put("Gt", ">");
        TOKEN_MAP.put("Gteq", ">=");
        TOKEN_MAP.put("In", "in");
        TOKEN_MAP.put("Is", "=");
        TOKEN_MAP.put("Eq", "=");
        TOKEN_MAP.put("Not", "<>");
        TOKEN_MAP.put("Like", "like");
        TOKEN_MAP.put("Between", "between");
    }

    public void execute(EntityInfo entityInfo, MethodInfo methodInfo) {
        this.execute(entityInfo, methodInfo, ConditionOriginType.METHOD_NAME, methodInfo.getMethodName());
    }

    public void execute(EntityInfo entityInfo, MethodInfo methodInfo, ConditionOriginType conditionOriginType, String methodName) {
        CharStream charStream = CharStreams.fromString(methodName);
        MethodNameLexer methodNameLexer = new MethodNameLexer(charStream);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);
        ParseTree sqlStatementContext = methodNameParser.sql_statement();
        this.getTokens(entityInfo, methodInfo, conditionOriginType, sqlStatementContext);
    }

    private void getTokens(EntityInfo entityInfo, MethodInfo methodInfo, ConditionOriginType conditionOriginType, ParseTree parseTree) {
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            String token = parseTreeChild.getText();
            String parentSimpleName = parseTreeChild.getParent().getClass().getSimpleName();
            String simpleName = parseTreeChild.getClass().getSimpleName();

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
            } else if (parseTreeChild instanceof MethodNameParser.Where_clauseContext) {
                List<ConditionInfo> conditionInfoList = this.parseWhereClause(entityInfo, conditionOriginType, parseTreeChild);
                methodInfo.setConditionInfoList(conditionInfoList);
            } else if (parseTreeChild instanceof MethodNameParser.EndContext) {
                return;
            } else {
                this.getTokens(entityInfo, methodInfo, conditionOriginType, parseTreeChild);
            }
        }
    }

    private List<ConditionInfo> parseWhereClause(EntityInfo entityInfo, ConditionOriginType conditionOriginType, ParseTree whereClause) {
        List<ConditionInfo> conditionInfoList = new ArrayList<>();
        Integer conditionCount = 0;
        int childCount = whereClause.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree whereChildItem = whereClause.getChild(i);
            if (whereChildItem instanceof MethodNameParser.Where_start_clauseContext) {
                if (i != 0) {
                    throw new RuntimeException("语法错误，条件必须以By开头");
                }
            } else if (whereChildItem instanceof MethodNameParser.Condition_clauseContext) {
                ConditionInfo conditionInfo = new ConditionInfo(conditionCount++, conditionOriginType);
                this.parseCondition(entityInfo, conditionInfo, conditionOriginType, whereChildItem);
                conditionInfoList.add(conditionInfo);
            } else {
                throw new RuntimeException("不支持的语法");
            }
        }
        return conditionInfoList;
    }

    private void parseCondition(EntityInfo entityInfo, ConditionInfo conditionInfo, ConditionOriginType conditionOriginType, ParseTree parseTree) {
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            conditionInfo.setOriginSegment(parseTreeChild.getText());
            if (parseTreeChild instanceof MethodNameParser.Condition_item_clauseContext) {
                this.parseConditionItem(entityInfo, conditionInfo, conditionOriginType, parseTreeChild);
            }
        }
    }

    private void parseConditionItem(EntityInfo entityInfo, ConditionInfo conditionInfo, ConditionOriginType conditionOriginType, ParseTree parseTree) {
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            String token = parseTreeChild.getText();
            if (parseTreeChild instanceof MethodNameParser.Logic_op_clauseContext) {
                conditionInfo.setConditionOriginType(conditionOriginType);
                conditionInfo.setLogicOperator(LogicOperator.getLogicOperator(token));
            } else if (parseTreeChild instanceof MethodNameParser.Field_condition_op_clauseContext) {
                if (conditionOriginType == ConditionOriginType.ENTITY_FIELD) {
                    String conditionColumnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, token);
                    conditionInfo.setColumnName(conditionColumnName);
                }
                this.parseConditionItem(entityInfo, conditionInfo, conditionOriginType, parseTreeChild);
            } else if (parseTreeChild instanceof MethodNameParser.Field_clauseContext) {
                String conditionColumnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, token);
                ColumnInfo columnInfo = entityInfo.getColumnInfo(conditionColumnName);
                if (columnInfo == null) {
                    throw new RuntimeException("方法条件或者实体中条件与数据库库实体无法对应：" + conditionColumnName);
                }
                if (conditionOriginType == ConditionOriginType.METHOD_NAME) {
                    conditionInfo.setColumnName(conditionColumnName);
                }
                conditionInfo.setColumnInfo(columnInfo);
            } else if (parseTreeChild instanceof MethodNameParser.Comparison_op_clauseContext) {
                conditionInfo.setComparisonOperator(ComparisonOperator.getComparisonOperator(token));
            } else {
                throw new RuntimeException("不支持的语法");
            }
        }
    }
}
