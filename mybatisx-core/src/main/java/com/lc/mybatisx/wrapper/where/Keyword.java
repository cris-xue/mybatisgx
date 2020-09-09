package com.lc.mybatisx.wrapper.where;

import com.lc.mybatisx.wrapper.WhereWrapper;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Keyword {

    /*动作关键字*/
    FIND(false, "find", KeywordType.ACTION, Arrays.asList()) {
        @Override
        public WhereWrapper createWhereWrapper(WhereWrapper tail, List<String> keywordList, int i) {
            return null;
        }
    },
    SELECT(false, "select", KeywordType.ACTION, Arrays.asList()),

    /*无语义关键字*/
    BY(false, "By", KeywordType.NONE, Arrays.asList()) {},
    SELECTIVE(false, "Selective", KeywordType.NONE, Arrays.asList()),

    /*连接关键字*/
    AND(false, "And", KeywordType.LINK, Arrays.asList()) {
        @Override
        protected void link(WhereWrapper tail, WhereWrapper whereWrapper) {
            tail.linkRule(whereWrapper, LinkOp.AND);
        }
    },
    OR(true, "Or", KeywordType.LINK, Arrays.asList()) {
        @Override
        protected void link(WhereWrapper tail, WhereWrapper whereWrapper) {
            tail.linkRule(whereWrapper, LinkOp.OR);
        }
    },

    /*操作符关键字*/
    EQ(true, "Eq", KeywordType.OP, Arrays.asList("=")),
    BETWEEN(true, "Between", KeywordType.OP, Arrays.asList()),

    /*运算型关键字*/
    TOP(true, "Top", KeywordType.FUNC, Arrays.asList()),
    GROUP_BY(true, "GroupBy", KeywordType.FUNC, Arrays.asList()),
    ORDER_BY(true, "OrderBy", KeywordType.FUNC, Arrays.asList()),
    DESC(true, "Desc", KeywordType.FUNC, Arrays.asList());

    private static Map<String, Keyword> keywordMap = new HashMap<>();
    private Boolean parse;
    private String keyword;
    private KeywordType keywordType;
    private List<String> sql;

    static {
        Keyword[] keywords = Keyword.values();
        for (Keyword keyword : keywords) {
            keywordMap.put(keyword.getKeyword(), keyword);
        }
    }

    Keyword(Boolean parse, String keyword, KeywordType keywordType, List<String> sql) {
        this.parse = parse;
        this.keyword = keyword;
        this.keywordType = keywordType;
        this.sql = sql;
    }

    public Boolean getParse() {
        return parse;
    }

    public String getKeyword() {
        return keyword;
    }

    public List<String> getDescription() {
        return sql;
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
                WhereWrapper whereWrapper = keyword.createWhereWrapper(tail, keywordList, i);
                if (whereWrapper == null) {
                    continue;
                }

                keyword.link(tail, whereWrapper);
                tail = whereWrapper;
            }
        }

        return head.getWhereWrapper();
    }

    public WhereWrapper createWhereWrapper(WhereWrapper tail, List<String> keywordList, int i) {
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
