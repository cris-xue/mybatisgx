package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.Column;
import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.annotation.Version;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:39
 */
public class EntityFieldNode extends FieldNode {

    private Id id;

    private Column column;

    private LogicDelete logicDelete;

    private Version version;

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
