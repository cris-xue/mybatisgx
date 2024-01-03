package com.lc.mybatisx.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:14
 */
public class MethodInfo {

    /**
     * sql动作，insert、delete、update、select
     */
    private String action;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 方法名查询信息
     */
    private List<ConditionInfo> conditionInfoList = new ArrayList<>();
    /**
     * 是否动态参数
     */
    private Boolean isDynamic;
    /**
     * 是否单参数
     */
    private Boolean isSingleParam;
    /**
     * 方法参数信息
     */
    private MethodParamInfo methodParamInfo;
    /**
     * 方法参数列表
     */
    private List<MethodParamInfo> methodParamInfoList;
    /**
     * 方法返回信息
     */
    private MethodReturnInfo methodReturnInfo;

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

    public Boolean getSingleParam() {
        return isSingleParam;
    }

    public void setSingleParam(Boolean singleParam) {
        isSingleParam = singleParam;
    }

    public MethodParamInfo getMethodParamInfo() {
        return methodParamInfo;
    }

    public void setMethodParamInfo(MethodParamInfo methodParamInfo) {
        this.methodParamInfo = methodParamInfo;
    }

    public List<MethodParamInfo> getMethodParamInfoList() {
        return methodParamInfoList;
    }

    public void setMethodParamInfoList(List<MethodParamInfo> methodParamInfoList) {
        this.methodParamInfoList = methodParamInfoList;
    }

    public MethodReturnInfo getMethodReturnInfo() {
        return methodReturnInfo;
    }

    public void setMethodReturnInfo(MethodReturnInfo methodReturnInfo) {
        this.methodReturnInfo = methodReturnInfo;
    }
}
