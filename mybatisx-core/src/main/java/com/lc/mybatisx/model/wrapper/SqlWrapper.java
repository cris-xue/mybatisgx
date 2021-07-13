package com.lc.mybatisx.model.wrapper;

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
     * 返回结果
     */
    private ResultMapWrapper resultMapWrapper;
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

    public ResultMapWrapper getResultMapWrapper() {
        return resultMapWrapper;
    }

    public void setResultMapWrapper(ResultMapWrapper resultMapWrapper) {
        this.resultMapWrapper = resultMapWrapper;
    }

    public LogicDeleteWrapper getLogicDeleteWrapper() {
        return logicDeleteWrapper;
    }

    public void setLogicDeleteWrapper(LogicDeleteWrapper logicDeleteWrapper) {
        this.logicDeleteWrapper = logicDeleteWrapper;
    }
}
