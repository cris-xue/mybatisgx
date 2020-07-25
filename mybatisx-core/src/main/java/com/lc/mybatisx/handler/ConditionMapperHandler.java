package com.lc.mybatisx.handler;

import com.lc.mybatisx.wrapper.WhereWrapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：条件处理器
 * @date ：2020/7/20 12:17
 */
public class ConditionMapperHandler {

    private static Map<String, Boolean> unParseMap = new HashMap<>();

    static {
        // 会内存泄漏
        unParseMap.put("findAll", false);
        unParseMap.put("find", false);
    }

    public List<WhereWrapper> buildWhereWrapper(Method method) {
        String methodName = method.getName();

        List<WhereWrapper> whereWrapperList = new ArrayList<>();
        if (unParseMap.getOrDefault(methodName, true)) {
            String[] methodNames = methodName.split("By");
            String[] wheres = methodNames[1].split("And|Or");
            for (int i = 0; i < wheres.length; i++) {
                WhereWrapper whereWrapper = new WhereWrapper();
                whereWrapper.setField(wheres[i]);
                whereWrapper.setOp("=");
                whereWrapper.setValue("${" + wheres[i] + "}");
                whereWrapperList.add(whereWrapper);
            }
        }
        return whereWrapperList;
    }

}
