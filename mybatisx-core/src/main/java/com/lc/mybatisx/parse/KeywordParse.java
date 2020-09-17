package com.lc.mybatisx.parse;

import com.lc.mybatisx.wrapper.WhereWrapper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeywordParse {

    private static Map<String, Keyword> keywordMap = new HashMap<>();

    static {
        Keyword[] keywords = Keyword.values();
        for (Keyword keyword : keywords) {
            keywordMap.put(keyword.getKeyword(), keyword);
        }
    }

    public static WhereWrapper buildWhereWrapper(Method method, List<String> keywordList) {
        int whereCount = 0;
        WhereWrapper tail = new WhereWrapper();
        WhereWrapper head = tail;
        for (int i = 0; i < keywordList.size(); i++) {
            String kw = keywordList.get(i);
            Keyword keyword = keywordMap.get(kw);

            if (keyword != null && keyword.getKeywordType() == KeywordType.ACTION) {
                continue;
            }

            if (keyword != null && keyword.getKeywordType() == KeywordType.NONE) {
                continue;
            }

            if (keyword != null && keyword.getKeywordType() == KeywordType.LIMIT) {
                String a = keywordList.get(++i);
                keyword.getSql(Arrays.asList(a));
                continue;
            }

            String javaColumn = "";
            Keyword opKeyword = null;
            Keyword linkOpKeyword = null;
            for (; i < keywordList.size(); i++) {
                String javaColumnTemp = keywordList.get(i);
                Keyword k = keywordMap.get(javaColumnTemp);

                if (k == null) {
                    javaColumn = javaColumn + javaColumnTemp;
                    continue;
                }

                if (k.getKeywordType() == KeywordType.OP) {
                    opKeyword = k;
                    continue;
                }
                if (k.getKeywordType() == KeywordType.LINK) {
                    linkOpKeyword = k;
                    break;
                }
            }
            if (opKeyword == null) {
                opKeyword = Keyword.EQ;
            }

            WhereWrapper whereWrapper = new WhereWrapper();
            whereWrapper.setDbColumn(javaColumn);
            whereWrapper.setOp(opKeyword.getSql());
            whereWrapper.setLinkOp(linkOpKeyword != null ? linkOpKeyword.getSql() : null);

            tail.setWhereWrapper(whereWrapper);
            tail = whereWrapper;

            // 参数校验
            Parameter[] parameters = method.getParameters();
            Object[] results = opKeyword.getSql(whereCount, parameters);
            // whereCount = (int) results[0];
            // String sql = (String) results[1];
            // List<String> javaColumnList = (List<String>) results[2];
            // whereWrapper.setJavaColumn(javaColumnList);

            whereCount++;
        }

        return head.getWhereWrapper();
    }

}
