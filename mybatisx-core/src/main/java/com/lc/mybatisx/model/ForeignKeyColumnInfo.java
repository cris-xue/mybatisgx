package com.lc.mybatisx.model;

/**
 * 外键字段是关系字段的特殊情况
 * @author ccxuef
 * @date 2025/8/8 17:48
 */
public class ForeignKeyColumnInfo {

    /**
     * 外键列的名称（当前实体表中的列名）
     */
    private String name;
    /**
     * 外键列的名称别名
     */
    private String nameAlias;
    /**
     * 被引用实体的主键列名（目标表的主键列）
     */
    private String referencedColumnName;
    /**
     * 外键字段信息列表
     */
    private ColumnInfo columnInfo;
    /**
     * 被引用实体外键字段信息列表
     */
    private ColumnInfo referencedColumnInfo;

    public ForeignKeyColumnInfo() {
    }

    public ForeignKeyColumnInfo(ColumnInfo columnInfo, String referencedColumnName) {
        this.columnInfo = columnInfo;
        this.referencedColumnName = referencedColumnName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameAlias() {
        return nameAlias;
    }

    public void setNameAlias(String nameAlias) {
        this.nameAlias = nameAlias;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }

    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }

    public void setColumnInfo(ColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
    }

    public ColumnInfo getReferencedColumnInfo() {
        return referencedColumnInfo;
    }

    public void setReferencedColumnInfo(ColumnInfo referencedColumnInfo) {
        this.referencedColumnInfo = referencedColumnInfo;
    }
}
