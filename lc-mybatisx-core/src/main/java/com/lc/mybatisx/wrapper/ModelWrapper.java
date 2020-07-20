package com.lc.mybatisx.wrapper;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/5 15:24
 */
public class ModelWrapper {

    private String dbColumn;

    private String entityColumn;

    public String getDbColumn() {
        return dbColumn;
    }

    public void setDbColumn(String dbColumn) {
        this.dbColumn = dbColumn;
    }

    public String getEntityColumn() {
        return entityColumn;
    }

    public void setEntityColumn(String entityColumn) {
        this.entityColumn = entityColumn;
    }

}
