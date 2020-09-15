package com.lc.mybatisx.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.exception.ParamMethodUnMatcherException;
import com.lc.mybatisx.wrapper.WhereWrapper;
import com.lc.mybatisx.wrapper.where.LinkOp;
import com.lc.mybatisx.wrapper.where.Operation;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：薛承城
 * @description：条件处理器
 * @date ：2020/7/20 12:17
 */
public class ConditionMapperHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConditionMapperHandler.class);

    protected List<String> parseMethodList;

    protected List<String> parseKeywordList;

    protected List<String> unParseKeywordList;

    public ConditionMapperHandler() {
    }

    public ConditionMapperHandler(List<String> parseMethodList) {
        this.parseMethodList = parseMethodList;

        parseKeywordList = new ArrayList<>();
        parseKeywordList.add("And");
        parseKeywordList.add("Or");
        parseKeywordList.add("OrderBy");
        parseKeywordList.add("GroupBy");

        unParseKeywordList = new ArrayList<>();
        unParseKeywordList.add("insert");
        unParseKeywordList.add("add");
        unParseKeywordList.add("delete");
        unParseKeywordList.add("update");
        unParseKeywordList.add("find");
        unParseKeywordList.add("select");
        unParseKeywordList.add("query");
        unParseKeywordList.add("By");
        unParseKeywordList.add("Selective");
    }

    public WhereWrapper buildWhereWrapper(Method method) {
        WhereWrapper whereWrapper = this.parseMethod(method);
        return whereWrapper;
    }

    /**
     * 是否解析方法
     *
     * @param methodName 方法名
     * @return true：解析
     */
    private boolean isParseMethod(String methodName) {
        for (String parseMethod : parseMethodList) {
            if (methodName.startsWith(parseMethod)) {
                logger.info("parse method: {}", methodName);
                return true;
            }
        }
        return false;
    }

    private WhereWrapper parseMethod(Method method) {
        String methodName = method.getName();

        if (!isParseMethod(methodName)) {
            return null;
        }

        List<String> conditionKeywordList = this.parseConditionKeyword1(methodName);
        List<String> conditionList = this.concatConditionKeyword(conditionKeywordList);
        return this.buildWrapper(method, conditionList);
    }

    private List<String> parseConditionKeyword1(String methodName) {
        int unParseKeywordOffset = 0;
        for (int i = 0; i < unParseKeywordList.size(); i++) {
            String unParseKeyword = unParseKeywordList.get(i);
            if (methodName.startsWith(unParseKeyword)) {
                unParseKeywordOffset += unParseKeyword.length();
                i = 0;

                methodName = methodName.substring(unParseKeyword.length());
            }
        }

        int parseKeywordOffset = 0;
        for (int i = 0; i < parseKeywordList.size(); i++) {
            String parseKeyword = parseKeywordList.get(i);
            if (methodName.startsWith(parseKeyword, parseKeywordOffset)) {
                parseKeywordOffset += parseKeyword.length();
            }
        }

        return null;
    }

    public List<String> parseConditionKeyword(String methodName) {
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

    private List<String> concatConditionKeyword(List<String> conditionKeywordList) {
        List<String> conditionList = new ArrayList<>();
        String condition = "";
        int conditionKeywordSize = conditionKeywordList.size();
        for (int i = 0; i < conditionKeywordSize; i++) {
            String conditionKeyword = conditionKeywordList.get(i);

            if ("By".equals(conditionKeyword) || "And".equals(conditionKeyword) || "Or".equals(conditionKeyword)) {
                conditionList.add(condition);
                conditionList.add(conditionKeyword);
                condition = "";
                continue;
            } else if (i == conditionKeywordSize - 1) {
                condition = condition + conditionKeyword;
                conditionList.add(condition);
                condition = "";
                continue;
            }

            condition = condition + conditionKeyword;
        }

        return conditionList;
    }

    /**
     * @param method
     * @param conditionList
     * @return
     */
    private WhereWrapper buildWrapper(Method method, List<String> conditionList) {
        Parameter[] parameters = method.getParameters();
        Operation[] operations = Operation.values();

        int conditionLength = conditionList.size();
        int parameterLength = parameters.length;
        int operationLength = operations.length;

        WhereWrapper whereWrapper = new WhereWrapper();
        for (int i = conditionLength, k = parameterLength - 1; i > 0; i = i - 2, k--) {
            String condition = conditionList.get(i - 1);

            // 分离字段和操作符
            for (int j = 0; j < operationLength; j++) {
                Operation operation = operations[j];
                boolean isMatcher;
                if (parameterLength == 1) {
                    isMatcher = this.setWhereWrapper(whereWrapper, parameters[0], operation, condition);
                } else {
                    Parameter parameter = k >= 0 && parameterLength >= k ? parameters[k] : null;
                    isMatcher = this.setWhereWrapper(whereWrapper, parameter, operation, condition);
                }

                if (isMatcher) {
                    break;
                }
            }

            String linkOp = conditionList.get(i - 2);
            if ("By".equals(linkOp)) {
                break;
            }

            WhereWrapper ww = new WhereWrapper();
            ww.linkRule(whereWrapper, LinkOp.valueOf(linkOp.toUpperCase()));
            whereWrapper = ww;
        }

        return whereWrapper;
    }

    private boolean setWhereWrapper(WhereWrapper whereWrapper, Parameter parameter, Operation operation, String condition) {
        List<String> operationNameList = operation.getName();

        for (String operationName : operationNameList) {
            if (condition.endsWith(operationName)) {
                String methodField = condition.replaceAll(operationName, "");
                String paramName = this.getParamName(methodField, parameter);

                // 把方法中的Username转成username。判断方法参数和方法名字段是否对应
                methodField = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, methodField);
                if (!methodField.equals(paramName)) {
                    String methodName = parameter.getDeclaringExecutable().getName();
                    logger.error("{} method name: {} param: {} un matcher", methodName, methodField, paramName);
                    throw new ParamMethodUnMatcherException("方法名条件和参数不匹配!");
                }

                methodField = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, methodField);
                whereWrapper.setDbColumn(methodField);
                whereWrapper.setOperation(operation);
                whereWrapper.setJavaColumn(Arrays.asList(paramName));

                return true;
            }
        }

        return false;
    }

    protected String getParamName(String methodField, Parameter parameter) {
        if (parameter == null) {
            return null;
        }

        Param param = parameter.getAnnotation(Param.class);
        return param != null ? param.value() : parameter.getName();
    }

}
