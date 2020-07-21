package com.lc.mybatisx.wrapper;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2019/11/30 20:29
 */
public class SqlWrapper {

    /**
     * 命名空间
     */
    private String namespace;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数类型
     */
    private String parameterType;
    /**
     * 数据库表名
     */
    private String tableName;
    /**
     * 返回结果类型
     */
    private String resultType;
    /**
     * 方法返回的模型映射器
     */
    private List<ModelWrapper> modelWrapperList;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public List<ModelWrapper> getModelWrapperList() {
        return modelWrapperList;
    }

    public void setModelWrapperList(List<ModelWrapper> modelWrapperList) {
        this.modelWrapperList = modelWrapperList;
    }
}
