package com.lc.mybatisx.wrapper.where;

import com.lc.mybatisx.wrapper.WhereWrapper;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Keyword {

    /*动作关键字*/
    FIND("find", KeywordType.ACTION, "") {
        @Override
        public WhereWrapper createWhereWrapper(List<String> keywordList, int i) {
            return null;
        }
    },
    SELECT("select", KeywordType.ACTION, ""),
    DELETE("delete", KeywordType.ACTION, ""),
    COUNT("count", KeywordType.ACTION, ""),

    /*无语义关键字*/
    BY("By", KeywordType.NONE, ""),
    SELECTIVE("Selective", KeywordType.NONE, ""),

    /*连接关键字*/
    AND("And", KeywordType.LINK, "and") {
        @Override
        protected void link(WhereWrapper tail, WhereWrapper whereWrapper) {
            tail.linkRule(whereWrapper, LinkOp.AND);
        }
    },
    OR("Or", KeywordType.LINK, "or") {
        @Override
        protected void link(WhereWrapper tail, WhereWrapper whereWrapper) {
            tail.linkRule(whereWrapper, LinkOp.OR);
        }
    },

    /*操作符关键字*/
    EQ("Eq", KeywordType.OP, "="),
    IS("Is", KeywordType.OP, "="),
    BETWEEN("Between", KeywordType.OP, ""),

    /*限定关键字*/
    TOP("Top", KeywordType.LIMIT, ""),
    FIRST("First", KeywordType.LIMIT, ""),

    /*运算型关键字*/
    GROUP_BY("GroupBy", KeywordType.FUNC, ""),
    ORDER_BY("OrderBy", KeywordType.FUNC, "order by"),
    DESC("Desc", KeywordType.FUNC, "desc"),
    ASC("Desc", KeywordType.FUNC, "asc");

    private static Map<String, Keyword> keywordMap = new HashMap<>();
    private String keyword;
    private KeywordType keywordType;
    private String sql;

    static {
        Keyword[] keywords = Keyword.values();
        for (Keyword keyword : keywords) {
            keywordMap.put(keyword.getKeyword(), keyword);
        }
    }

    Keyword(String keyword, KeywordType keywordType, String sql) {
        this.keyword = keyword;
        this.keywordType = keywordType;
        this.sql = sql;
    }

    public String getKeyword() {
        return keyword;
    }

    protected String getJavaColumn(List<String> keywordList, int i) {
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

    public static WhereWrapper buildWhereWrapper(List<String> keywordList) {
        WhereWrapper tail = new WhereWrapper();
        WhereWrapper head = tail;
        for (int i = 0; i < keywordList.size(); i++) {
            Keyword keyword = keywordMap.get(keywordList.get(i));
            if (keyword != null) {
                WhereWrapper whereWrapper = keyword.createWhereWrapper(keywordList, i);
                if (whereWrapper == null) {
                    continue;
                }

                keyword.link(tail, whereWrapper);
                tail = whereWrapper;
            }
        }

        return head.getWhereWrapper();
    }

    public WhereWrapper createWhereWrapper(List<String> keywordList, int i) {
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
        whereWrapper.setJavaColumn(javaColumn);

        return whereWrapper;
    }

    protected void link(WhereWrapper tail, WhereWrapper whereWrapper) {
        tail.linkRule(whereWrapper, null);
    }

}
