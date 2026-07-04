package com.mybatisgx.dsl.mgxql.checker.semantic;

import com.mybatisgx.dsl.mgxql.checker.CheckerContext;
import com.mybatisgx.dsl.mgxql.checker.WhereFieldChecker;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.dsl.test.entity.User;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.handler.EntityInfoHandler;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * WHERE字段存在性校验器单元测试
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WhereFieldCheckerTest {

    private WhereFieldChecker checker;
    private EntityInfo userEntityInfo;

    @Before
    public void setUp() {
        checker = new WhereFieldChecker();
        EntityInfoHandler entityInfoHandler = new EntityInfoHandler();
        userEntityInfo = entityInfoHandler.execute(User.class);
    }

    @Test
    public void test01_whereFieldExists_shouldPassAndSetColumnInfo() {
        SelectStatement stmt = new SelectStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        WhereClause whereClause = new WhereClause(new WhereExpression(LogicOperator.NULL));
        WhereConditionNode node = new WhereConditionNode();
        node.setFieldRef(new FieldReference(null, "name"));
        whereClause.getRootExpression().addNode(node);
        stmt.setWhereClause(whereClause);

        CheckerContext context = new CheckerContext(userEntityInfo);
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
        Assert.assertNotNull(node.getColumnInfo());
    }

    @Test
    public void test02_whereFieldNotExists_shouldError() {
        SelectStatement stmt = new SelectStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        WhereClause whereClause = new WhereClause(new WhereExpression(LogicOperator.NULL));
        WhereConditionNode node = new WhereConditionNode();
        node.setFieldRef(new FieldReference(null, "nonExistentField"));
        whereClause.getRootExpression().addNode(node);
        stmt.setWhereClause(whereClause);

        CheckerContext context = new CheckerContext(userEntityInfo);
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("nonExistentField"));
    }

    @Test
    public void test03_deleteWhereFieldExists_shouldPass() {
        MgxqlStatement stmt = new ModifyStatement();
        stmt.setCommandType(SqlCommandType.DELETE);
        WhereClause whereClause = new WhereClause(new WhereExpression(LogicOperator.NULL));
        WhereConditionNode node = new WhereConditionNode();
        node.setFieldRef(new FieldReference(null, "name"));
        whereClause.getRootExpression().addNode(node);
        stmt.setWhereClause(whereClause);

        CheckerContext context = new CheckerContext(userEntityInfo);
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test04_nestedConditionFieldNotExists_shouldError() {
        SelectStatement stmt = new SelectStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        WhereExpression rootExpr = new WhereExpression(LogicOperator.AND);
        WhereConditionNode normalNode = new WhereConditionNode();
        normalNode.setFieldRef(new FieldReference(null, "name"));
        rootExpr.addNode(normalNode);

        WhereExpression nestedExpr = new WhereExpression(LogicOperator.NULL);
        WhereConditionNode nestedNode = new WhereConditionNode();
        nestedNode.setFieldRef(new FieldReference(null, "nonExistentField"));
        nestedExpr.addNode(nestedNode);

        WhereConditionNode nestedWrapper = new WhereConditionNode();
        nestedWrapper.setSubExpression(nestedExpr);
        rootExpr.addNode(nestedWrapper);

        stmt.setWhereClause(new WhereClause(rootExpr));

        CheckerContext context = new CheckerContext(userEntityInfo);
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("nonExistentField"));
    }
}
