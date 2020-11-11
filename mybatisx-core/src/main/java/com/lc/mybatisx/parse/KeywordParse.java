package com.lc.mybatisx.parse;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.utils.ReflectUtils;
import com.lc.mybatisx.wrapper.LimitWrapper;
import com.lc.mybatisx.wrapper.OrderWrapper;
import com.lc.mybatisx.wrapper.WhereWrapper;
import org.apache.ibatis.annotations.Param;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeywordParse {

    private static Map<String, Keyword> keywordMap = new HashMap<>();
    private static Map<Class<?>, Boolean> basicTypeMap = new HashMap<>();

    static {
        Keyword[] keywords = Keyword.values();
        for (Keyword keyword : keywords) {
            keywordMap.put(keyword.getKeyword(), keyword);
        }

        basicTypeMap.put(Integer.class, true);
        basicTypeMap.put(Long.class, true);
        basicTypeMap.put(Double.class, true);
        basicTypeMap.put(String.class, true);
        basicTypeMap.put(Boolean.class, true);
        basicTypeMap.put(BigDecimal.class, true);
        basicTypeMap.put(Date.class, true);
        basicTypeMap.put(LocalDate.class, true);
        basicTypeMap.put(LocalDateTime.class, true);
    }

    public static List<String> parseMethod(Method method) {
        return parseMethod(method.getName());
    }

    public static List<String> parseMethod(String methodName) {
        // methodName = "findTop10ByIdAndNameIsOrAgeLessThanAndAgeLessThan";
        // updateByIdSelect
        // findById、findByIs、findByNameIsAndAgeIs
        // 创建 Pattern 对象
        String regex = "[a-z]+|By|And|Or|GroupBy|OrderBy|[A-Z][a-z]+|[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        // 创建 matcher 对象
        List<String> conditionKeywordList = new ArrayList<>();
        Matcher matcher = pattern.matcher(methodName);
        while (matcher.find()) {
            conditionKeywordList.add(matcher.group());
        }

        return conditionKeywordList;
    }

    public static WhereWrapper buildWhereWrapper(Method method, List<String> keywordList, Type[] daoInterfaceParams) {
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
                // 这里执行i++操作是为了让索引跳过top后面的数字
                i++;
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
            javaColumn = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, javaColumn);
            if (opKeyword == null) {
                opKeyword = Keyword.EQ;
            }

            WhereWrapper whereWrapper = new WhereWrapper();
            whereWrapper.setDbColumn(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, javaColumn));
            whereWrapper.setOp(opKeyword.getSql());
            whereWrapper.setLinkOp(linkOpKeyword != null ? linkOpKeyword.getSql() : null);

            tail.setWhereWrapper(whereWrapper);
            tail = whereWrapper;

            // 参数校验
            int index = opKeyword.getIndex();
            int length = whereCount + index;
            List<String> javaColumnList = getJavaColumn(whereCount, length, javaColumn, method, daoInterfaceParams);
            whereCount = length;
            whereWrapper.setJavaColumn(javaColumnList);

            whereWrapper.setTest(opKeyword.getTest(javaColumnList));
            whereWrapper.setSql(opKeyword.getSql(whereWrapper));
        }

        return head.getWhereWrapper();
    }

    private static List<String> getJavaColumn(int whereCount, int length, String javaColumn, Method method, Type[] daoInterfaceParams) {
        List<String> javaColumnList = new ArrayList<>();

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Type parameterType = parameter.getParameterizedType();
            Type type = parameter.getType();

            if (basicTypeMap.getOrDefault(parameterType, false)) {
                break;
            }
            if ("ENTITY".equals(parameterType.getTypeName())) {
                Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
                Field[] fields = ReflectUtils.getAllField(entityClass);
                for (Field field : fields) {
                    if (javaColumn.equals(field.getName())) {
                        javaColumnList.add(javaColumn);
                        break;
                    }
                }
                return javaColumnList;
            }
            if ("ID".equals(parameterType.getTypeName())) {
                Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
                Field[] fields = ReflectUtils.getAllField(entityClass);
                for (Field field : fields) {
                    if (javaColumn.equals(field.getName())) {
                        javaColumnList.add(javaColumn);
                        break;
                    }
                }
                return javaColumnList;
            }

            Field[] fields = ReflectUtils.getAllField((Class<?>) type);
            for (Field field : fields) {
                if (javaColumn.equals(field.getName())) {
                    javaColumnList.add(javaColumn);
                    break;
                }
            }
            return javaColumnList;
        }

        for (; whereCount < length; whereCount++) {
            Parameter parameter = parameters[whereCount];
            Param param = parameter.getAnnotation(Param.class);
            if (param != null) {
                javaColumnList.add(param.value());
            }
        }

        return javaColumnList;
    }

    public static LimitWrapper buildLimitWrapper(List<String> keywordList) {
        for (int i = 0; i < keywordList.size(); i++) {
            String kw = keywordList.get(i);
            Keyword keyword = keywordMap.get(kw);
            if (keyword == null) {
                continue;
            }
            if (keyword.getKeywordType() == KeywordType.LIMIT) {
                LimitWrapper limitWrapper = new LimitWrapper();
                String top = keywordList.get(++i);
                String sql = keyword.getSql(Arrays.asList(top));
                limitWrapper.setSql(sql);
                return limitWrapper;
            }
        }
        return null;
    }

    public static OrderWrapper buildOrderByWrapper(List<String> keywordList) {
        int length = keywordList.size();
        for (int i = 0; i < length; i++) {
            if (i + 1 >= length) {
                break;
            }
            String kw1 = keywordList.get(i);
            String kw2 = keywordList.get(i + 1);
            String kwFull = kw1 + kw2;
            Keyword keyword = keywordMap.get(kwFull);
            if (keyword == null) {
                continue;
            }
            if (keyword.getKeywordType() == KeywordType.ORDER) {
                OrderWrapper orderWrapper = new OrderWrapper();
                String sql = keyword.getSql(Arrays.asList(kwFull));
                orderWrapper.setSql(sql);
                return orderWrapper;
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
