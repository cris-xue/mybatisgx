package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.*;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * 字段别名校验器测试
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FieldAliasCheckerTest {

    private FieldAliasChecker checker;

    @Before
    public void setUp() {
        checker = new FieldAliasChecker();
    }

    @Test
    public void test01_multiEntityWhereBareField() {
        MgxqlStatement stmt = buildMultiEntitySelect();
        WhereClause whereClause = new WhereClause();
        ConditionExpression expr = new ConditionExpression();
        ConditionNode node = new ConditionNode();
        node.setFieldName("id");
        node.setFieldAlias(null);
        expr.addNode(node);
        whereClause.setRootExpression(expr);
        stmt.setWhereClause(whereClause);

        SyntaxCheckerContext context = buildMultiEntityContext();
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("缺少实体别名前缀"));
    }

    @Test
    public void test02_multiEntitySelectBareField() {
        MgxqlStatement stmt = buildMultiEntitySelect();
        SyntaxCheckerContext context = buildMultiEntityContext();
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
    }

    @Test
    public void test03_multiEntityOrderByBareField() {
        MgxqlStatement stmt = buildCleanMultiEntityStmt();
        OrderByClause orderBy = new OrderByClause();
        orderBy.addItem(new OrderByItem(new FieldReference(null, "name"), null));
        stmt.setOrderByClause(orderBy);

        SyntaxCheckerContext context = buildMultiEntityContext();
        checker.check(stmt, context);
        boolean hasBareFieldError = false;
        for (String error : context.getErrors()) {
            if (error.contains("ORDER BY") && error.contains("缺少实体别名前缀")) {
                hasBareFieldError = true;
            }
        }
        Assert.assertTrue(hasBareFieldError);
    }

    @Test
    public void test04_multiEntityGroupByBareField() {
        MgxqlStatement stmt = buildCleanMultiEntityStmt();
        GroupByClause groupBy = new GroupByClause();
        groupBy.addField(new FieldReference(null, "status"));
        stmt.setGroupByClause(groupBy);

        SyntaxCheckerContext context = buildMultiEntityContext();
        checker.check(stmt, context);
        boolean hasBareFieldError = false;
        for (String error : context.getErrors()) {
            if (error.contains("GROUP BY") && error.contains("缺少实体别名前缀")) {
                hasBareFieldError = true;
            }
        }
        Assert.assertTrue(hasBareFieldError);
    }

    @Test
    public void test05_multiEntityAggregateBareField() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        SelectItem aggItem = new SelectItem();
        aggItem.setType(SelectItemType.COUNT);
        aggItem.setAggregateFieldRef(new FieldReference(null, "id"));
        stmt.addSelectItem(aggItem);
        setMultiEntityFrom(stmt);

        SyntaxCheckerContext context = buildMultiEntityContext();
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("缺少实体别名前缀"));
    }

    @Test
    public void test06_multiEntityHavingBareField() {
        MgxqlStatement stmt = buildCleanMultiEntityStmt();

        HavingClause havingClause = new HavingClause();
        HavingCondition condition = new HavingCondition();
        SelectItem aggFunc = new SelectItem();
        aggFunc.setType(SelectItemType.MAX);
        aggFunc.setAggregateFieldRef(new FieldReference(null, "age"));
        condition.setAggregateFunction(aggFunc);
        havingClause.addCondition(condition);
        stmt.setHavingClause(havingClause);

        SyntaxCheckerContext context = buildMultiEntityContext();
        checker.check(stmt, context);
        boolean hasBareFieldError = false;
        for (String error : context.getErrors()) {
            if (error.contains("HAVING") && error.contains("缺少实体别名前缀")) {
                hasBareFieldError = true;
            }
        }
        Assert.assertTrue(hasBareFieldError);
    }

    @Test
    public void test07_aliasNotExist() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN);
        item.setEntityAlias("dept");
        item.setFieldName("id");
        stmt.addSelectItem(item);
        setMultiEntityFrom(stmt);

        SyntaxCheckerContext context = buildMultiEntityContext();
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        boolean hasAliasError = false;
        for (String error : context.getErrors()) {
            if (error.contains("dept") && error.contains("未在FROM子句中定义")) {
                hasAliasError = true;
            }
        }
        Assert.assertTrue(hasAliasError);
    }

    @Test
    public void test08_deleteWithAlias() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.DELETE);
        WhereClause whereClause = new WhereClause();
        ConditionExpression expr = new ConditionExpression();
        ConditionNode node = new ConditionNode();
        node.setFieldAlias("user");
        node.setFieldName("id");
        expr.addNode(node);
        whereClause.setRootExpression(expr);
        stmt.setWhereClause(whereClause);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(false);
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("DELETE/UPDATE"));
    }

    @Test
    public void test09_singleEntityBareFieldAllowed() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN);
        item.setFieldName("id");
        stmt.addSelectItem(item);

        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", null));
        stmt.setFromClause(fromClause);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(false);
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test10_singleEntityWithAliasBareFieldAllowed() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN);
        item.setFieldName("id");
        stmt.addSelectItem(item);

        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", "user"));
        stmt.setFromClause(fromClause);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(false);
        context.registerAlias("user");
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test11_multiEntityWithAliasPrefixNoError() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN);
        item.setEntityAlias("user");
        item.setFieldName("id");
        stmt.addSelectItem(item);
        setMultiEntityFrom(stmt);

        SyntaxCheckerContext context = buildMultiEntityContext();
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    private MgxqlStatement buildMultiEntitySelect() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN);
        item.setFieldName("id");
        stmt.addSelectItem(item);
        setMultiEntityFrom(stmt);
        return stmt;
    }

    private MgxqlStatement buildCleanMultiEntityStmt() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN);
        item.setEntityAlias("user");
        item.setFieldName("id");
        stmt.addSelectItem(item);
        setMultiEntityFrom(stmt);
        return stmt;
    }

    private void setMultiEntityFrom(MgxqlStatement stmt) {
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", "user"));
        fromClause.addJoinEntity(new JoinEntity("Role", "role", JoinType.LEFT));
        stmt.setFromClause(fromClause);
    }

    private SyntaxCheckerContext buildMultiEntityContext() {
        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(true);
        context.registerAlias("user");
        context.registerAlias("role");
        return context;
    }
}
