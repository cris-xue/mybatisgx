package com.lc.mybatisx.parse;

import com.lc.mybatisx.model.wrapper.WhereWrapper;
import com.lc.mybatisx.syntax.MethodNameLexer;
import com.lc.mybatisx.syntax.MethodNameParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
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

    public static SqlModel parse(String methodName) {
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

            if (parseTreeChild instanceof MethodNameParser.Select_clauseContext) {
            } else if (parseTreeChild instanceof MethodNameParser.Where_clauseContext) {
                List<WhereWrapper> whereWrapperList = new ArrayList<>();
                buildWhere(whereWrapperList, null, parseTreeChild);
                System.out.println("aa");
            } else if (parseTreeChild instanceof TerminalNodeImpl) {
                parseTree(sqlModel, parseTreeChild);
            } else if (parseTreeChild instanceof MethodNameParser.Field_clauseContext) {
                parseTree(sqlModel, parseTreeChild);
            } else {
                buildSqlModel(sqlModel, parseTreeChild);
            }
        }
    }

    private static WhereWrapper buildWhere(List<WhereWrapper> whereWrapperList, WhereWrapper whereWrapper, ParseTree parseTree) {
        int count = parseTree.getChildCount();

        for (int i = 0; i < count; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);

            String tokens = parseTreeChild.getText();
            String parentSimpleName = parseTreeChild.getParent().getClass().getSimpleName();
            String simpleName = parseTreeChild.getClass().getSimpleName();

            if (parseTreeChild instanceof MethodNameParser.Where_itemContext) {
                WhereWrapper ww = buildWhere(null, new WhereWrapper(), parseTreeChild);
                whereWrapperList.add(ww);
            }

            if (parseTreeChild instanceof MethodNameParser.Where_link_op_clauseContext) {
                System.out.println(tokens + "---" + simpleName + "---" + parentSimpleName);
                whereWrapper.setLinkOp(tokens);
            } else if (parseTreeChild instanceof MethodNameParser.Field_clauseContext) {
                System.out.println(tokens + "---" + simpleName + "---" + parentSimpleName);
                whereWrapper.setJavaColumn(Arrays.asList(tokens));
            } else if (parseTreeChild instanceof MethodNameParser.Where_op_clauseContext) {
                System.out.println(tokens + "---" + simpleName + "---" + parentSimpleName);
                whereWrapper.setOp(tokens);
            }
        }

        return whereWrapper;
    }

    private static void parseTree(SqlModel sqlModel, ParseTree parseTree) {
        ParseTree parentParseTree = parseTree.getParent();
        if (parentParseTree instanceof MethodNameParser.Select_clauseContext) {
            sqlModel.setAction(parseTree.getText());
        } else if (parentParseTree instanceof MethodNameParser.Where_clauseContext) {
            // sqlModel.setSqlWhere(parseTree.getText());
            System.out.println("adfadsf");
        }/* else if (parentParseTree instanceof MethodNameParser.Groupby_clauseContext) {
            sqlModel.setGroupBy(parseTree.getText());
        } else if (parentParseTree instanceof MethodNameParser.Orderby_clauseContext) {
            sqlModel.setOrderBy(parseTree.getText());
        }*/
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
