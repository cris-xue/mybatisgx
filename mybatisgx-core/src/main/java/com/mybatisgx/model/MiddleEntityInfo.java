package com.mybatisgx.model;

import java.util.List;

/**
 * 中间实体信息，仅用于多对多关联
 * @author ccxuef
 * @date 2025/9/20 18:00
 */
public class MiddleEntityInfo {

    /**
     * 表名称
     */
    private String tableName;
    /**
     * 左表join字段信息列表，如：user left join user_role on user.id = user_role.user_id
     */
    private List<ColumnInfo> leftColumnInfoList;
    /**
     * 右表join字段信息列表，如：user_role left join role on user_role.role_id = role.id
     */
    private List<ColumnInfo> rightColumnInfoList;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnInfo> getLeftColumnInfoList() {
        return leftColumnInfoList;
    }

    public void setLeftColumnInfoList(List<ColumnInfo> leftColumnInfoList) {
        this.leftColumnInfoList = leftColumnInfoList;
    }

    public List<ColumnInfo> getRightColumnInfoList() {
        return rightColumnInfoList;
    }

    public void setRightColumnInfoList(List<ColumnInfo> rightColumnInfoList) {
        this.rightColumnInfoList = rightColumnInfoList;
    }
}
