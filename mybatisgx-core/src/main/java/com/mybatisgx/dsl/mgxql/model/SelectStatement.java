package com.mybatisgx.dsl.mgxql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * MGXQL语句模型，作为语法解析的中间表示，最终会转换成MyBatis XML
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class SelectStatement extends MgxqlStatement {

    /**
     * 查询项列表
     */
    private List<SelectItem> selectItems = new ArrayList<>();

    /**
     * FROM子句
     */
    private FromClause fromClause;

    /**
     * GROUP BY子句
     */
    private GroupByClause groupByClause;

    /**
     * HAVING子句
     */
    private HavingClause havingClause;

    /**
     * ORDER BY子句
     */
    private OrderByClause orderByClause;

    /**
     * LIMIT子句
     */
    private LimitClause limitClause;

    public List<SelectItem> getSelectItems() {
        return selectItems;
    }

    public void setSelectItems(List<SelectItem> selectItems) {
        this.selectItems = selectItems;
    }

    public void addSelectItem(SelectItem selectItem) {
        this.selectItems.add(selectItem);
    }

    public FromClause getFromClause() {
        return fromClause;
    }

    public void setFromClause(FromClause fromClause) {
        this.fromClause = fromClause;
    }

    public GroupByClause getGroupByClause() {
        return groupByClause;
    }

    public void setGroupByClause(GroupByClause groupByClause) {
        this.groupByClause = groupByClause;
    }

    public HavingClause getHavingClause() {
        return havingClause;
    }

    public void setHavingClause(HavingClause havingClause) {
        this.havingClause = havingClause;
    }

    public OrderByClause getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(OrderByClause orderByClause) {
        this.orderByClause = orderByClause;
    }

    public LimitClause getLimitClause() {
        return limitClause;
    }

    public void setLimitClause(LimitClause limitClause) {
        this.limitClause = limitClause;
    }
}
