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
     * 数据库表名
     */
    private String tableName;
    /**
     * 数据
     */
    private List<ParamWrapper> paramWrapper;
    /**
     * 结果包装器
     */
    private List<ResultWrapper> resultWrapper;
    /**
     * 返回结果
     */
    private List<ResultMapWrapper> resultMapWrapper;
    /**
     * 逻辑删除包装器
     */
    private LogicDeleteWrapper logicDeleteWrapper;

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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ParamWrapper> getParamWrapper() {
        return paramWrapper;
    }

    public void setParamWrapper(List<ParamWrapper> paramWrapper) {
        this.paramWrapper = paramWrapper;
    }

    public List<ResultWrapper> getResultWrapper() {
        return resultWrapper;
    }

    public void setResultWrapper(List<ResultWrapper> resultWrapper) {
        this.resultWrapper = resultWrapper;
    }

    public List<ResultMapWrapper> getResultMapWrapper() {
        return resultMapWrapper;
    }

    public void setResultMapWrapper(List<ResultMapWrapper> resultMapWrapper) {
        this.resultMapWrapper = resultMapWrapper;
    }

    public LogicDeleteWrapper getLogicDeleteWrapper() {
        return logicDeleteWrapper;
    }

    public void setLogicDeleteWrapper(LogicDeleteWrapper logicDeleteWrapper) {
        this.logicDeleteWrapper = logicDeleteWrapper;
    }
}
