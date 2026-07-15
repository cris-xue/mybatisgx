package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql foreach 单元块节点（Unit 层），对应 MyBatis {@code <foreach>}。
 * <p>统一表达 IN 子句的所有变体：
 * <ul>
 *   <li>{@code in :list} / {@code in (:list)} —— 简单类型，itemName="item"，valueExpr="#{item}"</li>
 *   <li>{@code (item:collection)=>$item.prop} —— 复杂类型，自定义 itemName 与 valueExpr</li>
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
     * 迭代体值表达式（如 {@code #{item}} 或 {@code #{item.id}}）
     */
    private final String valueExpr;

    public ForeachUnit(String itemName, String collectionName, String valueExpr,
                       int startPosition, int line, int column) {
        super(startPosition, line, column);
        this.itemName = itemName;
        this.collectionName = collectionName;
        this.valueExpr = valueExpr;
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
}
