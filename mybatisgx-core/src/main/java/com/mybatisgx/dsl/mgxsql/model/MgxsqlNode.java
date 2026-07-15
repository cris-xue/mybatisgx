package com.mybatisgx.dsl.mgxsql.model;

import com.mybatisgx.dsl.mgxsql.MgxsqlAstRenderer;

/**
 * mgxsql AST 节点根接口，所有 Scope/Unit/Expression 节点实现此接口。
 * <p>节点为纯数据（字段 + getter），不携带渲染逻辑；渲染由 {@link MgxsqlAstRenderer} 遍历完成。
 * 节点携带位置信息（起始位置/行/列），用于语法错误定位。
 *
 * @author 薛承城
 * @description mgxsql AST 节点根接口
 * @date 2026/7/13
 */
public interface MgxsqlNode {

    /**
     * 节点在输入文本中的起始位置（0 基）
     */
    int getStartPosition();

    /**
     * 节点起始行号（1 基）
     */
    int getLine();

    /**
     * 节点起始列号（1 基）
     */
    int getColumn();
}
