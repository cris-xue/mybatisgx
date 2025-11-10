package com.mybatisgx.annotation.handler;

import com.mybatisgx.annotation.GenerateValue;
import com.mybatisgx.annotation.Id;
import com.mybatisgx.annotation.Lock;
import com.mybatisgx.annotation.LogicDelete;

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
    /**
     * 生成值注解
     */
    private GenerateValue generateValue;

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

    public GenerateValue getGenerateValue() {
        return generateValue;
    }

    public void setGenerateValue(GenerateValue generateValue) {
        this.generateValue = generateValue;
    }
}
