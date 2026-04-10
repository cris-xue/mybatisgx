package com.mybatisgx.model;

/**
 * 类型类别
 *
 * @author ccxuef
 * @date 2025/11/6 15:24
 */
public enum TypeCategory {

    SIMPLE,      // 基础类型 + String + Date + Enum（都可以直接映射）
    OBJECT,      // 普通Java对象
    COLLECTION   // List / Set / Map / 数组
}
