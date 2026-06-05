package com.mybatisgx.dsl.mgxql.model;

/**
 * MGXQL查询项类型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public enum SelectItemType {

    /**
     * select * 或 entity.*
     */
    COLUMN_ALL,
    /**
     * 自定义列：select id, name 或 entity.name
     */
    COLUMN,
    /**
     * count聚合函数
     */
    COUNT,
    /**
     * max聚合函数
     */
    MAX,
    /**
     * min聚合函数
     */
    MIN,
    /**
     * avg聚合函数
     */
    AVG,
    /**
     * sum聚合函数
     */
    SUM
}
