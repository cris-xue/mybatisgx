package com.lc.mybatisx.parse;

import com.lc.mybatisx.syntax.MethodNameParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class SqlModel {

    private static SqlModel sqlModel = new SqlModel();

    private ParseTree action;

    private List<ParseTree> where;

    private List<ParseTree> groupBy;

    private List<ParseTree> orderBy;

    private SqlModel() {
    }

    public static SqlModel build() {
        return sqlModel;
    }

    public static void buildSqlModel(ParseTree parseTree) {
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            String tokens = parseTreeChild.getText();
            String parentSimpleName = parseTreeChild.getParent().getClass().getSimpleName();
            String simpleName = parseTreeChild.getClass().getSimpleName();

            if (parseTreeChild instanceof TerminalNodeImpl) {
                System.out.println(tokens + "---" + simpleName + "---" + parentSimpleName);
                parseTree(parseTreeChild);
            } else if (parseTreeChild instanceof MethodNameParser.Field_clauseContext) {
                System.out.println(tokens + "---" + simpleName + "---" + parentSimpleName);
                parseTree(parseTreeChild);
            } else {
                buildSqlModel(parseTreeChild);
            }
        }
    }

    private static void parseTree(ParseTree parseTree) {
        ParseTree parentParseTree = parseTree.getParent();
        if (parentParseTree instanceof MethodNameParser.Select_clauseContext) {
            sqlModel.setAction(parseTree);
        } else if (parentParseTree instanceof MethodNameParser.Where_clauseContext) {
            sqlModel.setWhere(parseTree);
        } else if (parentParseTree instanceof MethodNameParser.Groupby_clauseContext) {
            sqlModel.setGroupBy(parseTree);
        } else if (parentParseTree instanceof MethodNameParser.Orderby_clauseContext) {
            sqlModel.setOrderBy(parseTree);
        }
    }

    public ParseTree getAction() {
        return action;
    }

    public void setAction(ParseTree action) {
        this.action = action;
    }

    public List<ParseTree> getWhere() {
        return where;
    }

    public void setWhere(ParseTree where) {
        if (ObjectUtils.isEmpty(this.where)) {
            this.where = new ArrayList();
        }
        this.where.add(where);
    }

    public void setWhere(List<ParseTree> where) {
        this.where = where;
    }

    public List<ParseTree> getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(ParseTree groupBy) {
        if (ObjectUtils.isEmpty(this.groupBy)) {
            this.groupBy = new ArrayList();
        }
        this.groupBy.add(groupBy);
    }

    public void setGroupBy(List<ParseTree> groupBy) {
        this.groupBy = groupBy;
    }

    public List<ParseTree> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(ParseTree orderBy) {
        if (ObjectUtils.isEmpty(this.orderBy)) {
            this.orderBy = new ArrayList();
        }
        this.orderBy.add(orderBy);
    }

    public void setOrderBy(List<ParseTree> orderBy) {
        this.orderBy = orderBy;
    }
}
