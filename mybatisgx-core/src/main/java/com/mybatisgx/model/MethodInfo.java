package com.mybatisgx.model;

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

    /**
     * 方法所属实体
     */
    private EntityInfo entityInfo;
    /**
     * java方法信息
     */
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
     * 语义表达式
     */
    private String statementExpression;
    /**
     * 是否动态参数
     */
    private Boolean isDynamic = false;
    /**
     * 是否批量操作
     */
    private Boolean isBatch = false;
    /**
     * 查询节点信息
     */
    private SelectItemInfo selectItemInfo;
    /**
     * 查询排序信息
     */
    private List<SelectOrderByInfo> selectOrderByInfoList;
    /**
     * 查询数量限制
     */
    private SelectPageInfo selectPageInfo;
    /**
     * 方法名条件信息【修改、删除、查询都可以存在条件】
     */
    private List<ConditionInfo> conditionInfoList = new ArrayList<>();
    /**
     * 实体参数
     * 1、如果存在实体参数，那么条件只能使用实体参数，如果不存在实体参数，那么条件只能使用方法参数列表
     * 2、方法参数中只要有一个是实体，就存在实体参数，如果存在多个实体参数，只取最后一个实体参数
     * 3、新增、修改、删除、查询都只允许一个实体参数
     */
    private MethodParamInfo entityParamInfo;
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

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

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

    public String getStatementExpression() {
        return statementExpression;
    }

    public void setStatementExpression(String statementExpression) {
        this.statementExpression = statementExpression;
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

    public SelectItemInfo getSelectItemInfo() {
        return selectItemInfo;
    }

    public void setSelectItemInfo(SelectItemInfo selectItemInfo) {
        this.selectItemInfo = selectItemInfo;
    }

    public List<SelectOrderByInfo> getSelectOrderByInfoList() {
        return selectOrderByInfoList;
    }

    public void setSelectOrderByInfoList(List<SelectOrderByInfo> selectOrderByInfoList) {
        this.selectOrderByInfoList = selectOrderByInfoList;
    }

    public SelectPageInfo getSelectPageInfo() {
        return selectPageInfo;
    }

    public void setSelectPageInfo(SelectPageInfo selectPageInfo) {
        this.selectPageInfo = selectPageInfo;
    }

    public List<ConditionInfo> getConditionInfoList() {
        return conditionInfoList;
    }

    public void setConditionInfoList(List<ConditionInfo> conditionInfoList) {
        this.conditionInfoList = conditionInfoList;
    }

    public MethodParamInfo getEntityParamInfo() {
        return entityParamInfo;
    }

    public void setEntityParamInfo(MethodParamInfo entityParamInfo) {
        this.entityParamInfo = entityParamInfo;
    }

    public List<MethodParamInfo> getMethodParamInfoList() {
        return methodParamInfoList;
    }

    public void setMethodParamInfoList(List<MethodParamInfo> methodParamInfoList) {
        this.methodParamInfoList = methodParamInfoList;
        // 写字段的时候在参数或者方法名中可能出现user_name写成username、userName两种情况
        for (MethodParamInfo methodParamInfo : methodParamInfoList) {
            methodParamInfoMap.put(methodParamInfo.getArgName(), methodParamInfo);
            methodParamInfoMap.put(methodParamInfo.getArgName().toLowerCase(), methodParamInfo);
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
