package com.mybatisgx.dsl.mgxql.checker.syntax;

import com.mybatisgx.dsl.mgxql.checker.FieldAliasChecker;
import com.mybatisgx.dsl.mgxql.checker.SyntaxCheckerContext;
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
        WhereExpression expr = new WhereExpression();
        WhereConditionNode node = new WhereConditionNode();
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
        SelectStatement stmt = buildCleanMultiEntityStmt();
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
        SelectStatement stmt = buildCleanMultiEntityStmt();
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
        SelectStatement stmt = new SelectStatement();
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
        SelectStatement stmt = buildCleanMultiEntityStmt();

        HavingExpression havingExpression = new HavingExpression(LogicOperator.NULL);
        HavingConditionNode node = new HavingConditionNode();
        node.setLeftSide(new com.mybatisgx.dsl.mgxql.model.expression.HavingAggregateExpression(
                AggregateFunction.MAX, "age"));
        havingExpression.addNode(node);
        stmt.setHavingExpression(havingExpression);

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
        SelectStatement stmt = new SelectStatement();
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
    public void test09_singleEntityBareFieldAllowed() {
        SelectStatement stmt = new SelectStatement();
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
        SelectStatement stmt = new SelectStatement();
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
        SelectStatement stmt = new SelectStatement();
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
        SelectStatement stmt = new SelectStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN);
        item.setFieldName("id");
        stmt.addSelectItem(item);
        setMultiEntityFrom(stmt);
        return stmt;
    }

    private SelectStatement buildCleanMultiEntityStmt() {
        SelectStatement stmt = new SelectStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN);
        item.setEntityAlias("user");
        item.setFieldName("id");
        stmt.addSelectItem(item);
        setMultiEntityFrom(stmt);
        return stmt;
    }

    private void setMultiEntityFrom(SelectStatement stmt) {
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
