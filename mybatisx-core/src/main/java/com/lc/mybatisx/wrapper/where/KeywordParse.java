package com.lc.mybatisx.wrapper.where;

import com.lc.mybatisx.wrapper.WhereWrapper;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class KeywordParse {

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

    protected static String getJavaColumn(List<String> keywordList, int i) {
        if (i + 1 >= keywordList.size()) {
            return "";
        }

        String javaColumn = keywordList.get(++i);
        Keyword keyword = keywordMap.get(javaColumn);
        if (keyword == null) {
            String javaColumnTemp = getJavaColumn(keywordList, i);
            return javaColumn + javaColumnTemp;
        }
        return "";
    }

    protected static Operation getOp(List<String> keywordList, int i) {
        if (i + 1 >= keywordList.size()) {
            return Operation.EQ;
        }

        String javaColumn = keywordList.get(++i);
        Keyword keyword = keywordMap.get(javaColumn);
        if (keyword == null) {
            return getOp(keywordList, i);
        }

        /*if (keyword != null && keyword.keywordType == KeywordType.LINK) {
            return Operation.EQ;
        } else if (keyword != null && keyword.keywordType == KeywordType.OP) {
            return Operation.valueOf(keyword.name());
        }*/
        return null;
    }

    public static WhereWrapper buildWhereWrapper(Method method, List<String> keywordList) {
        int whereCount = 0;
        WhereWrapper tail = new WhereWrapper();
        WhereWrapper head = tail;
        for (int i = 0; i < keywordList.size(); i++) {
            String kw = keywordList.get(i);
            Keyword keyword = keywordMap.get(kw);

            if (keyword != null) {
                continue;
            }

            String javaColumn = "";
            WhereWrapper whereWrapper = new WhereWrapper();
            Operation operationKeyword = null;
            LinkOp linkOpKeyword = null;
            for (; i < keywordList.size(); i++) {
                String javaColumnTemp = keywordList.get(i);
                Keyword k = keywordMap.get(javaColumnTemp);

                if (k == null) {
                    javaColumn = javaColumn + javaColumnTemp;
                    continue;
                }

                if (k.getKeywordType() == KeywordType.OP) {
                    operationKeyword = Operation.valueOf(k.name());
                    continue;
                }
                if (k.getKeywordType() == KeywordType.LINK) {
                    linkOpKeyword = LinkOp.valueOf(k.name());
                    break;
                }
            }
            whereWrapper.setDbColumn(javaColumn);
            whereWrapper.setJavaColumn(Arrays.asList(javaColumn));
            if (operationKeyword == null) {
                whereWrapper.setOperation(Operation.EQ);
            } else {
                whereWrapper.setOperation(operationKeyword);
            }
            if (linkOpKeyword != null) {
                whereWrapper.setLinkOp(linkOpKeyword);
            }
            tail.setWhereWrapper(whereWrapper);
            tail = whereWrapper;

            whereCount++;

            // 参数校验
            Parameter[] parameters = method.getParameters();
            int j = 0;
            if (operationKeyword == Operation.BETWEEN) {
                j = whereCount - 2;
            } else {
                j = whereCount - 1;
            }
            for (; j < whereCount; j++) {
                Parameter parameter = parameters[j];
                String dbColumn = tail.getDbColumn();
                System.out.println(dbColumn);
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

    public static WhereWrapper createWhereWrapper(Method method, List<String> keywordList, int i) {
        // 获取java字段
        String javaColumn = getJavaColumn(keywordList, i);
        if (!StringUtils.hasLength(javaColumn)) {
            return null;
        }

        // 条件操作符
        Operation operation = getOp(keywordList, i);
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
    }

}
