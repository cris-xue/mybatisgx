package com.mybatisgx.dsl.mgxql.model.expression;

import org.apache.commons.lang3.StringUtils;

/**
 * WHERE 条件中的单列表达式
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public class ConditionColumnExpression extends ConditionSqlExpression {

    private final String dbColumnName;

    private final String typeHandler;

    private final String tableAlias;

    public ConditionColumnExpression(String dbColumnName, String typeHandler, String tableAlias) {
        this.dbColumnName = dbColumnName;
        this.typeHandler = typeHandler;
        this.tableAlias = tableAlias;
    }

    public ConditionColumnExpression(String dbColumnName, String typeHandler) {
        this(dbColumnName, typeHandler, null);
    }

    public ConditionColumnExpression(String dbColumnName) {
        this(dbColumnName, null, null);
    }

    @Override
    public String toSql() {
        if (StringUtils.isNotBlank(this.tableAlias)) {
            return this.tableAlias + "." + this.dbColumnName;
        }
        return this.dbColumnName;
    }

    public String getDbColumnName() {
        return dbColumnName;
    }

    public String getTypeHandler() {
        return typeHandler;
    }

    public String getTableAlias() {
        return tableAlias;
    }
}
