package com.mybatisgx.api;

/**
 * 数据值生命周期枚举
 * @author 薛承城
 * @date 2025/12/14 18:28
 */
public enum ValueProcessPhase {

    /**
     * 插入前
     */
    INSERT,
    /**
     * 更新前
     */
    UPDATE,
    /**
     * 逻辑删除前
     */
    LOGIC_DELETE,
}
