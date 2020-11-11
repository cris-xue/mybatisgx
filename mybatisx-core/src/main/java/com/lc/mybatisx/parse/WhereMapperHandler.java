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
            if (isWhere || keyword != null && (keyword.getKeywordType() == KeywordType.WHERE || keyword.getKeywordType() == KeywordType.LINK)) {
                if (keyword == Keyword.BY) {
                    whereWrapper = new WhereWrapper();
                    isWhere = true;
                    continue;
                }
                if (keyword == Keyword.AND) {
                    whereWrapper = new WhereWrapper();
                    whereWrapper.setLinkOp(keyword.getSql());
                    isWhere = true;
                    continue;
                }
                if (keyword == Keyword.OR) {
                    whereWrapper = new WhereWrapper();
                    whereWrapper.setLinkOp(keyword.getSql());
                    isWhere = true;
                    continue;
                }

                if (keyword == Keyword.EQ) {
                    whereWrapper.setOp(keyword.getSql());
                    isWhere = true;
                    continue;
                }
                if (keyword == Keyword.IS) {
                    whereWrapper.setOp(keyword.getSql());
                    isWhere = true;
                    continue;
                }
                if (keyword == Keyword.LESS_THAN) {
                    whereWrapper.setOp(keyword.getSql());
                    isWhere = true;
                    continue;
                }
                if (keyword == Keyword.BETWEEN) {
                    whereWrapper.setOp(keyword.getSql());
                    isWhere = true;
                    continue;
                }
                whereWrapper.setJavaColumn(Arrays.asList(kw));
                tail.setWhereWrapper(whereWrapper);
                tail = whereWrapper;
                isWhere = false;
            }
        }

        return head.getWhereWrapper();
    }

}
