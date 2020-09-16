package com.lc.mybatisx.parse;

import com.lc.mybatisx.wrapper.WhereWrapper;
import org.apache.ibatis.annotations.Param;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
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

            if (keyword == null) {
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
            }

            if (keyword.getKeywordType() == KeywordType.ACTION) {
                continue;
            }

            if (keyword.getKeywordType() == KeywordType.NONE) {
                continue;
            }

            if (keyword != null) {
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

            WhereWrapper whereWrapper = new WhereWrapper();
            whereWrapper.setDbColumn(javaColumn);
            if (opKeyword == null) {
                whereWrapper.setOp(Keyword.EQ.getSql());
            } else {
                whereWrapper.setOp(opKeyword.getSql());
            }
            if (linkOpKeyword != null) {
                whereWrapper.setLinkOp(linkOpKeyword.getSql());
            }
            tail.setWhereWrapper(whereWrapper);
            tail = whereWrapper;

            whereCount++;

            // 参数校验
            Parameter[] parameters = method.getParameters();
            int j = whereCount - 1;
            if (opKeyword == Keyword.BETWEEN) {
                whereCount = whereCount + 1;
            }
            List<String> javaColumnList = new ArrayList<>();
            for (; j < whereCount; j++) {
                Parameter parameter = parameters[j];
                Param param = parameter.getAnnotation(Param.class);
                javaColumnList.add(param.value());
            }
            whereWrapper.setJavaColumn(javaColumnList);
        }

        return head.getWhereWrapper();
    }

}
