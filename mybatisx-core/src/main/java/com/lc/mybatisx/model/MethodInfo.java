package com.lc.mybatisx.model;

import org.apache.ibatis.mapping.SqlCommandType;

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
    private SqlCommandType sqlCommandType;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 查询条件
     */
    private String conditionGroupExpression;
    /**
     * 是否动态参数
     */
    private Boolean isDynamic = false;
    /**
     * 是否批量操作
     */
    private Boolean isBatch = false;
    /**
     * 方法名条件信息【修改、删除、查询都可以存在条件】
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
     * 结果集信息id
     */
    private String resultMapId;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getConditionGroupExpression() {
        return conditionGroupExpression;
    }

    public void setConditionGroupExpression(String conditionGroupExpression) {
        this.conditionGroupExpression = conditionGroupExpression;
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

    public Boolean getBatch() {
        return isBatch;
    }

    public void setBatch(Boolean batch) {
        isBatch = batch;
    }

    public List<MethodParamInfo> getMethodParamInfoList() {
        return methodParamInfoList;
    }

    public void setMethodParamInfoList(List<MethodParamInfo> methodParamInfoList) {
        this.methodParamInfoList = methodParamInfoList;
        // 写字段的时候在参数或者方法名中可能出现user_name写成username、userName两种情况
        for (MethodParamInfo methodParamInfo : methodParamInfoList) {
            methodParamInfoMap.put(methodParamInfo.getParamName(), methodParamInfo);
            methodParamInfoMap.put(methodParamInfo.getParamName().toLowerCase(), methodParamInfo);
            methodParamInfoMap.put(methodParamInfo.getArgName(), methodParamInfo);
        }
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

    public String getResultMapId() {
        return resultMapId;
    }

    public void setResultMapId(String resultMapId) {
        this.resultMapId = resultMapId;
    }
}
