package com.lc.mybatisx.handler;

import com.lc.mybatisx.wrapper.WhereWrapper;
import com.lc.mybatisx.wrapper.where.LinkOp;
import com.lc.mybatisx.wrapper.where.Operation;

import java.lang.reflect.Method;
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
    private static List<String> conditionKeywordList = new ArrayList<>();

    static {
        // 会内存泄漏
        unParseMethodMap.put("findAll", false);
        unParseMethodMap.put("find", false);
    }

    static {
        conditionKeywordList.add("By");
        conditionKeywordList.add("And");
        conditionKeywordList.add("Or");
        conditionKeywordList.add("Is");
        conditionKeywordList.add("Equals");
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
        if (!methodName.startsWith("findBy")) {
            return null;
        }
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

        WhereWrapper whereWrapper = new WhereWrapper();
        boolean flag = false;
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

        return whereWrapper;
    }

}
