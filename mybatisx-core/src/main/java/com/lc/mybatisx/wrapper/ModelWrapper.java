package com.lc.mybatisx.wrapper;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/5 15:24
 */
public class ModelWrapper {

    private String dbColumn;

    private String dbType;

    private String javaColumn;

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

    public String getJavaColumn() {
        return javaColumn;
    }

    public void setJavaColumn(String javaColumn) {
        this.javaColumn = javaColumn;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }
}
