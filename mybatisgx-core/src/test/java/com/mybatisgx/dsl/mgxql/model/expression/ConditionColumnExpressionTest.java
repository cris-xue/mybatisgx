package com.mybatisgx.dsl.mgxql.model.expression;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * ConditionColumnExpression tableAlias 渲染测试
 *
 * @author 薛承城
 * @date 2026/6/30
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConditionColumnExpressionTest {

    @Test
    public void test01_toSqlWithoutTableAliasReturnsBareColumnName() {
        ConditionColumnExpression expr = new ConditionColumnExpression("user_name");
        Assert.assertEquals("tableAlias 为 null 时 toSql() 应返回裸列名", "user_name", expr.toSql());
        Assert.assertNull("tableAlias 应为 null", expr.getTableAlias());
    }

    @Test
    public void test02_toSqlWithTableAliasReturnsAliasedColumnName() {
        ConditionColumnExpression expr = new ConditionColumnExpression("name", null, "u");
        Assert.assertEquals("tableAlias 非空时 toSql() 应返回 alias.col", "u.name", expr.toSql());
        Assert.assertEquals("u", expr.getTableAlias());
    }

    @Test
    public void test03_toSqlWithTableAliasAndTypeHandler() {
        ConditionColumnExpression expr = new ConditionColumnExpression("status", "com.example.StatusHandler", "user");
        Assert.assertEquals("带 typeHandler 时 toSql() 仍应返回 alias.col", "user.status", expr.toSql());
        Assert.assertEquals("com.example.StatusHandler", expr.getTypeHandler());
        Assert.assertEquals("user", expr.getTableAlias());
    }

    @Test
    public void test04_twoArgConstructorTableAliasDefaultsToNull() {
        ConditionColumnExpression expr = new ConditionColumnExpression("code", "com.example.Handler");
        Assert.assertEquals("两参数构造函数 tableAlias 默认 null，应返回裸列名", "code", expr.toSql());
        Assert.assertNull("两参数构造函数 tableAlias 应为 null", expr.getTableAlias());
    }

    @Test
    public void test05_oneArgConstructorTableAliasDefaultsToNull() {
        ConditionColumnExpression expr = new ConditionColumnExpression("id");
        Assert.assertEquals("单参数构造函数 tableAlias 默认 null，应返回裸列名", "id", expr.toSql());
        Assert.assertNull("单参数构造函数 tableAlias 应为 null", expr.getTableAlias());
    }

    @Test
    public void test06_threeArgConstructorExplicitNullTableAliasReturnsBareColumn() {
        ConditionColumnExpression expr = new ConditionColumnExpression("age", null, null);
        Assert.assertEquals("显式 null tableAlias 应返回裸列名", "age", expr.toSql());
        Assert.assertNull(expr.getTableAlias());
    }
}
