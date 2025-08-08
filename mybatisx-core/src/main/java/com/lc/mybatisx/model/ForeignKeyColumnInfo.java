package com.lc.mybatisx.model;

/**
 * 外键字段信息
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
     * 被引用实体的主键列名别名
     */
    private String referencedColumnNameAlias;

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

    public String getReferencedColumnNameAlias() {
        return referencedColumnNameAlias;
    }

    public void setReferencedColumnNameAlias(String referencedColumnNameAlias) {
        this.referencedColumnNameAlias = referencedColumnNameAlias;
    }
}
