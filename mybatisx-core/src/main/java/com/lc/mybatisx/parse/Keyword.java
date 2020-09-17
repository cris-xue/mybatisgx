package com.lc.mybatisx.parse;

import com.lc.mybatisx.wrapper.*;
import org.apache.ibatis.annotations.Param;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Keyword {

    /*动作关键字*/
    FIND("find", KeywordType.ACTION, "select #{0} from", QuerySqlWrapper.class) {},
    SELECT("select", KeywordType.ACTION, "", QuerySqlWrapper.class),
    COUNT("count", KeywordType.ACTION, "", QuerySqlWrapper.class),
    DELETE("delete", KeywordType.ACTION, "", DeleteSqlWrapper.class),

    /*无语义关键字*/
    BY("By", KeywordType.NONE, "", null),
    SELECTIVE("Selective", KeywordType.NONE, "", null),

    /*连接关键字*/
    AND("And", KeywordType.LINK, "and", null) {},
    OR("Or", KeywordType.LINK, "or", null) {},

    /*操作符关键字*/
    EQ("Eq", KeywordType.OP, "=", WhereWrapper.class),
    LT("Lt", KeywordType.OP, "=", WhereWrapper.class),
    LTEQ("Lteq", KeywordType.OP, "=", WhereWrapper.class),
    NOT("Not", KeywordType.OP, "=", WhereWrapper.class),
    IS("Is", KeywordType.OP, "#{0} = #{1}", WhereWrapper.class),
    BETWEEN("Between", KeywordType.OP, "between #{0} and #{1}", WhereWrapper.class) {
        @Override
        public Object[] getSql(int i, Parameter[] parameters) {
            List<String> javaColumnList = new ArrayList<>();

            String sql = this.getSql();
            int length = i + 2;
            for (int p = 0; i < length; i++, p++) {
                Parameter parameter = parameters[i];
                Param param = parameter.getAnnotation(Param.class);
                if (param != null) {
                    sql = sql.replace("#{#{" + p + "}}", param.value());
                    javaColumnList.add(param.value());
                }
            }

            Object[] result = new Object[]{length, sql, javaColumnList};
            return result;
        }
    },

    /*限定关键字*/
    TOP("Top", KeywordType.LIMIT, "limit 0, #{0}", LimitWrapper.class),
    FIRST("First", KeywordType.LIMIT, "limit 0, #{0}", LimitWrapper.class),

    /*运算型关键字*/
    GROUP_BY("GroupBy", KeywordType.FUNC, "group by #{0}", FunctionWrapper.class),
    ORDER_BY("OrderBy", KeywordType.FUNC, "order by #{0}", FunctionWrapper.class),
    DESC("Desc", KeywordType.FUNC, "desc", FunctionWrapper.class),
    ASC("Desc", KeywordType.FUNC, "asc", FunctionWrapper.class);

    private static Map<String, Keyword> keywordMap = new HashMap<>();
    private String keyword;
    private KeywordType keywordType;
    private String sql;
    private Class<?> clazz;
    private static int index = 0;

    static {
        Keyword[] keywords = Keyword.values();
        for (Keyword keyword : keywords) {
            keywordMap.put(keyword.getKeyword(), keyword);
        }
    }

    Keyword(String keyword, KeywordType keywordType, String sql, Class<?> clazz) {
        this.keyword = keyword;
        this.keywordType = keywordType;
        this.sql = sql;
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

    public String getSql(List<String> param) {
        String sql = this.sql;
        for (int i = 0; i < param.size(); i++) {
            sql = sql.replace("#{" + i + "}", param.get(i));
        }
        return sql;
    }

    public Object[] getSql(int i, Parameter[] parameters) {
        return new String[3];
    }

}
