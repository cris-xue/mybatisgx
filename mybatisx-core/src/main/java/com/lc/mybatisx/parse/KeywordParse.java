package com.lc.mybatisx.parse;

import com.lc.mybatisx.wrapper.WhereWrapper;
import org.apache.ibatis.annotations.Param;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

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
            int index = opKeyword.getIndex();
            int length = whereCount + index;
            List<String> javaColumnList = getJavaColumn(whereCount, length, method);
            whereCount = length;
            whereWrapper.setJavaColumn(javaColumnList);
        }

        return head.getWhereWrapper();
    }

    private static List<String> getJavaColumn(int whereCount, int length, Method method) {
        List<String> javaColumnList = new ArrayList<>();

        Parameter[] parameters = method.getParameters();
        for (; whereCount < length; whereCount++) {
            Parameter parameter = parameters[whereCount];
            Param param = parameter.getAnnotation(Param.class);
            if (param != null) {
                javaColumnList.add(param.value());
            }
        }

        return javaColumnList;
    }

    public static String buildLimitWrapper(List<String> keywordList) {
        for (int i = 0; i < keywordList.size(); i++) {
            String kw = keywordList.get(i);
            Keyword keyword = keywordMap.get(kw);
            if (keyword == null) {
                continue;
            }
            if (keyword.getKeywordType() == KeywordType.LIMIT) {
                return keyword.getSql();
            }
        }
        return null;
    }

    public static boolean isDynamic(List<String> keywordList) {
        String kw = keywordList.get(keywordList.size() - 1);
        Keyword keyword = keywordMap.get(kw);
        if (keyword == Keyword.SELECTIVE) {
            return true;
        }
        return false;
    }

}
