package com.mybatisgx.template;

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

    public String execute(OrderByClause orderByClause) {
        List<String> orderByList = new ArrayList<>();
        for (OrderByItem item : orderByClause.getItems()) {
            String columnName;
            if (item.getField().getColumnInfo() != null) {
                columnName = item.getField().getColumnInfo().getDbColumnName();
            } else {
                columnName = item.getField().getFieldName();
            }
            if (item.getField().getEntityAlias() != null && !item.getField().getEntityAlias().isEmpty()) {
                columnName = item.getField().getEntityAlias() + "." + columnName;
            }
            orderByList.add(String.format("%s %s", columnName, item.getDirection()));
        }
        return String.format(" ORDER BY %s", StringUtils.join(orderByList, ", "));
    }
}
