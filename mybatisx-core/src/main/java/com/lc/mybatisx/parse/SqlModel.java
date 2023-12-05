package com.lc.mybatisx.parse;

import com.lc.mybatisx.model.MethodNode;
import com.lc.mybatisx.syntax.MethodNameLexer;
import com.lc.mybatisx.syntax.MethodNameParser;
import com.lc.mybatisx.wrapper.WhereWrapper;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqlModel {

    private String action;

    private SqlField sqlField;

    private List<SqlWhere> sqlWheres;

    private List<String> groupBy;

    private List<String> orderBy;

    private SqlModel() {
    }

    public static SqlModel parse(MethodNode methodNode, String methodName) {
        CharStream input = CharStreams.fromString(methodName);
        MethodNameLexer methodNameLexer = new MethodNameLexer(input);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);

        ParseTree qlStatementContext = methodNameParser.sql_statement();
        SqlModel sqlModel = new SqlModel();
        buildSqlModel(sqlModel, qlStatementContext);
        return sqlModel;
    }

    public static void buildSqlModel(SqlModel sqlModel, ParseTree parseTree) {
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);

            buildInsert(parseTreeChild);
            buildDelete(parseTreeChild);
            buildUpdate(parseTreeChild);
            buildSelectStatement(parseTreeChild);
        }
    }

    private static void buildInsert(ParseTree parseTree) {
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

    private static void buildDelete(ParseTree parseTree) {
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

    private static void buildUpdate(ParseTree parseTree) {
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

    private static void buildSelectStatement(ParseTree parseTree) {
        ParseTree parseTreeParent = parseTree.getParent();
        if (!(parseTree instanceof MethodNameParser.Select_statementContext)
                || !(parseTreeParent instanceof MethodNameParser.Select_statementContext)) {
            return;
        }

        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            buildSelectStatement(parseTree.getChild(i));
        }

        if (!(parseTree.getParent() instanceof MethodNameParser.Select_statementContext)) {
            return;
        }
        // 构建动作

        // 构建条件
        List<WhereWrapper> whereWrapperList = new ArrayList<>();
        buildWhereClause(whereWrapperList, parseTree);
    }

    private static void buildWhereClause(List<WhereWrapper> whereWrapperList, ParseTree parseTree) {
        if (!(parseTree instanceof MethodNameParser.Where_clauseContext)) {
            return;
        }

        buildWhereItem(whereWrapperList, parseTree);
    }

    private static void buildWhereItem(List<WhereWrapper> whereWrapperList, ParseTree parseTree) {
        if (!(parseTree instanceof MethodNameParser.Where_itemContext)) {
            return;
        }

        int count = parseTree.getChildCount();
        for (int i = 0; i < count; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            String tokens = parseTreeChild.getText();

            if (parseTreeChild instanceof MethodNameParser.Where_itemContext) {
                buildWhereItem(whereWrapperList, parseTreeChild);
            }

            WhereWrapper whereWrapper = new WhereWrapper();
            if (parseTreeChild instanceof MethodNameParser.Where_link_op_clauseContext) {
                whereWrapper.setLinkOp(tokens);
            }
            String field = buildField(parseTree);
            whereWrapper.setJavaColumn(Arrays.asList(field));

            whereWrapperList.add(whereWrapper);
        }
    }

    private static String buildField(ParseTree parseTree) {
        if (!(parseTree instanceof MethodNameParser.Field_clauseContext)) {
            return null;
        }
        return parseTree.getText();
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<SqlWhere> getSqlWheres() {
        return sqlWheres;
    }

    public void setSqlWhere(SqlWhere sqlWhere) {
        if (ObjectUtils.isEmpty(this.sqlWheres)) {
            this.sqlWheres = new ArrayList<>();
        }
        this.sqlWheres.add(sqlWhere);
    }

    public void setSqlWheres(List<SqlWhere> sqlWheres) {
        this.sqlWheres = sqlWheres;
    }

    public List<String> getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        if (ObjectUtils.isEmpty(this.groupBy)) {
            this.groupBy = new ArrayList();
        }
        this.groupBy.add(groupBy);
    }

    public void setGroupBy(List<String> groupBy) {
        this.groupBy = groupBy;
    }

    public List<String> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        if (ObjectUtils.isEmpty(this.orderBy)) {
            this.orderBy = new ArrayList();
        }
        this.orderBy.add(orderBy);
    }

    public void setOrderBy(List<String> orderBy) {
        this.orderBy = orderBy;
    }
}
