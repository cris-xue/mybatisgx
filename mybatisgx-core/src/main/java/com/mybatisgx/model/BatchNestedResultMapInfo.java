package com.mybatisgx.model;

/**
 * 批量查询结果映射信息
 *
 * @author ccxuef
 * @date 2025/9/22 20:46
 */
public class BatchNestedResultMapInfo extends ResultMapInfo {

    @Override
    public RelationColumnInfo getColumnInfo() {
        return (RelationColumnInfo) this.getComposites().get(0).getColumnInfo();
    }
}
