package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * ON子句别名校验器测试
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OnAliasCheckerTest {

    private OnAliasChecker checker;

    @Before
    public void setUp() {
        checker = new OnAliasChecker();
    }

    @Test
    public void test01_onLeftPreviousRightCurrent() {
        MgxqlStatement stmt = buildMultiJoin();
        SyntaxCheckerContext context = buildContext();
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test02_onLeftIsCurrentJoinAlias() {
        SelectStatement stmt = new SelectStatement();
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", "user"));
        JoinEntity join = new JoinEntity("Role", "role", JoinType.LEFT);
        join.setOnLeftAlias("role");
        join.setOnRightAlias("user");
        fromClause.addJoinEntity(join);
        stmt.setFromClause(fromClause);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(true);
        context.registerAlias("user");
        context.registerAlias("role");

        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        boolean hasLeftError = false;
        for (String error : context.getErrors()) {
            if (error.contains("左侧别名") && error.contains("role")) {
                hasLeftError = true;
            }
        }
        Assert.assertTrue(hasLeftError);
    }

    @Test
    public void test03_onLeftUndefinedAlias() {
        SelectStatement stmt = new SelectStatement();
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", "user"));
        JoinEntity join = new JoinEntity("Role", "role", JoinType.LEFT);
        join.setOnLeftAlias("dept");
        join.setOnRightAlias("role");
        fromClause.addJoinEntity(join);
        stmt.setFromClause(fromClause);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(true);
        context.registerAlias("user");
        context.registerAlias("role");

        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        boolean hasUndefinedError = false;
        for (String error : context.getErrors()) {
            if (error.contains("dept") && error.contains("未在FROM子句中定义")) {
                hasUndefinedError = true;
            }
        }
        Assert.assertTrue(hasUndefinedError);
    }

    @Test
    public void test04_onRightNotCurrentJoinAlias() {
        SelectStatement stmt = new SelectStatement();
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", "user"));
        JoinEntity join = new JoinEntity("Role", "role", JoinType.LEFT);
        join.setOnLeftAlias("user");
        join.setOnRightAlias("user");
        fromClause.addJoinEntity(join);
        stmt.setFromClause(fromClause);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(true);
        context.registerAlias("user");
        context.registerAlias("role");

        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        boolean hasRightError = false;
        for (String error : context.getErrors()) {
            if (error.contains("右侧别名") && error.contains("user")) {
                hasRightError = true;
            }
        }
        Assert.assertTrue(hasRightError);
    }

    @Test
    public void test05_chainedJoinOnPreviousEntity() {
        SelectStatement stmt = new SelectStatement();
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("A", "a"));

        JoinEntity join1 = new JoinEntity("B", "b", JoinType.LEFT);
        join1.setOnLeftAlias("a");
        join1.setOnRightAlias("b");
        fromClause.addJoinEntity(join1);

        JoinEntity join2 = new JoinEntity("C", "c", JoinType.LEFT);
        join2.setOnLeftAlias("b");
        join2.setOnRightAlias("c");
        fromClause.addJoinEntity(join2);

        stmt.setFromClause(fromClause);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(true);
        context.registerAlias("a");
        context.registerAlias("b");
        context.registerAlias("c");

        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test06_chainedJoinOnPrimaryEntity() {
        SelectStatement stmt = new SelectStatement();
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("A", "a"));

        JoinEntity join1 = new JoinEntity("B", "b", JoinType.LEFT);
        join1.setOnLeftAlias("a");
        join1.setOnRightAlias("b");
        fromClause.addJoinEntity(join1);

        JoinEntity join2 = new JoinEntity("C", "c", JoinType.LEFT);
        join2.setOnLeftAlias("a");
        join2.setOnRightAlias("c");
        fromClause.addJoinEntity(join2);

        stmt.setFromClause(fromClause);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(true);
        context.registerAlias("a");
        context.registerAlias("b");
        context.registerAlias("c");

        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    private MgxqlStatement buildMultiJoin() {
        SelectStatement stmt = new SelectStatement();
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", "user"));
        JoinEntity join = new JoinEntity("Role", "role", JoinType.LEFT);
        join.setOnLeftAlias("user");
        join.setOnRightAlias("role");
        fromClause.addJoinEntity(join);
        stmt.setFromClause(fromClause);
        return stmt;
    }

    private SyntaxCheckerContext buildContext() {
        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(true);
        context.registerAlias("user");
        context.registerAlias("role");
        return context;
    }
}
