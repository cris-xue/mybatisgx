package com.lc.mybatisx.wrapper.where;

import com.lc.mybatisx.wrapper.*;

import java.util.HashMap;
import java.util.Map;

public enum Keyword {

    /*动作关键字*/
    FIND("find", KeywordType.ACTION, "", QuerySqlWrapper.class) {
        /*@Override
        public WhereWrapper createWhereWrapper(Method method, List<String> keywordList, int i) {
            return null;
        }*/
    },
    SELECT("select", KeywordType.ACTION, "", QuerySqlWrapper.class),
    COUNT("count", KeywordType.ACTION, "", QuerySqlWrapper.class),
    DELETE("delete", KeywordType.ACTION, "", DeleteSqlWrapper.class),


    /*无语义关键字*/
    BY("By", KeywordType.NONE, "", null),
    SELECTIVE("Selective", KeywordType.NONE, "", null),

    /*连接关键字*/
    AND("And", KeywordType.LINK, "and", null) {
        /*@Override
        protected void link(WhereWrapper tail, WhereWrapper whereWrapper) {
            tail.linkRule(whereWrapper, LinkOp.AND);
        }*/
    },
    OR("Or", KeywordType.LINK, "or", null) {
        /*@Override
        protected void link(WhereWrapper tail, WhereWrapper whereWrapper) {
            tail.linkRule(whereWrapper, LinkOp.OR);
        }*/
    },

    /*操作符关键字*/
    EQ("Eq", KeywordType.OP, "=", WhereWrapper.class),
    LT("Lt", KeywordType.OP, "=", WhereWrapper.class),
    LTEQ("Lteq", KeywordType.OP, "=", WhereWrapper.class),
    NOT("Not", KeywordType.OP, "=", WhereWrapper.class),
    IS("Is", KeywordType.OP, "=", WhereWrapper.class),
    BETWEEN("Between", KeywordType.OP, "between #{0} and #{1}", WhereWrapper.class),

    /*限定关键字*/
    TOP("Top", KeywordType.LIMIT, "limit", LimitWrapper.class),
    FIRST("First", KeywordType.LIMIT, "limit", LimitWrapper.class),

    /*运算型关键字*/
    GROUP_BY("GroupBy", KeywordType.FUNC, "group by", FunctionWrapper.class),
    ORDER_BY("OrderBy", KeywordType.FUNC, "order by", FunctionWrapper.class),
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

    /*protected String getJavaColumn(List<String> keywordList, int i) {
        if (i + 1 >= keywordList.size()) {
            return "";
        }

        String javaColumn = keywordList.get(++i);
        Keyword keyword = keywordMap.get(javaColumn);
        if (keyword == null) {
            String javaColumnTemp = this.getJavaColumn(keywordList, i);
            return javaColumn + javaColumnTemp;
        }
        return "";
    }

    protected Operation getOp(List<String> keywordList, int i) {
        if (i + 1 >= keywordList.size()) {
            return Operation.EQ;
        }

        String javaColumn = keywordList.get(++i);
        Keyword keyword = keywordMap.get(javaColumn);
        if (keyword == null) {
            return this.getOp(keywordList, i);
        }

        if (keyword != null && keyword.keywordType == KeywordType.LINK) {
            return Operation.EQ;
        } else if (keyword != null && keyword.keywordType == KeywordType.OP) {
            return Operation.valueOf(keyword.name());
        }
        return null;
    }

    public static WhereWrapper buildWhereWrapper(Method method, List<String> keywordList) {
        WhereWrapper tail = new WhereWrapper();
        WhereWrapper head = tail;
        for (int i = 0; i < keywordList.size(); i++) {
            Keyword keyword = keywordMap.get(keywordList.get(i));
            if (keyword != null) {
                WhereWrapper whereWrapper = keyword.createWhereWrapper(method, keywordList, i);
                if (whereWrapper == null) {
                    continue;
                }

                keyword.link(tail, whereWrapper);
                tail = whereWrapper;
            }
        }

        return head.getWhereWrapper();
    }

    public static List<String> getKeywordList(List<String> keywordList) {
        List<String> aaaList = new ArrayList<>();
        for (int i = 0; i < keywordList.size(); i++) {
            String kw = keywordList.get(i);
            Keyword keyword = keywordMap.get(kw);
            if (keyword != null) {
                aaaList.add(kw);
            } else {
                Map<String, String> map = new HashMap<>();
                String javaColumn = getJavaColumn111(keywordList, i);
                i = index;
                index = 0;
                aaaList.add(javaColumn);
            }
        }

        return aaaList;
    }

    protected static String getJavaColumn111(List<String> keywordList, int i) {
        String javaColumn = keywordList.get(i);
        if (i + 1 >= keywordList.size()) {
            index = i;
            return javaColumn;
        }

        Keyword keyword = keywordMap.get(javaColumn);
        if (keyword == null) {
            String javaColumnTemp = getJavaColumn111(keywordList, ++i);
            return javaColumn + javaColumnTemp;
        }
        index = --i;
        return "";
    }

    public WhereWrapper createWhereWrapper(Method method, List<String> keywordList, int i) {
        // 获取java字段
        String javaColumn = this.getJavaColumn(keywordList, i);
        if (!StringUtils.hasLength(javaColumn)) {
            return null;
        }

        // 条件操作符
        Operation operation = this.getOp(keywordList, i);
        if (operation == null) {
            return null;
        }

        WhereWrapper whereWrapper = new WhereWrapper();
        whereWrapper.setDbColumn(javaColumn);
        whereWrapper.setOperation(operation);
        whereWrapper.setJavaColumn(Arrays.asList(javaColumn));

        return whereWrapper;
    }

    protected void link(WhereWrapper tail, WhereWrapper whereWrapper) {
        tail.linkRule(whereWrapper, null);
    }*/

}
