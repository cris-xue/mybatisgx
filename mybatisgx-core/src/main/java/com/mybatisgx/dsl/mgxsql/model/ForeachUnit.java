package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql foreach 单元块节点（Unit 层），对应 MyBatis {@code <foreach>}。
 * <p>统一表达 IN 子句与 {@code #for} 指令的所有变体：
 * <ul>
 *   <li>{@code in :list} / {@code in (:list)} —— 简单类型，itemName="item"，valueExpr="#{item}"，{@code prependIn=true}</li>
 *   <li>{@code (item:collection)=>$item.prop} —— 复杂类型（in 糖），自定义 itemName 与 valueExpr，{@code prependIn=true}</li>
 *   <li>{@code #for(item:collection)=>$item} —— 独立迭代指令，脱离 {@code in} 关键字，{@code prependIn=false}</li>
 *   <li>复合 RHS {@code [$a,$b]} —— {@code composite=true}，渲染 {@code separator="),("}</li>
 * </ul>
 * {@code collectionName} 同时参与外层条件体的参数收集（生成 isNotEmpty）。
 *
 * @author 薛承城
 * @description foreach 单元块 AST 节点
 * @date 2026/7/13
 */
public class ForeachUnit extends AbstractMgxsqlNode {

    /**
     * 迭代变量名（简单类型固定为 "item"）
     */
    private final String itemName;

    /**
     * 集合参数名
     */
    private final String collectionName;

    /**
     * 迭代体值表达式（如 {@code #{item}} 或 {@code #{item.id}}，复合时为逗号连接的多个）
     */
    private final String valueExpr;

    /**
     * 渲染时是否在 {@code <foreach>} 前追加 {@code "in "}：in 糖=true（in 关键字被 foreach 消费），
     * {@code #for}=false（in 关键字作为普通 SQL 文本已在前）
     */
    private final boolean prependIn;

    /**
     * 是否为复合键（RHS 为 {@code [$a,$b]} 多 token），决定 {@code separator="),("}
     */
    private final boolean composite;

    /**
     * 全参构造器（v6）
     */
    public ForeachUnit(String itemName, String collectionName, String valueExpr,
                       boolean prependIn, boolean composite,
                       int startPosition, int line, int column) {
        super(startPosition, line, column);
        this.itemName = itemName;
        this.collectionName = collectionName;
        this.valueExpr = valueExpr;
        this.prependIn = prependIn;
        this.composite = composite;
    }

    /**
     * 兼容构造器（in 糖：prependIn=true，composite=false）
     */
    public ForeachUnit(String itemName, String collectionName, String valueExpr,
                       int startPosition, int line, int column) {
        this(itemName, collectionName, valueExpr, true, false, startPosition, line, column);
    }

    public String getItemName() {
        return itemName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getValueExpr() {
        return valueExpr;
    }

    public boolean isPrependIn() {
        return prependIn;
    }

    public boolean isComposite() {
        return composite;
    }
}
