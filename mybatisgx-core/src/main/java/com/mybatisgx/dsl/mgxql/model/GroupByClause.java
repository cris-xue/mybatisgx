package com.mybatisgx.dsl.mgxql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * MGXQL GROUP BY子句模型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class GroupByClause {

    /**
     * 分组字段列表
     */
    private List<FieldReference> fields = new ArrayList<>();

    public List<FieldReference> getFields() {
        return fields;
    }

    public void setFields(List<FieldReference> fields) {
        this.fields = fields;
    }

    public void addField(FieldReference field) {
        this.fields.add(field);
    }
}
