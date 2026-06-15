package com.mybatisgx.dsl.mgxql.model.expression;

import com.mybatisgx.dsl.mgxql.model.AggregateFunction;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;

/**
 * SqlExpression 实现类 toSql() 输出测试
 *
 * @author 薛承城
 * @date 2026/6/15
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SqlExpressionTest {

    @Test
    public void test01_conditionColumnExpression() {
        ConditionColumnExpression expr = new ConditionColumnExpression("user_name");
        Assert.assertEquals("user_name", expr.toSql());
        Assert.assertEquals("user_name", expr.getDbColumnName());
        Assert.assertNull(expr.getTypeHandler());
    }

    @Test
    public void test02_conditionColumnExpressionWithTypeHandler() {
        ConditionColumnExpression expr = new ConditionColumnExpression("status", "com.example.StatusHandler");
        Assert.assertEquals("status", expr.toSql());
        Assert.assertEquals("com.example.StatusHandler", expr.getTypeHandler());
    }

    @Test
    public void test03_conditionCompositeExpression() {
        ConditionColumnExpression col1 = new ConditionColumnExpression("user_id");
        ConditionColumnExpression col2 = new ConditionColumnExpression("order_id");
        ConditionCompositeExpression expr = new ConditionCompositeExpression(Arrays.asList(col1, col2));
        Assert.assertEquals("user_id, order_id", expr.toSql());
        Assert.assertEquals(2, expr.getColumns().size());
    }

    @Test
    public void test04_havingAggregateExpression_count() {
        HavingAggregateExpression expr = new HavingAggregateExpression(AggregateFunction.COUNT, "user_id");
        Assert.assertEquals("count(user_id)", expr.toSql());
    }

    @Test
    public void test05_havingAggregateExpression_countStar() {
        HavingAggregateExpression expr = new HavingAggregateExpression(AggregateFunction.COUNT, "*");
        Assert.assertEquals("count(*)", expr.toSql());
    }

    @Test
    public void test06_havingAggregateExpression_countOne() {
        HavingAggregateExpression expr = new HavingAggregateExpression(AggregateFunction.COUNT, null);
        Assert.assertEquals("count(1)", expr.toSql());
    }

    @Test
    public void test07_havingAggregateExpression_max() {
        HavingAggregateExpression expr = new HavingAggregateExpression(AggregateFunction.MAX, "age");
        Assert.assertEquals("max(age)", expr.toSql());
    }

    @Test
    public void test08_havingAggregateExpression_min() {
        HavingAggregateExpression expr = new HavingAggregateExpression(AggregateFunction.MIN, "score");
        Assert.assertEquals("min(score)", expr.toSql());
    }

    @Test
    public void test09_havingAggregateExpression_avg() {
        HavingAggregateExpression expr = new HavingAggregateExpression(AggregateFunction.AVG, "salary");
        Assert.assertEquals("avg(salary)", expr.toSql());
    }

    @Test
    public void test10_havingAggregateExpression_sum() {
        HavingAggregateExpression expr = new HavingAggregateExpression(AggregateFunction.SUM, "amount");
        Assert.assertEquals("sum(amount)", expr.toSql());
    }

    @Test
    public void test11_groupByColumnExpression() {
        GroupByColumnExpression expr = new GroupByColumnExpression("status");
        Assert.assertEquals("status", expr.toSql());
        Assert.assertEquals("status", expr.getDbColumnName());
    }

    @Test
    public void test12_aggregateFunctionEnum() {
        Assert.assertEquals(5, AggregateFunction.values().length);
        Assert.assertEquals("count", AggregateFunction.COUNT.getSqlKeyword());
        Assert.assertEquals("max", AggregateFunction.MAX.getSqlKeyword());
        Assert.assertEquals("min", AggregateFunction.MIN.getSqlKeyword());
        Assert.assertEquals("avg", AggregateFunction.AVG.getSqlKeyword());
        Assert.assertEquals("sum", AggregateFunction.SUM.getSqlKeyword());
    }
}
