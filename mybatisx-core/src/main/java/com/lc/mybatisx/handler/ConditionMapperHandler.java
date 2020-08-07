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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：薛承城
 * @description：条件处理器
 * @date ：2020/7/20 12:17
 */
public class ConditionMapperHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConditionMapperHandler.class);

    private static Map<String, Boolean> unParseMethodMap = new HashMap<>();
    private static List<String> parseMethodList = new ArrayList<>();

    static {
        // 会内存泄漏
        unParseMethodMap.put("findAll", false);
        unParseMethodMap.put("find", false);
    }

    static {
        parseMethodList.add("findTop10By");
        parseMethodList.add("findBy");
        parseMethodList.add("updateBy");
        parseMethodList.add("deleteBy");
    }

    public WhereWrapper buildWhereWrapper(Method method) {
        WhereWrapper whereWrapper = null;
        if (unParseMethodMap.getOrDefault(method.getName(), true)) {
            whereWrapper = this.parseMethod(method);
        }
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

        List<String> conditionKeywordList = this.parseConditionKeyword(methodName);
        List<String> conditionGroup = this.concatConditionKeyword(conditionKeywordList);
        return this.buildWrapper(method, conditionGroup);
    }

    private List<String> parseConditionKeyword(String methodName) {
        // methodName = "findTop10ByIdAndNameIsOrAgeLessThanAndAgeLessThan";

        // findById、findByIs、findByNameIsAndAgeIs
        // 创建 Pattern 对象
        String regex = "[A-Z][a-z]+[0-9]*";
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
        List<String> conditionGroup = new ArrayList<>();
        String condition = "";
        int conditionKeywordSize = conditionKeywordList.size();
        for (int i = 0; i < conditionKeywordSize; i++) {
            String conditionKeyword = conditionKeywordList.get(i);

            if ("By".equals(conditionKeyword) || "And".equals(conditionKeyword) || "Or".equals(conditionKeyword)) {
                conditionGroup.add(condition);
                conditionGroup.add(conditionKeyword);
                condition = "";
                continue;
            } else if (i == conditionKeywordSize - 1) {
                condition = condition + conditionKeyword;
                conditionGroup.add(condition);
                condition = "";
                continue;
            }

            condition = condition + conditionKeyword;
        }

        return conditionGroup;
    }

    /**
     * @param method
     * @param conditionGroup
     * @return
     */
    private WhereWrapper buildWrapper(Method method, List<String> conditionGroup) {
        Parameter[] parameters = method.getParameters();

        WhereWrapper whereWrapper = new WhereWrapper();
        for (int i = conditionGroup.size(), k = parameters.length - 1; i > 0; i = i - 2, k--) {
            String condition = conditionGroup.get(i - 1);

            // 分离字段和操作符
            Operation[] operations = Operation.values();
            for (int j = 0; j < operations.length; j++) {
                Operation operation = operations[j];
                Parameter parameter = k >= 0 && parameters.length >= k ? parameters[k] : null;

                boolean isMatcher = this.setWhereWrapper(whereWrapper, parameter, operation, condition);
                if (isMatcher) {
                    break;
                }
            }

            String linkOp = conditionGroup.get(i - 2);
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
                whereWrapper.setField(methodField);
                whereWrapper.setOperation(operation);
                whereWrapper.setValue(paramName);

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
