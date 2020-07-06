package com.lc.mybatisx.wrapper;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2019/11/30 20:29
 */
public class SqlWrapper {

    private String namespace;
    private String methodName;
    private String parameterType;
    private String tableName;
    private String resultType;
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
