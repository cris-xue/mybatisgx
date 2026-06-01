package com.mybatisgx.dsl.method.model;

import com.mybatisgx.model.SelectItemType;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.ArrayList;
import java.util.List;

public class MgxqlContext {

    private SqlCommandType sqlCommandType;

    private SelectItemType selectItemType;

    private String from;

    private List<String> whereList = new ArrayList();

    private List<String> orderByList = new ArrayList();

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

    public List<String> getOrderByList() {
        return orderByList;
    }

    public void setOrderByList(List<String> orderByList) {
        this.orderByList = orderByList;
    }

    public List<String> getLimitList() {
        return limitList;
    }

    public void setLimitList(List<String> limitList) {
        this.limitList = limitList;
    }
}
