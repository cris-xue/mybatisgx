package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql include 单元块节点（Unit 层），对应 MyBatis {@code <include refid="..."/>}。
 * <p>由 {@code #include[sqlId]} 产生。{@code refid} 为静态标识符（不接受 {@code :param} / {@code #{}}）。
 * 渲染为 {@code <include refid="refid"/>}。include 不携带 {@code :param}，故位于 {@code #[...]}
 * 条件体内时不参与 auto-guard 收集（guard 退化为 {@code true}）。
 *
 * @author 薛承城
 * @description include 单元块 AST 节点
 * @date 2026/7/16
 */
public class IncludeUnit extends AbstractMgxsqlNode {

    /**
     * 被引用的 sql 片段 id（静态标识符）
     */
    private final String refid;

    public IncludeUnit(String refid, int startPosition, int line, int column) {
        super(startPosition, line, column);
        this.refid = refid;
    }

    public String getRefid() {
        return refid;
    }
}
