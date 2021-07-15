package com.lc.mybatisx.handler.handler;

import com.lc.mybatisx.model.MethodNode;
import com.lc.mybatisx.model.MethodParamNode;
import com.lc.mybatisx.model.wrapper.QuerySqlWrapper;
import com.lc.mybatisx.model.wrapper.SqlWrapper;
import com.lc.mybatisx.model.wrapper.WhereWrapper;
import com.lc.mybatisx.syntax.MethodNameLexer;
import com.lc.mybatisx.syntax.MethodNameParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/14 15:48
 */
public class SqlMapperHandler {

    private static SqlMapperHandler instance = new SqlMapperHandler();
    private MethodNode methodNode;

    public static SqlWrapper build(MethodNode methodNode, String methodName) {
        ParseTree parseTree = instance.getParseTree(methodName);
        return instance.buildSqlWrapper(methodNode, parseTree);
    }

    private ParseTree getParseTree(String methodName) {
        CharStream input = CharStreams.fromString(methodName);
        MethodNameLexer methodNameLexer = new MethodNameLexer(input);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);

        ParseTree sqlStatementContext = methodNameParser.sql_statement();
        return sqlStatementContext;
    }

    private SqlWrapper buildSqlWrapper(MethodNode methodNode, ParseTree parseTree) {
        this.methodNode = methodNode;
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);

            buildInsert(parseTreeChild);
            buildDelete(parseTreeChild);
            buildUpdate(parseTreeChild);

            QuerySqlWrapper querySqlWrapper = parseSelectStatement(parseTreeChild);
            if (querySqlWrapper != null) {
                return querySqlWrapper;
            }
        }

        return null;
    }

    private void buildInsert(ParseTree parseTree) {
        if (parseTree instanceof MethodNameParser.Insert_statementContext) {
            int childCount = parseTree.getChildCount();
            for (int i = 0; i < childCount; i++) {
                buildInsert(parseTree.getChild(i));
            }
        }
        if (!(parseTree instanceof MethodNameParser.Insert_clauseContext)) {
            return;
        }
    }

    private void buildDelete(ParseTree parseTree) {
        if (parseTree instanceof MethodNameParser.Delete_statementContext) {
            int childCount = parseTree.getChildCount();
            for (int i = 0; i < childCount; i++) {
                buildDelete(parseTree.getChild(i));
            }
        }
        if (!(parseTree instanceof MethodNameParser.Delete_clauseContext)) {
            return;
        }
    }

    private void buildUpdate(ParseTree parseTree) {
        if (parseTree instanceof MethodNameParser.Update_statementContext) {
            int childCount = parseTree.getChildCount();
            for (int i = 0; i < childCount; i++) {
                buildUpdate(parseTree.getChild(i));
            }
        }
        if (!(parseTree instanceof MethodNameParser.Update_clauseContext)) {
            return;
        }
    }

    private QuerySqlWrapper parseSelectStatement(ParseTree parseTree) {
        boolean isSelectStatement = parseTree instanceof MethodNameParser.Select_statementContext;
        if (!isSelectStatement) {
            return null;
        }

        QuerySqlWrapper querySqlWrapper = new QuerySqlWrapper();

        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            parseSelectClause(parseTreeChild);

            WhereWrapper whereWrapper = parseWhereClause(parseTreeChild);
            if (whereWrapper != null) {
                querySqlWrapper.setWhereWrapper(whereWrapper);
            }
        }

        return querySqlWrapper;
    }

    private void parseSelectClause(ParseTree parseTree) {
        boolean isSelectClause = parseTree instanceof MethodNameParser.Select_clauseContext;
        if (!isSelectClause) {
            return;
        }

        // 构建动作
        buildAction(parseTree);
    }

    private void buildAction(ParseTree parseTree) {
        if (parseTree instanceof MethodNameParser.Insert_clauseContext) {

        } else if (parseTree instanceof MethodNameParser.Select_clauseContext) {

        }
    }

    private WhereWrapper parseWhereClause(ParseTree parseTree) {
        boolean isWhereClause = parseTree instanceof MethodNameParser.Where_clauseContext;
        if (!isWhereClause) {
            return null;
        }

        List<WhereWrapper> whereWrapperList = new ArrayList<>();
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            parseWhereItem(whereWrapperList, parseTreeChild);
        }

        // 方法名条件和方法参数做匹配
        matchField(whereWrapperList);

        int whereSize = whereWrapperList.size();
        WhereWrapper ww1 = null;
        WhereWrapper ww2 = null;
        for (int i = 0; i < whereSize; i++) {
            WhereWrapper ww = whereWrapperList.get(i);
            if (ww1 == null) {
                ww1 = ww;
                ww2 = ww;
            } else {
                ww2.setWhereWrapper(ww);
                ww2 = ww;
            }
        }
        return ww1;
    }

    private void parseWhereItem(List<WhereWrapper> whereWrapperList, ParseTree parseTree) {
        boolean isWhereItem = parseTree instanceof MethodNameParser.Where_itemContext;
        if (!isWhereItem) {
            return;
        }

        WhereWrapper whereWrapper = new WhereWrapper();
        int count = parseTree.getChildCount();
        for (int i = 0; i < count; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            String tokens = parseTreeChild.getText();

            if (parseTreeChild instanceof MethodNameParser.Where_link_op_clauseContext) {
                whereWrapper.setLinkOp(tokens);
            } else if (parseTreeChild instanceof MethodNameParser.Where_op_clauseContext) {
                whereWrapper.setOp(tokens);
            }
            String field = parseFieldClause(parseTreeChild);
            if (StringUtils.isNoneBlank(field)) {
                whereWrapper.setJavaColumn(Arrays.asList(field));
            }
        }
        whereWrapperList.add(whereWrapper);
    }

    private void matchField(List<WhereWrapper> whereWrapperList) {
        List<MethodParamNode> methodParamNodeList = methodNode.getMethodParamNodeList();

        int paramCount = methodParamNodeList.size();
        int whereCount = whereWrapperList.size();
        if (paramCount == whereCount) {

        }

        for (WhereWrapper whereWrapper : whereWrapperList) {

        }

        for (MethodParamNode methodParamNode : methodParamNodeList) {
            String methodParamName = methodParamNode.getName();
            if (field.equalsIgnoreCase(methodParamName)) {

            }
        }
        throw new RuntimeException("方法名和参数条件不匹配");
    }

    private String parseFieldClause(ParseTree parseTree) {
        boolean isFieldClause = parseTree instanceof MethodNameParser.Field_clauseContext;
        return isFieldClause ? parseTree.getText() : null;
    }

}
