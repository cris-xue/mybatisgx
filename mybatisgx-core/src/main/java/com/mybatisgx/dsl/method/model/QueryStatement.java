package com.mybatisgx.dsl.method.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class QueryStatement extends BaseStatement {

    protected SelectItemType selectItemType;

    protected String from;

    protected OrderBy orderBy;

    protected List<String> limitList = new ArrayList();

    public SelectItemType getSelectItemType() {
        return selectItemType;
    }

    public void setSelectItemType(SelectItemType selectItemType) {
        this.selectItemType = selectItemType;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public List<String> getLimitList() {
        return limitList;
    }

    public void setLimitList(List<String> limitList) {
        this.limitList = limitList;
    }

    public String toMgxql() {
        String orderBySql = "";
        if (orderBy != null) {
            orderBySql = orderBy.toOrderBy();
        }

        String whereSql = "";
        if (condition != null) {
            whereSql = condition.toCondition();
        }

        return String.format(
                "%s %s %s %s %s %s",
                sqlCommandType.name().toLowerCase(),
                selectItemType.getValue(),
                from,
                whereSql,
                orderBySql,
                StringUtils.join(limitList, " ")
        );
    }
}
