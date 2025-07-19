package com.lc.mybatisx.model.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.ConditionInfo;
import com.lc.mybatisx.model.EntityInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.syntax.MethodNameLexer;
import com.lc.mybatisx.syntax.MethodNameParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 条件解析
 *
 * @author ccxuef
 * @date 2025/7/19 20:42
 */
public class ConditionAstHandler {

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
        CharStream charStream = CharStreams.fromString(methodInfo.getQueryCondition());
        MethodNameLexer methodNameLexer = new MethodNameLexer(charStream);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);
        ParseTree sqlStatementContext = methodNameParser.sql_statement();

        getTokens(entityInfo, methodInfo, false, sqlStatementContext);
    }

    public void execute(EntityInfo entityInfo, MethodInfo methodInfo, Boolean conditionEntity, String methodName) {
        CharStream charStream = CharStreams.fromString(methodName);
        MethodNameLexer methodNameLexer = new MethodNameLexer(charStream);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);
        ParseTree sqlStatementContext = methodNameParser.sql_statement();

        getTokens(entityInfo, methodInfo, conditionEntity, sqlStatementContext);
    }

    private void getTokens(EntityInfo entityInfo, MethodInfo methodInfo, Boolean conditionEntity, ParseTree parseTree) {
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
                methodInfo.setAction("insert");
            } else if (parseTreeChild instanceof MethodNameParser.Delete_clauseContext) {
                methodInfo.setAction("delete");
            } else if (parseTreeChild instanceof MethodNameParser.Update_clauseContext) {
                methodInfo.setAction("update");
            } else if (parseTreeChild instanceof MethodNameParser.Select_clauseContext) {
                methodInfo.setAction("select");
            } else if (parseTreeChild instanceof MethodNameParser.Where_clauseContext) {
                List<ConditionInfo> conditionInfoList = this.parseWhereClause(entityInfo, conditionEntity, parseTreeChild);
                methodInfo.setConditionInfoList(conditionInfoList);
            } else if (parseTreeChild instanceof MethodNameParser.EndContext) {
                return;
            } else {
                this.getTokens(entityInfo, methodInfo, conditionEntity, parseTreeChild);
            }
        }
    }

    private List<ConditionInfo> parseWhereClause(EntityInfo entityInfo, Boolean conditionEntity, ParseTree whereClause) {
        List<ConditionInfo> conditionInfoList = new ArrayList<>();
        int childCount = whereClause.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree whereChildItem = whereClause.getChild(i);
            if (whereChildItem instanceof MethodNameParser.Where_start_clauseContext) {
                if (i != 0) {
                    throw new RuntimeException("语法错误，条件必须以By开头");
                }
            } else if (whereChildItem instanceof MethodNameParser.Condition_clauseContext) {
                ConditionInfo conditionInfo = new ConditionInfo();
                conditionInfo.setIndex(i);
                this.parseCondition(entityInfo, conditionInfo, conditionEntity, whereChildItem);
                conditionInfoList.add(conditionInfo);
            } else {
                throw new RuntimeException("不支持的语法");
            }
        }
        return conditionInfoList;
    }

    private void parseCondition(EntityInfo entityInfo, ConditionInfo conditionInfo, Boolean conditionEntity, ParseTree parseTree) {
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            if (parseTreeChild instanceof MethodNameParser.Condition_item_clauseContext) {
                this.parseConditionItem(entityInfo, conditionInfo, conditionEntity, parseTreeChild);
            }
        }
    }

    private void parseConditionItem(EntityInfo entityInfo, ConditionInfo conditionInfo, Boolean conditionEntity, ParseTree parseTree) {
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            String token = parseTreeChild.getText();
            if (parseTreeChild instanceof MethodNameParser.Logic_op_clauseContext) {
                conditionInfo.setConditionEntity(conditionEntity);
                conditionInfo.setOrigin(parseTreeChild.getText());
                conditionInfo.setLinkOp(TOKEN_MAP.get(token));
            } else if (parseTreeChild instanceof MethodNameParser.Field_condition_op_clauseContext) {
                String javaColumnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, token);
                conditionInfo.setConditionEntityJavaColumnName(javaColumnName);
                parseConditionItem(entityInfo, conditionInfo, conditionEntity, parseTreeChild);
            } else if (parseTreeChild instanceof MethodNameParser.Field_clauseContext) {
                String javaColumnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, token);
                ColumnInfo columnInfo = entityInfo.getColumnInfo(javaColumnName);
                if (columnInfo == null) {
                    throw new RuntimeException("方法条件或者实体中条件与数据库库实体无法对应：" + javaColumnName);
                }
                conditionInfo.setDbColumnName(columnInfo.getDbColumnName());
                conditionInfo.setJavaColumnName(columnInfo.getJavaColumnName());
            } else if (parseTreeChild instanceof MethodNameParser.Comparison_op_clauseContext) {
                conditionInfo.setOp(TOKEN_MAP.get(token));
            }
        }
    }
}
