package com.lc.mybatisx.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:14
 */
public class MethodInfo {

    private Method method;
    /**
     * sql动作，insert、delete、update、select
     */
    private String action;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 是否动态参数
     */
    private Boolean isDynamic;
    /**
     * 方法名查询信息
     */
    private List<ConditionInfo> conditionInfoList = new ArrayList<>();
    /**
     * 方法参数信息
     */
    private List<MethodParamInfo> methodParamInfoList;
    /**
     * 方法参数映射列表
     */
    private Map<String, MethodParamInfo> methodParamInfoMap = new LinkedHashMap<>();
    /**
     * 方法返回信息
     */
    private MethodReturnInfo methodReturnInfo;
    /**
     * 结果集信息
     */
    private ResultMapInfo resultMapInfo;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<ConditionInfo> getConditionInfoList() {
        return conditionInfoList;
    }

    public void setConditionInfoList(List<ConditionInfo> conditionInfoList) {
        this.conditionInfoList = conditionInfoList;
    }

    public Boolean getDynamic() {
        return isDynamic;
    }

    public void setDynamic(Boolean dynamic) {
        isDynamic = dynamic;
    }

    public List<MethodParamInfo> getMethodParamInfoList() {
        return methodParamInfoList;
    }

    public void setMethodParamInfoList(List<MethodParamInfo> methodParamInfoList) {
        this.methodParamInfoList = methodParamInfoList;
        // 写字段的时候在参数或者方法名中可能出现user_name写成username、userName两种情况
        this.methodParamInfoList.forEach(methodParamInfo -> {
            methodParamInfoMap.put(methodParamInfo.getParamName(), methodParamInfo);
            methodParamInfoMap.put(methodParamInfo.getParamName().toLowerCase(), methodParamInfo);
        });
    }

    public MethodParamInfo getMethodParamInfo(String paramName) {
        return methodParamInfoMap.get(paramName);
    }

    public Map<String, MethodParamInfo> getMethodParamInfoMap() {
        return methodParamInfoMap;
    }

    public void setMethodParamInfoMap(Map<String, MethodParamInfo> methodParamInfoMap) {
        this.methodParamInfoMap = methodParamInfoMap;
    }

    public MethodReturnInfo getMethodReturnInfo() {
        return methodReturnInfo;
    }

    public void setMethodReturnInfo(MethodReturnInfo methodReturnInfo) {
        this.methodReturnInfo = methodReturnInfo;
    }

    public ResultMapInfo getResultMapInfo() {
        return resultMapInfo;
    }

    public void setResultMapInfo(ResultMapInfo resultMapInfo) {
        this.resultMapInfo = resultMapInfo;
    }
}
