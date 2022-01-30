package com.lc.mybatisx.parse;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.handler.WhereMapperHandler;
import com.lc.mybatisx.syntax.MethodNameLexer;
import com.lc.mybatisx.syntax.MethodNameParser;
import com.lc.mybatisx.utils.ReflectUtils;
import com.lc.mybatisx.wrapper.ModelWrapper;
import com.lc.mybatisx.wrapper.OrderWrapper;
import com.lc.mybatisx.wrapper.WhereWrapper;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
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

    private static Map<String, Keyword> keywordMap = new LinkedHashMap<>();
    private static Map<Class<?>, Boolean> basicTypeMap = new HashMap<>();
    private static String regex = "[a-z]+|[A-Z][a-z]+|[0-9]+";

    static {
        Keyword[] keywords = Keyword.values();
        StringBuilder sb = new StringBuilder("");
        for (Keyword keyword : keywords) {
            String kw = keyword.getKeyword();
            keywordMap.put(kw, keyword);
            sb.append(kw).append("|");
        }
        // sb.append("[a-z]+|[A-Z][a-z]+|[0-9]+");
        // regex = sb.toString();

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

    public static Map<String, Keyword> getKeywordMap() {
        return keywordMap;
    }

    public static List<String> parseMethod(Method method, Class<?> entityClass) {
        return parseMethod(method.getName(), entityClass);
    }

    /*public static List<String> parseMethod(String methodName, Class<?> entityClass) {
        List<String> methodKeywordList = splitKeyword(methodName);
        methodKeywordList = mergeSqlKeyword(methodKeywordList);
        methodKeywordList = mergeFieldKeyword(methodKeywordList);
        return methodKeywordList;
    }*/

    public static List<String> parseMethod(String methodName, Class<?> entityClass) {
        CharStream input = CharStreams.fromString(methodName);
        MethodNameLexer methodNameLexer = new MethodNameLexer(input);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);

        ParseTree qlStatementContext = methodNameParser.sql_statement();
        List<String> sqlKeyword = new ArrayList<>();
        getSqlKeyword(sqlKeyword, qlStatementContext);
        return sqlKeyword;
    }

    private static void getSqlKeyword(List<String> sqlKeyword, ParseTree parseTree) {
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            if (parseTreeChild instanceof TerminalNodeImpl) {
                sqlKeyword.add(parseTreeChild.getText());
            }
            getSqlKeyword(sqlKeyword, parseTreeChild);
        }
    }

    public static SqlModel parseMethod1(Method method, Class<?> entityClass) {
        return parseMethod1(method.getName(), entityClass);
    }

    public static SqlModel parseMethod1(String methodName, Class<?> entityClass) {
        CharStream input = CharStreams.fromString(methodName);
        MethodNameLexer methodNameLexer = new MethodNameLexer(input);
        CommonTokenStream commonStream = new CommonTokenStream(methodNameLexer);
        MethodNameParser methodNameParser = new MethodNameParser(commonStream);

        ParseTree qlStatementContext = methodNameParser.sql_statement();

        SqlModel sqlModel = SqlModel.parse(null, methodName);
        return sqlModel;
    }

    private static void getSqlKeyword1(Map<Class<?>, List<Token>> sqlKeyword, ParseTree parseTree) {
        int childCount = parseTree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree parseTreeChild = parseTree.getChild(i);
            if (parseTreeChild instanceof TerminalNodeImpl) {
                TerminalNodeImpl terminalNode = (TerminalNodeImpl) parseTreeChild;

                Class<?> terminalNodeParentClass = terminalNode.getParent().getClass();
                if (!sqlKeyword.containsKey(terminalNodeParentClass)) {
                    sqlKeyword.put(terminalNodeParentClass, new ArrayList<>());
                }
                List<Token> tokenList = sqlKeyword.get(terminalNodeParentClass);
                tokenList.add(terminalNode.getSymbol());
            }
            getSqlKeyword1(sqlKeyword, parseTreeChild);
        }
    }

    private static List<String> splitKeyword(String methodName) {
        // methodName = "findTop10ByIdAndNameIsOrAgeLessThanAndAgeLessThan";
        // updateByIdSelect
        // findById、findByIs、findByNameIsAndAgeIs
        // 创建 Pattern 对象
        // String regex = "[a-z]+|GroupBy|OrderBy|By|And|Or|[A-Z][a-z]+|[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        // 创建 matcher 对象
        List<String> methodKeywordList = new LinkedList<>();
        Matcher matcher = pattern.matcher(methodName);
        while (matcher.find()) {
            methodKeywordList.add(matcher.group());
        }

        return methodKeywordList;
    }

    private static List<String> mergeSqlKeyword(List<String> methodKeywordList) {
        List<String> keywordList = new ArrayList<>();
        int length = methodKeywordList.size();
        for (int i = 0; i < length; i++) {
            String methodKeyword = methodKeywordList.get(i);
            keywordList.add(methodKeyword);

            if (i + 1 >= length) {
                break;
            }

            for (int j = i + 1; j < length; j++) {
                methodKeyword = methodKeyword + methodKeywordList.get(j);
                if (keywordMap.containsKey(methodKeyword)) {
                    keywordList.remove(keywordList.size() - 1);
                    keywordList.add(methodKeyword);
                    i = j;
                }
            }
        }

        return keywordList;
    }

    private static List<String> mergeFieldKeyword(List<String> methodKeywordList) {
        List<String> keywordList = new ArrayList<>();
        int size = methodKeywordList.size();
        for (int i = 0; i < size; i++) {
            String methodKeyword = methodKeywordList.get(i);
            keywordList.add(methodKeyword);

            if (i + 1 >= size) {
                break;
            }

            if (keywordMap.containsKey(methodKeyword)) {
                continue;
            }

            for (int j = i + 1; j < size; j++) {
                String nextMethodKeyword = methodKeywordList.get(j);
                if (keywordMap.containsKey(nextMethodKeyword)) {
                    break;
                }
                methodKeyword = methodKeyword + nextMethodKeyword;
                // Field field = ReflectUtils.getField(entityClass, CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, methodKeyword));
                keywordList.remove(keywordList.size() - 1);
                keywordList.add(methodKeyword);
                i = j;
            }
        }

        return keywordList;
    }

    public static WhereWrapper buildWhereWrapper(Method method, List<String> keywordList, Type[] daoInterfaceParams, List<ModelWrapper> modelWrapperList) {
        WhereMapperHandler whereMapperHandler = new WhereMapperHandler(keywordMap);
        return whereMapperHandler.build(method, keywordList, modelWrapperList);

        /*int whereCount = 0;
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

                if (k.getKeywordType() == KeywordType.WHERE) {
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

        return head.getWhereWrapper();*/
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

    /*public static LimitWrapper buildLimitWrapper(List<String> keywordList) {
        for (int i = 0; i < keywordList.size(); i++) {
            String kw = keywordList.get(i);
            Keyword keyword = keywordMap.get(kw);
            if (keyword == null) {
                continue;
            }
            if (keyword.getKeywordType() == KeywordType.LIMIT) {
                LimitWrapper limitWrapper = new LimitWrapper();
                String top = keywordList.get(++i);
                String sql = keyword.getSql("", Arrays.asList(top));
                limitWrapper.setSql(sql);
                return limitWrapper;
            }
        }
        return null;
    }*/

    public static OrderWrapper buildOrderByWrapper(List<String> keywordList) {
        OrderWrapper orderWrapper = null;
        String sql = "";

        int length = keywordList.size();
        boolean isOrder = false;
        for (int i = 0; i < length; i++) {
            String kw = keywordList.get(i);
            Keyword keyword = keywordMap.get(kw);
            if (keyword != null && keyword == Keyword.ORDER_BY) {
                orderWrapper = new OrderWrapper();
                isOrder = true;
                continue;
            }
            if (keyword == null && isOrder) {
                String dbColumn = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, kw);
                sql = orderWrapper.getKeyword().getSql(dbColumn);
                isOrder = false;
                continue;
            }
            if (keyword != null && keyword == Keyword.DESC) {
                sql += keyword.getSql();
                break;
            }
            if (keyword != null && keyword == Keyword.ASC) {
                sql += keyword.getSql();
                break;
            }
        }
        if (orderWrapper != null) {
            orderWrapper.setSql(sql);
        }

        return orderWrapper;
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
