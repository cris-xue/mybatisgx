package com.lc.mybatisx.model;

/**
 * 外键字段是关系字段的特殊情况
 * @author ccxuef
 * @date 2025/8/8 17:48
 */
public class ForeignKeyInfo {

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

    public ForeignKeyInfo() {
    }

    public ForeignKeyInfo(ColumnInfo columnInfo, String referencedColumnName) {
        this.columnInfo = columnInfo;
        this.referencedColumnName = referencedColumnName;
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
