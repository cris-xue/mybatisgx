package com.mybatisgx.dsl.mgxql.checker.syntax;

import com.mybatisgx.dsl.mgxql.checker.SyntaxCheckerContext;
import com.mybatisgx.dsl.mgxql.checker.WhereRequiredChecker;
import com.mybatisgx.dsl.mgxql.model.*;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WhereRequiredCheckerTest {

    private WhereRequiredChecker checker;

    @Before
    public void setUp() {
        checker = new WhereRequiredChecker();
    }

    @Test
    public void test01_deleteWithoutWhere_shouldError() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.DELETE);
        SyntaxCheckerContext context = new SyntaxCheckerContext();
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("DELETE"));
    }

    @Test
    public void test02_updateWithoutWhere_shouldError() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.UPDATE);
        SyntaxCheckerContext context = new SyntaxCheckerContext();
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("UPDATE"));
    }

    @Test
    public void test03_deleteWithWhere_shouldPass() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.DELETE);
        WhereClause whereClause = new WhereClause(new WhereExpression(LogicOperator.NULL));
        WhereConditionNode node = new WhereConditionNode();
        node.setFieldName("id");
        whereClause.getRootExpression().addNode(node);
        stmt.setWhereClause(whereClause);
        SyntaxCheckerContext context = new SyntaxCheckerContext();
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test04_selectWithoutWhere_shouldNotTrigger() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        SyntaxCheckerContext context = new SyntaxCheckerContext();
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }
}
