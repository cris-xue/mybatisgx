package com.lc.mybatisx.wrapper;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2019/11/30 20:29
 */
public class InsertSqlWrapper extends SqlWrapper {

    private List<String> dbColumn;

    private List<String> entityColumn;

    public List<String> getDbColumn() {
        return dbColumn;
    }

    public void setDbColumn(List<String> dbColumn) {
        this.dbColumn = dbColumn;
    }

    public List<String> getEntityColumn() {
        return entityColumn;
    }

    public void setEntityColumn(List<String> entityColumn) {
        this.entityColumn = entityColumn;
    }
}
