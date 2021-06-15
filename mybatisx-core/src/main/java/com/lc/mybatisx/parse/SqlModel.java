package com.lc.mybatisx.parse;

import com.lc.mybatisx.syntax.MethodNameLexer;
import com.lc.mybatisx.syntax.MethodNameParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class SqlModel {

    private String action;

    private List<String> where;

    private List<String> groupBy;

    private List<String> orderBy;

    private SqlModel() {
    }

    public static SqlModel parse(String methodName) {
        CharStream input = CharStreams.fromString(methodName);
        MethodNameLexer methodNameLexer = new MethodNameLexer(input);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);

        ParseTree qlStatementContext = methodNameParser.ql_statement();
        SqlModel sqlModel = new SqlModel();
        buildSqlModel(sqlModel, qlStatementContext);
        return sqlModel;
    }

    public static void buildSqlModel(SqlModel sqlModel, ParseTree parseTree) {
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            String tokens = parseTreeChild.getText();
            String parentSimpleName = parseTreeChild.getParent().getClass().getSimpleName();
            String simpleName = parseTreeChild.getClass().getSimpleName();

            if (parseTreeChild instanceof TerminalNodeImpl) {
                System.out.println(tokens + "---" + simpleName + "---" + parentSimpleName);
                parseTree(sqlModel, parseTreeChild);
            } else if (parseTreeChild instanceof MethodNameParser.Field_clauseContext) {
                System.out.println(tokens + "---" + simpleName + "---" + parentSimpleName);
                parseTree(sqlModel, parseTreeChild);
            } else {
                buildSqlModel(sqlModel, parseTreeChild);
            }
        }
    }

    private static void parseTree(SqlModel sqlModel, ParseTree parseTree) {
        ParseTree parentParseTree = parseTree.getParent();
        if (parentParseTree instanceof MethodNameParser.Select_clauseContext) {
            sqlModel.setAction(parseTree.getText());
        } else if (parentParseTree instanceof MethodNameParser.Where_clauseContext) {
            sqlModel.setWhere(parseTree.getText());
        } else if (parentParseTree instanceof MethodNameParser.Groupby_clauseContext) {
            sqlModel.setGroupBy(parseTree.getText());
        } else if (parentParseTree instanceof MethodNameParser.Orderby_clauseContext) {
            sqlModel.setOrderBy(parseTree.getText());
        }
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getWhere() {
        return where;
    }

    public void setWhere(String where) {
        if (ObjectUtils.isEmpty(this.where)) {
            this.where = new ArrayList();
        }
        this.where.add(where);
    }

    public void setWhere(List<String> where) {
        this.where = where;
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
