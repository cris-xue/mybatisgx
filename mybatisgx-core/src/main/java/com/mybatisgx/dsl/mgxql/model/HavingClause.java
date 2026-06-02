package com.mybatisgx.dsl.mgxql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * MGXQL HAVING子句模型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class HavingClause {

    /**
     * HAVING条件列表
     */
    private List<HavingCondition> conditions = new ArrayList<>();

    public List<HavingCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<HavingCondition> conditions) {
        this.conditions = conditions;
    }

    public void addCondition(HavingCondition condition) {
        this.conditions.add(condition);
    }
}
