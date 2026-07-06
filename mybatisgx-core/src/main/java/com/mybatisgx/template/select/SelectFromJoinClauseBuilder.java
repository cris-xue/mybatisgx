package com.mybatisgx.template.select;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.commons.lang3.StringUtils;

public class SelectFromJoinClauseBuilder {

    public Table buildFromItem(PlainSelect plainSelect, String mainTableName, String mainTableNameAlias) {
        Table mainTable = new Table(mainTableName);
        if (StringUtils.isNotBlank(mainTableNameAlias)) {
            mainTable.setAlias(new Alias(mainTableNameAlias));
        }
        plainSelect.setFromItem(mainTable);
        return mainTable;
    }

    public Join buildLeftJoin(String rightTableName, String rightTableNameAlias) {
        Table table = new Table(rightTableName);
        if (StringUtils.isNotBlank(rightTableNameAlias)) {
            table.setAlias(new Alias(rightTableNameAlias));
        }
        Join join = new Join();
        join.setLeft(true);
        join.setRightItem(table);
        return join;
    }
}
