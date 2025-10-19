package com.lc.mybatisx.annotation;

/**
 * 抓取模式
 * @author ccxuef
 * @date 2025/9/6 15:19
 */
public enum FetchMode {

    /**
     * 默认查询方式，存在N+1问题
     */
    SIMPLE,
    /**
     * 联表查询方式，可以把N+1变成1+1，但是存在结果膨胀问题，适合结果集数量较小的场景
     */
    JOIN,
    /**
     * 批量查询方式，可以把N+1变成1+M（M为关联表数量），适合结果集数量较大的场景
     */
    BATCH
}
