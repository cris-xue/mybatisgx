package com.mybatisgx.model;

/**
 * 查询节点信息
 * @author 薛承城
 * @date 2025/11/20 12:22
 */
public class SelectItemInfo {

    private SelectItemType selectItemType;

    private String expression;

    public SelectItemType getSelectItemType() {
        return selectItemType;
    }

    public void setSelectItemType(SelectItemType selectItemType) {
        this.selectItemType = selectItemType;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
