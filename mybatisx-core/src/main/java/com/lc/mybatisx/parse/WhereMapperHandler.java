package com.lc.mybatisx.parse;

import com.lc.mybatisx.wrapper.WhereWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WhereMapperHandler {

    private Map<String, Keyword> keywordMap;

    public WhereMapperHandler(Map<String, Keyword> keywordMap) {
        this.keywordMap = keywordMap;
    }

    public WhereWrapper build(List<String> keywordList) {
        WhereWrapper head = new WhereWrapper();
        WhereWrapper tail = new WhereWrapper();
        head = tail;

        WhereWrapper whereWrapper = null;
        boolean isWhere = false;
        for (int i = 0; i < keywordList.size(); i++) {
            String kw = keywordList.get(i);
            Keyword keyword = keywordMap.get(kw);

            WhereWrapper whereWrapperTemp = createWhereWrapper(keyword);
            if (whereWrapperTemp != null) {
                whereWrapper = whereWrapperTemp;
                isWhere = true;
                continue;
            }

            if (isWhere && keyword == null) {
                whereWrapper.setDbColumn(kw);
                whereWrapper.setJavaColumn(Arrays.asList(kw));
            }

            boolean isSetOp = setOp(keyword, whereWrapper);
            if (isSetOp) {
                tail.setWhereWrapper(whereWrapper);
                tail = whereWrapper;
                isWhere = false;
                continue;
            }

            boolean isSetDefaultOp = setDefaultOp(keywordList, i, isWhere, whereWrapper);
            if (isSetDefaultOp) {
                tail.setWhereWrapper(whereWrapper);
                tail = whereWrapper;
                isWhere = false;
                continue;
            }
        }

        return head.getWhereWrapper();
    }

    private WhereWrapper createWhereWrapper(Keyword keyword) {
        WhereWrapper whereWrapper = null;

        if (keyword != null && keyword == Keyword.BY) {
            whereWrapper = new WhereWrapper();
        }
        if (keyword != null && keyword == Keyword.AND) {
            whereWrapper = new WhereWrapper();
            whereWrapper.setLinkOp(keyword.getSql());
        }
        if (keyword != null && keyword == Keyword.OR) {
            whereWrapper = new WhereWrapper();
            whereWrapper.setLinkOp(keyword.getSql());
        }

        return whereWrapper;
    }

    private boolean setOp(Keyword keyword, WhereWrapper whereWrapper) {
        if (keyword != null && keyword.getKeywordType() == KeywordType.WHERE) {
            whereWrapper.setOp(keyword.getSql());
            whereWrapper.setSql(keyword.getSql(whereWrapper));
            whereWrapper.setTest(keyword.getTest(whereWrapper));
            return true;
        }

        return false;
    }

    private boolean setDefaultOp(List<String> keywordList, int index, boolean isWhere, WhereWrapper whereWrapper) {
        Keyword nextkeyword = null;
        int nextIndex = index + 1;
        if (nextIndex < keywordList.size()) {
            String nextKeyword = keywordList.get(nextIndex);
            nextkeyword = keywordMap.get(nextKeyword);
        }
        if (isWhere && (nextkeyword == null || nextkeyword.getKeywordType() != KeywordType.WHERE)) {
            Keyword keyword = Keyword.EQ;
            whereWrapper.setOp(keyword.getSql());
            whereWrapper.setSql(keyword.getSql(whereWrapper));
            whereWrapper.setTest(keyword.getTest(whereWrapper));
            return true;
        }

        return false;
    }

}
