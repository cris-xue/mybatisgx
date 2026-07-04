package com.mybatisgx.dsl.mgxql.checker.semantic;

import com.mybatisgx.dsl.mgxql.MgxqlSyntaxProcessor;
import com.mybatisgx.dsl.mgxql.checker.MgxqlSemanticCheckerChain;
import com.mybatisgx.dsl.mgxql.checker.MgxqlSyntaxCheckerChain;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.handler.EntityInfoHandler;
import com.mybatisgx.dsl.test.entity.User;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * DELETE/UPDATE 语义校验集成测试
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlDmlCheckerTest {

    private EntityInfoHandler entityInfoHandler;
    private MgxqlSyntaxProcessor processor;

    @Before
    public void setUp() {
        entityInfoHandler = new EntityInfoHandler();
        processor = new MgxqlSyntaxProcessor();
    }

    private EntityInfo getUserEntityInfo() {
        return entityInfoHandler.execute(User.class);
    }

    // ==================== EntityChecker 适配测试 ====================

    @Test
    public void test01_deleteNoFromClauseWithEntity_shouldPass() {
        EntityInfo entityInfo = getUserEntityInfo();
        MgxqlSemanticCheckerChain chain = new MgxqlSemanticCheckerChain();
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.DELETE);
        WhereClause whereClause = new WhereClause(new WhereExpression(LogicOperator.NULL));
        WhereConditionNode node = new WhereConditionNode();
        node.setFieldRef(new FieldReference(null, "id"));
        whereClause.getRootExpression().addNode(node);
        stmt.setWhereClause(whereClause);
        // EntityChecker 在无 FromClause 时确认 primaryEntityInfo 可用
        chain.check(stmt, entityInfo);
    }

    @Test
    public void test02_deleteNoFromClauseWithoutEntity_shouldError() {
        MgxqlSemanticCheckerChain chain = new MgxqlSemanticCheckerChain();
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.DELETE);
        WhereClause whereClause = new WhereClause(new WhereExpression(LogicOperator.NULL));
        WhereConditionNode node = new WhereConditionNode();
        node.setFieldRef(new FieldReference(null, "id"));
        whereClause.getRootExpression().addNode(node);
        stmt.setWhereClause(whereClause);
        try {
            chain.check(stmt, null);
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("缺少实体信息"));
        }
    }

    // ==================== DELETE 完整校验链测试 ====================

    @Test
    public void test10_deleteFieldNotExists_shouldError() {
        EntityInfo entityInfo = getUserEntityInfo();
        try {
            processor.executeAndCheck(
                    entityInfo, new MethodInfo(), null, MgxqlSourceType.METHOD_NAME,
                    "delete User where nonExistentField = :val");
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("语义校验失败"));
            Assert.assertTrue(e.getMessage().contains("nonExistentField"));
        }
    }

    @Test
    public void test11_deleteOperatorTypeMismatch_shouldError() {
        EntityInfo entityInfo = getUserEntityInfo();
        try {
            processor.executeAndCheck(
                    entityInfo, new MethodInfo(), null, MgxqlSourceType.METHOD_NAME,
                    "delete User where age like :age");
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("语义校验失败"));
            Assert.assertTrue(e.getMessage().contains("LIKE"));
        }
    }

    @Test
    public void test12_deleteNoWhere_shouldError() {
        MgxqlStatement stmt = new ModifyStatement();
        stmt.setCommandType(SqlCommandType.DELETE);
        MgxqlSyntaxCheckerChain chain = new MgxqlSyntaxCheckerChain();
        try {
            chain.check(stmt);
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("语法校验失败"));
            Assert.assertTrue(e.getMessage().contains("WHERE"));
        }
    }

    @Test
    public void test13_deleteValid_shouldPass() {
        EntityInfo entityInfo = getUserEntityInfo();
        processor.executeAndCheck(
                entityInfo, new MethodInfo(), null, MgxqlSourceType.METHOD_NAME,
                "delete User where name = :name");
    }

    @Test
    public void test14_deleteWithAliasPrefix_shouldError() {
        EntityInfo entityInfo = getUserEntityInfo();
        try {
            processor.executeAndCheck(
                    entityInfo, new MethodInfo(), null, MgxqlSourceType.METHOD_NAME,
                    "delete User where user.name = :name");
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("别名前缀"));
        }
    }

    // ==================== UPDATE 完整校验链测试 ====================

    @Test
    public void test20_updateFieldNotExists_shouldError() {
        EntityInfo entityInfo = getUserEntityInfo();
        try {
            processor.executeAndCheck(
                    entityInfo, new MethodInfo(), null, MgxqlSourceType.METHOD_NAME,
                    "update User where nonExistentField = :val");
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("语义校验失败"));
            Assert.assertTrue(e.getMessage().contains("nonExistentField"));
        }
    }

    @Test
    public void test21_updateOperatorTypeMismatch_shouldError() {
        EntityInfo entityInfo = getUserEntityInfo();
        try {
            processor.executeAndCheck(
                    entityInfo, new MethodInfo(), null, MgxqlSourceType.METHOD_NAME,
                    "update User where age like :age");
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("语义校验失败"));
            Assert.assertTrue(e.getMessage().contains("LIKE"));
        }
    }

    @Test
    public void test22_updateNoWhere_shouldError() {
        MgxqlStatement stmt = new ModifyStatement();
        stmt.setCommandType(SqlCommandType.UPDATE);
        MgxqlSyntaxCheckerChain chain = new MgxqlSyntaxCheckerChain();
        try {
            chain.check(stmt);
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("语法校验失败"));
            Assert.assertTrue(e.getMessage().contains("WHERE"));
        }
    }

    @Test
    public void test23_updateValid_shouldPass() {
        EntityInfo entityInfo = getUserEntityInfo();
        processor.executeAndCheck(
                entityInfo, new MethodInfo(), null, MgxqlSourceType.METHOD_NAME,
                "update User where name = :name");
    }

    @Test
    public void test24_updateWithAliasPrefix_shouldError() {
        EntityInfo entityInfo = getUserEntityInfo();
        try {
            processor.executeAndCheck(
                    entityInfo, new MethodInfo(), null, MgxqlSourceType.METHOD_NAME,
                    "update User where user.name = :name");
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("别名前缀"));
        }
    }

    // ==================== MgxqlCheckerChain 分支测试 ====================

    @Test
    public void test30_deleteShouldNotExecuteJoinRelationChecker() {
        EntityInfo entityInfo = getUserEntityInfo();
        processor.executeAndCheck(
                entityInfo, new MethodInfo(), null, MgxqlSourceType.METHOD_NAME,
                "delete User where name = :name");
    }
}
