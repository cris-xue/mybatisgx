package com.mybatisgx.model;

/**
 * 外键字段是关系字段的特殊情况
 * @author ccxuef
 * @date 2025/8/8 17:48
 */
@Deprecated
public class ForeignKeyColumnInfo extends ForeignKeyInfo {

    public ForeignKeyColumnInfo() {
    }

    public ForeignKeyColumnInfo(ColumnInfo columnInfo, String referencedColumnName) {
        super(columnInfo, referencedColumnName);
    }
}
