package com.lc.mybatisx.wrapper;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/31 14:03
 */
public class VersionWrapper {

    /**
     * 数据库字段
     */
    private String dbColumn;
    /**
     *
     */
    private String javaColumn;
    /**
     * 操作符
     */
    private String op = " = ";
    /**
     * 乐观锁初始值
     */
    private String value = "1";

    public String getDbColumn() {
        return dbColumn;
    }

    public void setDbColumn(String dbColumn) {
        this.dbColumn = dbColumn;
    }

    public String getJavaColumn() {
        return javaColumn;
    }

    public void setJavaColumn(String javaColumn) {
        this.javaColumn = javaColumn;
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
}
