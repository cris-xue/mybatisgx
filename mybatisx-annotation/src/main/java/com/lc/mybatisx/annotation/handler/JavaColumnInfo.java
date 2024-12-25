package com.lc.mybatisx.annotation.handler;

import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.annotation.Lock;
import com.lc.mybatisx.annotation.LogicDelete;

public class JavaColumnInfo {

    /**
     *
     */
    private Class<?> type;
    /**
     *
     */
    private String columnName;
    /**
     * 是否是主键
     */
    private Id id;
    /**
     * 是否是乐观锁字段
     */
    private Lock lock;
    /**
     * 是否是逻辑删除字段
     */
    private LogicDelete logicDelete;

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public LogicDelete getLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(LogicDelete logicDelete) {
        this.logicDelete = logicDelete;
    }
}
