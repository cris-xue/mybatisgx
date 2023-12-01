package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.annotation.Version;

import javax.persistence.Table;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:13
 */
public class EntityNode extends StructNode {

    private String name;

    private Table table;

    private LogicDelete logicDelete;

    private Version version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public LogicDelete getLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(LogicDelete logicDelete) {
        this.logicDelete = logicDelete;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }
}
