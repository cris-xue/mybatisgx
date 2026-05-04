package com.mybatisgx.model;

/**
 * @author：薛承城
 * @description：方法命令类型
 * @date：2026/5/4 16:28
 */
public enum MethodCommandType {

    /**
     * 插入
     */
    INSERT,
    /**
     * 更新
     */
    UPDATE,
    /**
     * 删除
     */
    DELETE,
    /**
     * 逻辑删除（update语义）
     */
    LOGIC_DELETE,
    /**
     * 查询
     */
    SELECT
}
