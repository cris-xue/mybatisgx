package com.lc.mybatisx.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 多对多关联表信息
 * @author ccxuef
 * @date 2025/8/2 23:29
 */
public class MiddleTableInfo {

    /**
     * 表名称
     */
    private String tableName;
    /**
     * 当前实体外键字段列表
     */
    private List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = new ArrayList();
    /**
     * 关联实体外键字段列表
     */
    private List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = new ArrayList();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ForeignKeyColumnInfo> getForeignKeyColumnInfoList() {
        return foreignKeyColumnInfoList;
    }

    public void setForeignKeyColumnInfoList(List<ForeignKeyColumnInfo> foreignKeyColumnInfoList) {
        this.foreignKeyColumnInfoList = foreignKeyColumnInfoList;
    }

    public List<ForeignKeyColumnInfo> getInverseForeignKeyColumnInfoList() {
        return inverseForeignKeyColumnInfoList;
    }

    public void setInverseForeignKeyColumnInfoList(List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList) {
        this.inverseForeignKeyColumnInfoList = inverseForeignKeyColumnInfoList;
    }
}
