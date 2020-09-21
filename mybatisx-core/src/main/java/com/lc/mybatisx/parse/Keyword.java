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

    /*无语义关键字*/
    BY("By", KeywordType.NONE, "", 0, null),
    SELECTIVE("Selective", KeywordType.NONE, "", 0, null),

    /*连接关键字*/
    AND("And", KeywordType.LINK, "and", 0, null),
    OR("Or", KeywordType.LINK, "or", 0, null),

    /*操作符关键字*/
    EQ("Eq", KeywordType.OP, "=", 1, WhereWrapper.class),
    IS("Is", KeywordType.OP, "#{0} = #{1}", 1, WhereWrapper.class),
    LT("Lt", KeywordType.OP, "<![CDATA[ < ]]>", 1, WhereWrapper.class),
    LTEQ("Lteq", KeywordType.OP, "<![CDATA[ <= ]]>", 1, WhereWrapper.class),
    NOT("Not", KeywordType.OP, "<![CDATA[ <> ]]>", 1, WhereWrapper.class),
    BETWEEN("Between", KeywordType.OP, "between #{0} and #{1}", 2, WhereWrapper.class),

    /*限定关键字*/
    TOP("Top", KeywordType.LIMIT, "limit 0, #{0}", 0, LimitWrapper.class),
    FIRST("First", KeywordType.LIMIT, "limit 0, #{0}", 0, LimitWrapper.class),

    /*运算型关键字*/
    GROUP_BY("GroupBy", KeywordType.FUNC, "group by #{0}", 0, FunctionWrapper.class),
    ORDER_BY("OrderBy", KeywordType.FUNC, "order by #{0}", 0, FunctionWrapper.class),
    DESC("Desc", KeywordType.FUNC, "desc", 0, FunctionWrapper.class),
    ASC("Desc", KeywordType.FUNC, "asc", 0, FunctionWrapper.class);

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

    public String getSql(List<String> param) {
        String sql = this.sql;
        for (int i = 0; i < param.size(); i++) {
            sql = sql.replace("#{" + i + "}", param.get(i));
        }
        return sql;
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
