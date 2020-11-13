package com.lc.mybatisx.parse;

import com.lc.mybatisx.wrapper.*;

import java.util.List;

public enum Keyword {

    /*动作关键字*/
    FIND("find", KeywordType.ACTION, "select #{ %s } from", 0, QuerySqlWrapper.class),
    SELECT("select", KeywordType.ACTION, "", 0, QuerySqlWrapper.class),
    COUNT("count", KeywordType.ACTION, "", 0, QuerySqlWrapper.class),
    DELETE("delete", KeywordType.ACTION, "", 0, DeleteSqlWrapper.class),
    UPDATE("update", KeywordType.ACTION, "", 0, DeleteSqlWrapper.class),
    INSERT("insert", KeywordType.ACTION, "", 0, InsertSqlWrapper.class),

    /*无语义关键字*/
    SELECTIVE("Selective", KeywordType.NONE, "", 0, null),

    /*连接关键字*/
    BY("By", KeywordType.LINK, "", 0, null),
    AND("And", KeywordType.LINK, "and", 0, null),
    OR("Or", KeywordType.LINK, "or", 0, null),

    /*查询条件关键字*/
    EQ("Eq", KeywordType.WHERE, " = #{ %s }", 1, WhereWrapper.class),
    IS("Is", KeywordType.WHERE, " = #{ %s }", 1, WhereWrapper.class),
    LT("Lt", KeywordType.WHERE, " <![CDATA[ < ]]> #{ %s }", 1, WhereWrapper.class),
    LESS_THAN("LessThan", KeywordType.WHERE, " <![CDATA[ < ]]> #{ %s }", 1, WhereWrapper.class),
    LTEQ("Lteq", KeywordType.WHERE, " <![CDATA[ <= ]]> #{ %s }", 1, WhereWrapper.class),
    NOT("Not", KeywordType.WHERE, " <![CDATA[ <> ]]> #{ %s }", 1, WhereWrapper.class),

    LIKE("Like", KeywordType.WHERE, " like #{ %s }", 1, WhereWrapper.class),
    NOT_LIKE("NotLike", KeywordType.WHERE, " not like #{ %s }", 1, WhereWrapper.class),
    STARTING_WITH("StartingWith", KeywordType.WHERE, " like #{ %s }%%", 1, WhereWrapper.class),
    ENDING_WITH("EndingWith", KeywordType.WHERE, " like %%#{ %s }", 1, WhereWrapper.class),
    CONTAINING("Containing", KeywordType.WHERE, " like %%#{ %s }%%", 1, WhereWrapper.class),

    BETWEEN("Between", KeywordType.WHERE, " between #{ %s } and #{ %s }", 2, WhereWrapper.class),

    /*top关键字*/
    TOP("Top", KeywordType.LIMIT, " limit 0,  %s ", 0, LimitWrapper.class),
    FIRST("First", KeywordType.LIMIT, " limit 0, #{ %s }", 0, LimitWrapper.class),

    /*排序关键字*/
    ORDER_BY("OrderBy", KeywordType.ORDER, "order by #{ %s }", 0, OrderWrapper.class),
    DESC("Desc", KeywordType.ORDER, "desc", 0, OrderWrapper.class),
    ASC("Asc", KeywordType.ORDER, "asc", 0, OrderWrapper.class),

    /*运算型关键字*/
    GROUP_BY("GroupBy", KeywordType.FUNC, "group by #{ %s }", 0, FunctionWrapper.class);

    private String keyword;
    private KeywordType keywordType;
    private String sql;
    private int index = 0;
    private Class<?> clazz;

    Keyword(String keyword, KeywordType keywordType, String sql, int index, Class<?> clazz) {
        this.keyword = keyword;
        this.keywordType = keywordType;
        this.sql = sql;
        this.index = index;
        this.clazz = clazz;
    }

    public String getKeyword() {
        return keyword;
    }

    public KeywordType getKeywordType() {
        return keywordType;
    }

    public String getSql() {
        return sql;
    }

    public int getIndex() {
        return index;
    }

    public String getTest(List<String> javaColumnList) {
        String test = "";
        for (int i = 0; i < javaColumnList.size(); i++) {
            if (i != 0) {
                test += " and ";
            }
            test += javaColumnList.get(i) + " != null";
        }
        return test;
    }

    public String getTest(WhereWrapper whereWrapper) {
        List<String> javaColumnList = whereWrapper.getJavaColumn();
        return getTest(javaColumnList);
    }

    public String getSql(String dbColumn, List<String> javaColumnList) {
        String sql = String.format(this.sql, javaColumnList.toArray());
        return dbColumn + sql;
    }

    public String getSql(WhereWrapper whereWrapper) {
        return getSql(whereWrapper.getDbColumn(), whereWrapper.getJavaColumn());
    }

}
