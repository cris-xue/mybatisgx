package com.lc.mybatisx.parse;

import com.lc.mybatisx.wrapper.*;
import org.apache.ibatis.annotations.Param;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public enum Keyword {

    /*动作关键字*/
    FIND("find", KeywordType.ACTION, "select #{0} from", 0, QuerySqlWrapper.class),
    SELECT("select", KeywordType.ACTION, "", 0, QuerySqlWrapper.class),
    COUNT("count", KeywordType.ACTION, "", 0, QuerySqlWrapper.class),
    DELETE("delete", KeywordType.ACTION, "", 0, DeleteSqlWrapper.class),
    UPDATE("update", KeywordType.ACTION, "", 0, DeleteSqlWrapper.class),
    INSERT("insert", KeywordType.ACTION, "", 0, InsertSqlWrapper.class),

    /*无语义关键字*/
    SELECTIVE("Selective", KeywordType.NONE, "", 0, null),

    /*连接关键字*/
    AND("And", KeywordType.LINK, "and", 0, null),
    OR("Or", KeywordType.LINK, "or", 0, null),

    /*查询条件关键字*/
    BY("By", KeywordType.WHERE, "", 0, WhereWrapper.class),
    EQ("Eq", KeywordType.WHERE, " = #{0}", 1, WhereWrapper.class),
    IS("Is", KeywordType.WHERE, " = #{0}", 1, WhereWrapper.class),
    LT("Lt", KeywordType.WHERE, " <![CDATA[ < ]]> #{0}", 1, WhereWrapper.class),
    LESS_THAN("LessThan", KeywordType.WHERE, " <![CDATA[ < ]]> #{0}", 1, WhereWrapper.class),
    LTEQ("Lteq", KeywordType.WHERE, " <![CDATA[ <= ]]> #{0}", 1, WhereWrapper.class),
    NOT("Not", KeywordType.WHERE, " <![CDATA[ <> ]]> #{0}", 1, WhereWrapper.class),
    BETWEEN("Between", KeywordType.WHERE, " between #{0} and #{1}", 2, WhereWrapper.class),

    /*top关键字*/
    TOP("Top", KeywordType.LIMIT, " limit 0, #{0}", 0, LimitWrapper.class),
    FIRST("First", KeywordType.LIMIT, " limit 0, #{0}", 0, LimitWrapper.class),

    /*排序关键字*/
    ORDER_BY("OrderBy", KeywordType.ORDER, "order by #{0}", 0, OrderWrapper.class),
    DESC("Desc", KeywordType.ORDER, "desc", 0, OrderWrapper.class),
    ASC("Asc", KeywordType.ORDER, "asc", 0, OrderWrapper.class),

    /*运算型关键字*/
    GROUP_BY("GroupBy", KeywordType.FUNC, "group by #{0}", 0, FunctionWrapper.class);

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
        String sql = this.sql;
        for (int i = 0; i < javaColumnList.size(); i++) {
            sql = sql.replace("#{" + i + "}", "#{ " + javaColumnList.get(i) + " }");
        }
        return dbColumn + sql;
    }

    public String getSql(WhereWrapper whereWrapper) {
        return getSql(whereWrapper.getDbColumn(), whereWrapper.getJavaColumn());
    }

    public Object[] getSql(int i, Parameter[] parameters) {
        List<String> javaColumnList = new ArrayList<>();
        Parameter parameter = parameters[i];
        Param param = parameter.getAnnotation(Param.class);
        if (param != null) {
            javaColumnList.add(param.value());
        }

        return new Object[]{i + 1, "sql", javaColumnList};
    }

}
