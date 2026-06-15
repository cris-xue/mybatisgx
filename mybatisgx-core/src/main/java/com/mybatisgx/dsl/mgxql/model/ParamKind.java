package com.mybatisgx.dsl.mgxql.model;

/**
 * 参数绑定形态枚举
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public enum ParamKind {

    /**
     * 简单类型：1列 ←→ 1路径
     */
    SIMPLE,
    /**
     * 复合主键：N列 ←→ N路径
     */
    COMPOSITE,
    /**
     * 关联类型：N列(FK) ←→ N路径(引用字段)
     */
    RELATION,
    /**
     * 无参数类型：IS NULL / IS NOT NULL
     */
    NULL_TYPE
}
