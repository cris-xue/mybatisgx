package com.mybatisgx.dsl.method.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderBy {

    private String start;

    private List<OrderByTerm> orderByTermList = new ArrayList();

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public List<OrderByTerm> getOrderByTermList() {
        return orderByTermList;
    }

    public void setOrderByTermList(List<OrderByTerm> orderByTermList) {
        this.orderByTermList = orderByTermList;
    }

    public String toOrderBy() {
        List<String> orderByTermStringList = new ArrayList<>();
        for (OrderByTerm orderByTerm : orderByTermList) {
            orderByTermStringList.add(orderByTerm.toOrderByTerm());
        }
        String orderByTermString = StringUtils.join(orderByTermStringList, ",");
        return String.format("%s %s", start, orderByTermString);
    }
}
