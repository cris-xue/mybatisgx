package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.EmbeddedId;
import com.lc.mybatisx.annotation.Id;

/**
 * 字段信息信息
 *
 * @author ccxuef
 * @date 2025/8/9 15:54
 */
public class IdColumnInfo extends ColumnInfo {

    /**
     * 主键
     */
    private Id id;
    /**
     * 联合主键
     */
    private EmbeddedId embeddedId;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public EmbeddedId getEmbeddedId() {
        return embeddedId;
    }

    public void setEmbeddedId(EmbeddedId embeddedId) {
        this.embeddedId = embeddedId;
    }
}
