package com.mybatisgx.dsl.mgxql.checker.syntax;

import com.mybatisgx.dsl.mgxql.checker.AggregateArgumentChecker;
import com.mybatisgx.dsl.mgxql.checker.SyntaxCheckerContext;
import com.mybatisgx.dsl.mgxql.model.AggregateArgumentKind;
import com.mybatisgx.dsl.mgxql.model.AggregateFunction;
import com.mybatisgx.dsl.mgxql.model.FieldReference;
import com.mybatisgx.dsl.mgxql.model.FromClause;
import com.mybatisgx.dsl.mgxql.model.FromEntity;
import com.mybatisgx.dsl.mgxql.model.HavingConditionNode;
import com.mybatisgx.dsl.mgxql.model.HavingExpression;
import com.mybatisgx.dsl.mgxql.model.LogicOperator;
import com.mybatisgx.dsl.mgxql.model.SelectItem;
import com.mybatisgx.dsl.mgxql.model.SelectItemType;
import com.mybatisgx.dsl.mgxql.model.SelectStatement;
import com.mybatisgx.dsl.mgxql.model.expression.HavingAggregateExpression;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * 聚合函数参数类型校验器测试
 *
 * @author 薛承城
 * @date 2026/6/30
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AggregateArgumentCheckerTest {

    private AggregateArgumentChecker checker;

    @Before
    public void setUp() {
        checker = new AggregateArgumentChecker();
    }

    @Test
    public void test01_maxAsteriskShouldError() {
        SelectStatement stmt = singleEntitySelect();
        stmt.addSelectItem(aggItem(SelectItemType.MAX, AggregateArgumentKind.ASTERISK, "*"));
        SyntaxCheckerContext context = new SyntaxCheckerContext();
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("max"));
        Assert.assertTrue(context.getErrors().get(0).contains("*"));
    }

    @Test
    public void test02_avgNumberShouldError() {
        SelectStatement stmt = singleEntitySelect();
        stmt.addSelectItem(aggItem(SelectItemType.AVG, AggregateArgumentKind.NUMBER, "1"));
        SyntaxCheckerContext context = new SyntaxCheckerContext();
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("avg"));
        Assert.assertTrue(context.getErrors().get(0).contains("数字"));
    }

    @Test
    public void test03_sumAsteriskShouldError() {
        SelectStatement stmt = singleEntitySelect();
        stmt.addSelectItem(aggItem(SelectItemType.SUM, AggregateArgumentKind.ASTERISK, "*"));
        SyntaxCheckerContext context = new SyntaxCheckerContext();
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("sum"));
    }

    @Test
    public void test04_countAsteriskAllowed() {
        SelectStatement stmt = singleEntitySelect();
        stmt.addSelectItem(aggItem(SelectItemType.COUNT, AggregateArgumentKind.ASTERISK, "*"));
        SyntaxCheckerContext context = new SyntaxCheckerContext();
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test05_countNumberAllowed() {
        SelectStatement stmt = singleEntitySelect();
        stmt.addSelectItem(aggItem(SelectItemType.COUNT, AggregateArgumentKind.NUMBER, "1"));
        SyntaxCheckerContext context = new SyntaxCheckerContext();
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test06_maxFieldAllowed() {
        SelectStatement stmt = singleEntitySelect();
        stmt.addSelectItem(aggItem(SelectItemType.MAX, AggregateArgumentKind.FIELD, "age"));
        SyntaxCheckerContext context = new SyntaxCheckerContext();
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test07_havingMaxNumberShouldError() {
        SelectStatement stmt = singleEntitySelect();
        HavingExpression having = new HavingExpression(LogicOperator.NULL);
        HavingConditionNode node = new HavingConditionNode();
        HavingAggregateExpression aggExpr = new HavingAggregateExpression(AggregateFunction.MAX, "1");
        aggExpr.setArgumentKind(AggregateArgumentKind.NUMBER);
        node.setLeftSide(aggExpr);
        having.addNode(node);
        stmt.setHavingExpression(having);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("max"));
    }

    @Test
    public void test08_havingCountAsteriskAllowed() {
        SelectStatement stmt = singleEntitySelect();
        HavingExpression having = new HavingExpression(LogicOperator.NULL);
        HavingConditionNode node = new HavingConditionNode();
        HavingAggregateExpression aggExpr = new HavingAggregateExpression(AggregateFunction.COUNT, "*");
        aggExpr.setArgumentKind(AggregateArgumentKind.ASTERISK);
        node.setLeftSide(aggExpr);
        having.addNode(node);
        stmt.setHavingExpression(having);

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    private SelectStatement singleEntitySelect() {
        SelectStatement stmt = new SelectStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", "user"));
        stmt.setFromClause(fromClause);
        return stmt;
    }

    private SelectItem aggItem(SelectItemType type, AggregateArgumentKind kind, String fieldName) {
        SelectItem item = new SelectItem();
        item.setType(type);
        item.setArgumentKind(kind);
        item.setFieldRef(new FieldReference(null, fieldName));
        return item;
    }
}
