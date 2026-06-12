package com.mybatisgx.model;

import com.mybatisgx.dsl.mgxql.model.SelectItemType;

/**
 * 聚合函数目标模型（COUNT / MAX / MIN / AVG / SUM）。
 * fieldRef 可为 null，对应 count(*) / count(1) 形态
 *
 * @author 薛承城
 * @date 2026/6/12
 */
public class AggregateFunctionInfo {

    private SelectItemType type;

    private GroupByFieldInfo fieldRef;

    public AggregateFunctionInfo() {
    }

    public AggregateFunctionInfo(SelectItemType type, GroupByFieldInfo fieldRef) {
        this.type = type;
        this.fieldRef = fieldRef;
    }

    public SelectItemType getType() {
        return type;
    }

    public void setType(SelectItemType type) {
        this.type = type;
    }

    public GroupByFieldInfo getFieldRef() {
        return fieldRef;
    }

    public void setFieldRef(GroupByFieldInfo fieldRef) {
        this.fieldRef = fieldRef;
    }
}
