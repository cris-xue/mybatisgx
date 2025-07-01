package com.lc.mybatisx.model;

public class ForeignKeyColumnInfo {

    /**
     * 关联字段
     */
    private String name;
    /**
     * 关联字段
     */
    private String referencedColumnName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }
}
