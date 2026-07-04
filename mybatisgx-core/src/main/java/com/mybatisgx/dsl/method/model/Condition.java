package com.mybatisgx.dsl.method.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Condition {

    private String start;

    private List<ConditionTerm> conditionTermList = new ArrayList();

    public Condition(String start, List<ConditionTerm> conditionTermList) {
        this.start = start;
        this.conditionTermList = conditionTermList;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public List<ConditionTerm> getConditionTermList() {
        return conditionTermList;
    }

    public void setConditionTermList(List<ConditionTerm> conditionTermList) {
        this.conditionTermList = conditionTermList;
    }

    public String toCondition() {
        List<String> conditionTermStringList = new ArrayList<>();
        for (ConditionTerm conditionTerm : conditionTermList) {
            conditionTermStringList.add(conditionTerm.toConditionTerm());
        }
        String conditionTermString = StringUtils.join(conditionTermStringList, " ");
        return String.format("%s %s", start, conditionTermString);
    }
}
