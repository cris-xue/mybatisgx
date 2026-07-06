package com.mybatisgx.template.select;

import com.mybatisgx.dsl.mgxql.model.OrderByClause;
import com.mybatisgx.dsl.mgxql.model.OrderByItem;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * MGXQL ORDER BY 模板处理器，从 OrderByClause + 已绑定的 columnInfo 渲染 ORDER BY SQL
 *
 * @author 薛承城
 * @date 2026/6/25
 */
public class MgxqlOrderByTemplateHandler {

    private AliasContext aliasContext;

    public String execute(OrderByClause orderByClause) {
        return this.doExecute(orderByClause);
    }

    public String execute(OrderByClause orderByClause, AliasContext aliasContext) {
        this.aliasContext = aliasContext;
        return this.doExecute(orderByClause);
    }

    private String doExecute(OrderByClause orderByClause) {
        List<String> orderByList = new ArrayList<>();
        for (OrderByItem item : orderByClause.getItems()) {
            String columnName;
            if (item.getField().getColumnInfo() != null) {
                columnName = item.getField().getColumnInfo().getDbColumnName();
            } else {
                columnName = item.getField().getFieldName();
            }
            if (item.getField().getEntityAlias() != null && !item.getField().getEntityAlias().isEmpty()) {
                String tableAlias = item.getField().getEntityAlias();
                if (this.aliasContext != null) {
                    tableAlias = this.aliasContext.resolveTableAlias(tableAlias);
                }
                columnName = tableAlias + "." + columnName;
            }
            orderByList.add(String.format("%s %s", columnName, item.getDirection()));
        }
        return String.format(" ORDER BY %s", StringUtils.join(orderByList, ", "));
    }
}
