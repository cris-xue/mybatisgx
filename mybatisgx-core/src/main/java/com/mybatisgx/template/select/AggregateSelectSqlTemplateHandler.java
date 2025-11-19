package com.mybatisgx.template.select;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.TableFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 查询sql模板处理
 *
 * @author ccxuef
 * @date 2025/9/6 14:05
 */
public class AggregateSelectSqlTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(AggregateSelectSqlTemplateHandler.class);

    /**
     * 构建聚合查询
     * <code>
     * select count(name) from user
     * </code>
     *
     * @return
     */
    public PlainSelect buildSelectSql() {
        PlainSelect plainSelect = this.buildMainSelect();
        this.buildFromItem(plainSelect, "test_user_simple");
        return plainSelect;
    }

    private PlainSelect buildMainSelect() {
        PlainSelect plainSelect = new PlainSelect();

        // 2. 创建COUNT聚合函数
        Function countFunction = new Function();
        countFunction.setName("COUNT");
        countFunction.setParameters(new ExpressionList(Arrays.asList(new AllColumns()))); // COUNT(*)

        // 3. 将函数封装为SelectExpressionItem并添加到选择项
        Expression countExpression = new TableFunction(countFunction);

        plainSelect.addSelectItems(countExpression);
        return plainSelect;
    }

    private Table buildFromItem(PlainSelect plainSelect, String mainTableName) {
        Table mainTable = new Table(mainTableName);
        plainSelect.setFromItem(mainTable);
        return mainTable;
    }
}