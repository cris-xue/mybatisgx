package com.lc.mybatisx.model.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.ConditionInfo;
import com.lc.mybatisx.model.MapperInfo;
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

public class ConditionInfoHandler {

    private static final Map<String, String> TOKEN_MAP = new HashMap<>();

    static {
        TOKEN_MAP.put("By", "");
        TOKEN_MAP.put("And", "and");
        TOKEN_MAP.put("Or", "or");

        TOKEN_MAP.put("Lt", "<![CDATA[ < ]]>");
        TOKEN_MAP.put("Lteq", "<![CDATA[ <= ]]>");
        TOKEN_MAP.put("Gt", "<![CDATA[ > ]]>");
        TOKEN_MAP.put("Gteq", "<![CDATA[ >= ]]>");
        TOKEN_MAP.put("In", "in");
        TOKEN_MAP.put("Is", "=");
        TOKEN_MAP.put("Eq", "=");
        TOKEN_MAP.put("Not", "<![CDATA[ <> ]]>");
    }

    public void execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        CharStream charStream = CharStreams.fromString(methodInfo.getMethodName());
        MethodNameLexer methodNameLexer = new MethodNameLexer(charStream);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);
        ParseTree sqlStatementContext = methodNameParser.sql_statement();

        getTokens(mapperInfo, methodInfo, sqlStatementContext);
    }

    private void getTokens(MapperInfo mapperInfo, MethodInfo methodInfo, ParseTree parseTree) {
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
                List<ConditionInfo> conditionInfoList = new ArrayList<>();
                parseCondition(conditionInfoList, parseTreeChild, mapperInfo);
                methodInfo.setConditionInfoList(conditionInfoList);
            } else if (parseTreeChild instanceof MethodNameParser.EndContext) {
                return;
            } else {
                getTokens(mapperInfo, methodInfo, parseTreeChild);
            }
        }
    }

    private void parseCondition(List<ConditionInfo> conditionInfoList, ParseTree parseTree, MapperInfo mapperInfo) {
        ConditionInfo conditionInfo = new ConditionInfo();
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            String token = parseTreeChild.getText();
            if (parseTreeChild instanceof MethodNameParser.Where_link_op_clauseContext) {
                conditionInfoList.add(conditionInfo);
                conditionInfo.setIndex(conditionInfoList.size() - 1);
                conditionInfo.setOrigin(parseTree.getText());
                conditionInfo.setLinkOp(TOKEN_MAP.get(token));
            } else if (parseTreeChild instanceof MethodNameParser.Field_clauseContext) {
                String javaColumnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, token);
                Map<String, ColumnInfo> columnInfoMap = mapperInfo.getResultMapInfo().getColumnInfoMap();
                conditionInfo.setDbColumnName(columnInfoMap.get(javaColumnName).getDbColumnName());
                conditionInfo.setJavaColumnName(javaColumnName);
            } else if (parseTreeChild instanceof MethodNameParser.Condition_op_clauseContext) {
                conditionInfo.setOp(TOKEN_MAP.get(token));
            } else {
                parseCondition(conditionInfoList, parseTreeChild, mapperInfo);
            }
        }
    }

}
