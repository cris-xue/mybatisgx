package com.mybatisgx.dsl.method.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.ArrayList;
import java.util.List;

public class MethodStatement {

    private SqlCommandType sqlCommandType;

    private SelectItemType selectItemType;

    private String from;

    private List<String> whereList = new ArrayList();

    private OrderBy orderBy;

    private List<String> limitList = new ArrayList();

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }

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

    public List<String> getWhereList() {
        return whereList;
    }

    public void setWhereList(List<String> whereList) {
        this.whereList = whereList;
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
        String mgxql = "";
        if (sqlCommandType == SqlCommandType.UPDATE) {
            mgxql = String.format(
                    "%s %s %s",
                    sqlCommandType.name().toLowerCase(),
                    from,
                    StringUtils.join(whereList, " ")
            );
        }
        if (sqlCommandType == SqlCommandType.DELETE) {
            mgxql = String.format(
                    "%s %s %s",
                    sqlCommandType.name().toLowerCase(),
                    from,
                    StringUtils.join(whereList, " ")
            );
        }
        if (sqlCommandType == SqlCommandType.SELECT) {
            String orderBySql = "";
            if (orderBy != null) {
                orderBySql = orderBy.toOrderBy();
            }

            mgxql = String.format(
                    "%s %s %s %s %s %s",
                    sqlCommandType.name().toLowerCase(),
                    selectItemType.getValue(),
                    from,
                    StringUtils.join(whereList, " "),
                    orderBySql,
                    StringUtils.join(limitList, " ")
            );
        }
        return mgxql;
    }
}
