package com.lc.mybatisx.handler;

import com.lc.mybatisx.wrapper.WhereWrapper;
import com.lc.mybatisx.wrapper.where.LinkOp;
import com.lc.mybatisx.wrapper.where.Operation;

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

    private static Map<String, Boolean> unParseMethodMap = new HashMap<>();

    static {
        // 会内存泄漏
        unParseMethodMap.put("findAll", false);
        unParseMethodMap.put("find", false);
    }

    public WhereWrapper buildWhereWrapper(Method method) {
        WhereWrapper whereWrapper = null;
        if (unParseMethodMap.getOrDefault(method.getName(), true)) {
            whereWrapper = this.parseMethod(method);
        }
        return whereWrapper;
    }

    private WhereWrapper parseMethod(Method method) {
        String methodName = method.getName();
        if (methodName.startsWith("findBy") && !methodName.startsWith("updateBy")) {
            return null;
        }

        List<String> conditionKeywordList = this.parseConditionKeyword(methodName);
        List<String> conditionGroup = this.concatConditionKeyword(conditionKeywordList);
        return this.buildWrapper(method, conditionGroup);
    }

    private List<String> parseConditionKeyword(String methodName) {
        methodName = "findTop10ByIdAndNameIsOrAgeLessThanAndAgeLessThan";

        // findById、findByIs、findByNameIsAndAgeIs
        // 创建 Pattern 对象
        String regex = "[A-Z][a-z]+[0-9]*";
        Pattern pattern = Pattern.compile(regex);
        // 现在创建 matcher 对象
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
        for (int i = conditionGroup.size(); i > 0; i = i - 2) {
            String c = conditionGroup.get(i - 1);

            whereWrapper.setField(c);
            whereWrapper.setOperation(Operation.EQ);
            whereWrapper.setValue("test");

            WhereWrapper ww = new WhereWrapper();
            ww.linkRule(whereWrapper, LinkOp.AND);
            whereWrapper = ww;

            String by = conditionGroup.get(i - 2);
            if ("By".equals(by)) {
                break;
            }
        }

        return whereWrapper.getWhereWrapper();
    }

}
