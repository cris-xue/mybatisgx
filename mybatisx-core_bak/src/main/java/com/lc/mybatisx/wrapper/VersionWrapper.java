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
    private String op = "=";
    /**
     * 乐观锁初始值
     */
    private Integer initValue;
    /**
     *
     */
    private Integer increment;
    /**
     *
     */
    private String sql = "%s %s #{ %s }";

    public void buildSql() {
        this.sql = String.format(sql, this.dbColumn, this.op, this.javaColumn);
    }

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

    public Integer getInitValue() {
        return initValue;
    }

    public void setInitValue(Integer initValue) {
        this.initValue = initValue;
    }

    public Integer getIncrement() {
        return increment;
    }

    public void setIncrement(Integer increment) {
        this.increment = increment;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
