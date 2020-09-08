package com.lc.mybatisx.wrapper.where;

import com.lc.mybatisx.wrapper.WhereWrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Keyword {

    FIND(false, "find", Arrays.asList()) {
        @Override
        public WhereWrapper parse(WhereWrapper tail, List<String> keywordList, int i) {
            return null;
        }
    },
    SELECT(false, "select", Arrays.asList()) {
        @Override
        public WhereWrapper parse(WhereWrapper tail, List<String> keywordList, int i) {
            return null;
        }
    },
    TOP(true, "Top", Arrays.asList()) {
        @Override
        public WhereWrapper parse(WhereWrapper tail, List<String> keywordList, int i) {
            return null;
        }
    },
    BY(false, "By", Arrays.asList()) {
        @Override
        public WhereWrapper parse(WhereWrapper tail, List<String> keywordList, int i) {
            String javaColumn = this.getJavaColumn(keywordList, i);

            WhereWrapper whereWrapper = new WhereWrapper();
            whereWrapper.setDbColumn(javaColumn);
            whereWrapper.setJavaColumn(javaColumn);
            return whereWrapper;
        }
        
    },
    GROUP_BY(true, "GroupBy", Arrays.asList()) {
        @Override
        public WhereWrapper parse(WhereWrapper tail, List<String> keywordList, int i) {
            return null;
        }
    },
    SELECTIVE(false, "Selective", Arrays.asList()) {
        @Override
        public WhereWrapper parse(WhereWrapper tail, List<String> keywordList, int i) {
            return null;
        }
    },
    AND(false, "And", Arrays.asList()) {
        @Override
        public WhereWrapper parse(WhereWrapper tail, List<String> keywordList, int i) {
            String javaColumn = this.getJavaColumn(keywordList, i);

            WhereWrapper whereWrapper = new WhereWrapper();
            whereWrapper.setDbColumn(javaColumn);
            whereWrapper.setJavaColumn(javaColumn);

            return whereWrapper;
        }

    },
    OR(true, "Or", Arrays.asList()) {
        @Override
        public WhereWrapper parse(WhereWrapper tail, List<String> keywordList, int i) {
            return null;
        }
    },
    EQ(true, "Eq", Arrays.asList()) {
        @Override
        public WhereWrapper parse(WhereWrapper tail, List<String> keywordList, int i) {
            return null;
        }
    },
    ORDER_BY(true, "OrderBy", Arrays.asList()) {
        @Override
        public WhereWrapper parse(WhereWrapper tail, List<String> keywordList, int i) {
            return null;
        }
    },
    DESC(true, "Desc", Arrays.asList()) {
        @Override
        public WhereWrapper parse(WhereWrapper tail, List<String> keywordList, int i) {
            return null;
        }
    },
    BETWEEN(true, "Between", Arrays.asList()) {
        @Override
        public WhereWrapper parse(WhereWrapper tail, List<String> keywordList, int i) {
            return null;
        }
    };

    private static Map<String, Keyword> keywordMap = new HashMap<>();
    private Boolean parse;
    private String keyword;
    private List<String> sql;

    static {
        Keyword[] keywords = Keyword.values();
        for (Keyword keyword : keywords) {
            keywordMap.put(keyword.getKeyword(), keyword);
        }
    }

    Keyword(Boolean parse, String keyword, List<String> sql) {
        this.parse = parse;
        this.keyword = keyword;
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
            String aaa = this.getJavaColumn(keywordList, i);
            return javaColumn + aaa;
        }
        return "";
    }

    public static WhereWrapper buildWhereWrapper(List<String> keywordList) {
        WhereWrapper tail = new WhereWrapper();
        WhereWrapper head = tail;
        for (int i = 0; i < keywordList.size(); i++) {
            Keyword keyword = keywordMap.get(keywordList.get(i));
            if (keyword != null) {
                WhereWrapper whereWrapper = keyword.parse(tail, keywordList, i);
                if (whereWrapper == null) {
                    continue;
                }

                tail.linkRule(whereWrapper, LinkOp.AND);
                tail = whereWrapper;
            }
        }

        return head;
    }

    public WhereWrapper parse(WhereWrapper tail, List<String> keywordList, int i) {
        String javaColumn = this.getJavaColumn(keywordList, i);

        WhereWrapper whereWrapper = new WhereWrapper();
        whereWrapper.setDbColumn(javaColumn);
        whereWrapper.setJavaColumn(javaColumn);

        return whereWrapper;
    }

}
