package com.lc.mybatisx.model.wrapper;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/11/9 16:54
 */
public class LogicDeleteWrapper {

    /**
     * 数据库字段
     */
    private String dbColumn;
    /**
     *
     */
    private String dbType;
    /**
     * 操作符
     */
    private String op;
    /**
     * 逻辑删除值
     */
    private String value;
    /**
     * 逻辑未删除值
     */
    private String notValue;
    /**
     *
     */
    private String javaType;

    public String getDbColumn() {
        return dbColumn;
    }

    public void setDbColumn(String dbColumn) {
        this.dbColumn = dbColumn;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNotValue() {
        return notValue;
    }

    public void setNotValue(String notValue) {
        this.notValue = notValue;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }
}
