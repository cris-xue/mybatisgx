package com.mybatisgx.dsl.mgxql.checker.semantic;

import com.mybatisgx.dsl.mgxql.checker.CheckerContext;
import com.mybatisgx.dsl.mgxql.checker.OperatorTypeChecker;
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
 * 运算符类型匹配校验器单元测试
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OperatorTypeCheckerTest {

    private OperatorTypeChecker checker;
    private EntityInfo userEntityInfo;

    @Before
    public void setUp() {
        checker = new OperatorTypeChecker();
        EntityInfoHandler entityInfoHandler = new EntityInfoHandler();
        userEntityInfo = entityInfoHandler.execute(User.class);
    }

    private MgxqlStatement buildStmtWithWhere(String fieldName, ComparisonOperator operator) {
        MgxqlStatement stmt = new SelectStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        WhereClause whereClause = new WhereClause(new WhereExpression(LogicOperator.NULL));
        WhereConditionNode node = new WhereConditionNode();
        node.setFieldRef(new FieldReference(null, fieldName));
        node.setOperator(operator);
        whereClause.getRootExpression().addNode(node);
        stmt.setWhereClause(whereClause);
        return stmt;
    }

    @Test
    public void test01_likeOnString_shouldPass() {
        MgxqlStatement stmt = buildStmtWithWhere("name", ComparisonOperator.LIKE);
        CheckerContext context = new CheckerContext(userEntityInfo);
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test02_likeOnInteger_shouldError() {
        MgxqlStatement stmt = buildStmtWithWhere("age", ComparisonOperator.LIKE);
        CheckerContext context = new CheckerContext(userEntityInfo);
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        boolean hasLikeError = false;
        for (String error : context.getErrors()) {
            if (error.contains("LIKE") && error.contains("age")) {
                hasLikeError = true;
            }
        }
        Assert.assertTrue(hasLikeError);
    }

    @Test
    public void test03_betweenOnInteger_shouldPass() {
        MgxqlStatement stmt = buildStmtWithWhere("age", ComparisonOperator.BETWEEN);
        CheckerContext context = new CheckerContext(userEntityInfo);
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test04_inOnInteger_shouldPass() {
        MgxqlStatement stmt = buildStmtWithWhere("age", ComparisonOperator.IN);
        CheckerContext context = new CheckerContext(userEntityInfo);
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test05_eqOnString_shouldPass() {
        MgxqlStatement stmt = buildStmtWithWhere("name", ComparisonOperator.EQ);
        CheckerContext context = new CheckerContext(userEntityInfo);
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }
}
