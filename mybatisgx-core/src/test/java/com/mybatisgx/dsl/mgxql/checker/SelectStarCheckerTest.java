package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * SELECT星号校验器测试
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SelectStarCheckerTest {

    private SelectStarChecker checker;

    @Before
    public void setUp() {
        checker = new SelectStarChecker();
    }

    @Test
    public void test01_multiEntityBareStar() {
        MgxqlStatement stmt = new MgxqlStatement();
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN_ALL);
        item.setFieldName("*");
        stmt.addSelectItem(item);
        setMultiEntityFrom(stmt);

        SyntaxCheckerContext context = buildMultiEntityContext();
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("SELECT *"));
    }

    @Test
    public void test02_multiEntityAliasStar() {
        MgxqlStatement stmt = new MgxqlStatement();
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN_ALL);
        item.setEntityAlias("user");
        item.setFieldName("*");
        stmt.addSelectItem(item);
        setMultiEntityFrom(stmt);

        SyntaxCheckerContext context = buildMultiEntityContext();
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test03_singleEntityBareStar() {
        MgxqlStatement stmt = new MgxqlStatement();
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN_ALL);
        item.setFieldName("*");
        stmt.addSelectItem(item);
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", null));
        stmt.setFromClause(fromClause);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(false);
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
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
