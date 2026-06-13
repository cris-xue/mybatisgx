package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;

/**
 * 别名要求校验器测试
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AliasRequirementCheckerTest {

    private AliasRequirementChecker checker;

    @Before
    public void setUp() {
        checker = new AliasRequirementChecker();
    }

    @Test
    public void test01_multiEntityPrimaryNoAlias() {
        SelectStatement stmt = new SelectStatement();
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", null));
        fromClause.addJoinEntity(new JoinEntity("Role", "role", JoinType.LEFT));
        stmt.setFromClause(fromClause);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(true);
        context.registerAlias("role");

        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("User"));
    }

    @Test
    public void test02_multiEntityJoinNoAlias() {
        SelectStatement stmt = new SelectStatement();
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", "user"));
        fromClause.addJoinEntity(new JoinEntity("Role", null, JoinType.LEFT));
        stmt.setFromClause(fromClause);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(true);
        context.registerAlias("user");

        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("Role"));
    }

    @Test
    public void test03_multiEntityAllHaveAliases() {
        SelectStatement stmt = new SelectStatement();
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", "user"));
        fromClause.addJoinEntity(new JoinEntity("Role", "role", JoinType.LEFT));
        stmt.setFromClause(fromClause);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(true);
        context.registerAlias("user");
        context.registerAlias("role");

        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test04_singleEntityNoAlias() {
        SelectStatement stmt = new SelectStatement();
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", null));
        stmt.setFromClause(fromClause);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(false);

        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test05_duplicateAlias() {
        SelectStatement stmt = new SelectStatement();
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", "user"));
        fromClause.addJoinEntity(new JoinEntity("Role", "user", JoinType.LEFT));
        stmt.setFromClause(fromClause);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(true);
        context.registerAlias("user");

        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        boolean hasDupError = false;
        for (String error : context.getErrors()) {
            if (error.contains("重复定义")) {
                hasDupError = true;
                break;
            }
        }
        Assert.assertTrue(hasDupError);
    }
}
