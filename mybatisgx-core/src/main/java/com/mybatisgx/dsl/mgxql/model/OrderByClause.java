package com.mybatisgx.dsl.mgxql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * MGXQL ORDER BY子句模型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class OrderByClause {

    /**
     * 排序项列表
     */
    private List<OrderByItem> items = new ArrayList<>();

    public List<OrderByItem> getItems() {
        return items;
    }

    public void setItems(List<OrderByItem> items) {
        this.items = items;
    }

    public void addItem(OrderByItem item) {
        this.items.add(item);
    }
}
