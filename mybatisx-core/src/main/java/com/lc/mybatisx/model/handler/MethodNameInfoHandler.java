package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.model.MethodNameInfo;
import com.lc.mybatisx.model.MethodNameWhereInfo;
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

public class MethodNameInfoHandler {

    private static final Map<String, String> tokenMap = new HashMap<>();

    static {
        tokenMap.put("By", "");
        tokenMap.put("And", "and");
        tokenMap.put("Or", "or");

        tokenMap.put("Lt", "<![CDATA[ < ]]>");
        tokenMap.put("Lteq", "<![CDATA[ <= ]]>");
        tokenMap.put("Gt", "<![CDATA[ > ]]>");
        tokenMap.put("Gteq", "<![CDATA[ >= ]]>");
        tokenMap.put("In", "in");
        tokenMap.put("Is", "=");
        tokenMap.put("Eq", "=");
        tokenMap.put("Not", "<![CDATA[ <> ]]>");
    }

    public MethodNameInfo execute(MethodInfo methodInfo, String methodName) {
        CharStream charStream = CharStreams.fromString(methodName);
        MethodNameLexer methodNameLexer = new MethodNameLexer(charStream);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);
        ParseTree sqlStatementContext = methodNameParser.sql_statement();

        MethodNameInfo methodNameInfo = new MethodNameInfo();
        getTokens(methodInfo, sqlStatementContext);
        return methodNameInfo;
    }

    private void getTokens(MethodInfo methodInfo, ParseTree parseTree) {
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
                List<MethodNameWhereInfo> methodNameWhereInfoList = new ArrayList<>();
                parseWhere(methodNameWhereInfoList, parseTreeChild);
                // methodInfo.setMethodNameWhereInfoList(methodNameWhereInfoList);
            } else if (parseTreeChild instanceof MethodNameParser.EndContext) {
                return;
            } else {
                getTokens(methodInfo, parseTreeChild);
            }
        }
    }

    private void parseWhere(List<MethodNameWhereInfo> methodNameWhereInfoList, ParseTree parseTree) {
        MethodNameWhereInfo methodNameWhereInfo = new MethodNameWhereInfo();
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            String token = parseTreeChild.getText();
            if (parseTreeChild instanceof MethodNameParser.Where_link_op_clauseContext) {
                methodNameWhereInfoList.add(methodNameWhereInfo);
                methodNameWhereInfo.setOrigin(parseTree.getText());
                methodNameWhereInfo.setLinkOp(tokenMap.get(token));
            } else if (parseTreeChild instanceof MethodNameParser.Field_clauseContext) {
                methodNameWhereInfo.setJavaColumnName(token);
            } else if (parseTreeChild instanceof MethodNameParser.Condition_op_clauseContext) {
                methodNameWhereInfo.setOp(tokenMap.get(token));
            } else {
                parseWhere(methodNameWhereInfoList, parseTreeChild);
            }
        }
    }

}
