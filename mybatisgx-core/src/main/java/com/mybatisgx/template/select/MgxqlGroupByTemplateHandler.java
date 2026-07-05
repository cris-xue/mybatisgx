package com.mybatisgx.template.select;

import com.mybatisgx.dsl.mgxql.model.FieldReference;
import com.mybatisgx.dsl.mgxql.model.GroupByClause;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * MGXQL GROUP BY 模板处理器，从 GroupByClause + 已绑定的 columnInfo 渲染 GROUP BY SQL
 *
 * @author 薛承城
 * @date 2026/6/25
 */
public class MgxqlGroupByTemplateHandler {

    private AliasContext aliasContext;

    public String execute(GroupByClause groupByClause) {
        return this.doExecute(groupByClause);
    }

    public String execute(GroupByClause groupByClause, AliasContext aliasContext) {
        this.aliasContext = aliasContext;
        return this.doExecute(groupByClause);
    }

    private String doExecute(GroupByClause groupByClause) {
        List<String> groupByList = new ArrayList<>();
        for (FieldReference fieldRef : groupByClause.getFields()) {
            String columnName;
            if (fieldRef.getColumnInfo() != null) {
                columnName = fieldRef.getColumnInfo().getDbColumnName();
            } else {
                columnName = fieldRef.getFieldName();
            }
            if (fieldRef.getEntityAlias() != null && !fieldRef.getEntityAlias().isEmpty()) {
                String tableAlias = fieldRef.getEntityAlias();
                if (this.aliasContext != null) {
                    tableAlias = this.aliasContext.resolveTableAlias(tableAlias);
                }
                columnName = tableAlias + "." + columnName;
            }
            groupByList.add(columnName);
        }
        return String.format(" GROUP BY %s", StringUtils.join(groupByList, ", "));
    }
}
