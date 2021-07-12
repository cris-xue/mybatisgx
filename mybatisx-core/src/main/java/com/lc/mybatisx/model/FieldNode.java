package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.Column;
import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.annotation.LogicDelete;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:39
 */
public class FieldNode {

    private Class<?> type;

    private String name;

    private Id id;

    private Column column;

    private LogicDelete logicDelete;

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LogicDelete getLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(LogicDelete logicDelete) {
        this.logicDelete = logicDelete;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }


}
