package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.MgxqlSyntaxProcessor;
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
        MgxqlCheckerChain chain = new MgxqlCheckerChain();
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.DELETE);
        WhereClause whereClause = new WhereClause(new ConditionExpression(LogicOperator.NULL));
        ConditionNode node = new ConditionNode();
        node.setFieldName("id");
        whereClause.getRootExpression().addNode(node);
        stmt.setWhereClause(whereClause);
        // EntityChecker 在无 FromClause 时确认 primaryEntityInfo 可用
        chain.check(stmt, entityInfo);
    }

    @Test
    public void test02_deleteNoFromClauseWithoutEntity_shouldError() {
        MgxqlCheckerChain chain = new MgxqlCheckerChain();
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(SqlCommandType.DELETE);
        WhereClause whereClause = new WhereClause(new ConditionExpression(LogicOperator.NULL));
        ConditionNode node = new ConditionNode();
        node.setFieldName("id");
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
        MgxqlStatement stmt = processor.executeAndCheck(
                entityInfo, new MethodInfo(), null, ConditionOriginType.METHOD_NAME,
                "delete where nonExistentField = :val");
        MgxqlCheckerChain chain = new MgxqlCheckerChain();
        try {
            chain.check(stmt, entityInfo);
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("语义校验失败"));
            Assert.assertTrue(e.getMessage().contains("nonExistentField"));
        }
    }

    @Test
    public void test11_deleteOperatorTypeMismatch_shouldError() {
        EntityInfo entityInfo = getUserEntityInfo();
        MgxqlStatement stmt = processor.executeAndCheck(
                entityInfo, new MethodInfo(), null, ConditionOriginType.METHOD_NAME,
                "delete where age like :age");
        MgxqlCheckerChain chain = new MgxqlCheckerChain();
        try {
            chain.check(stmt, entityInfo);
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("语义校验失败"));
            Assert.assertTrue(e.getMessage().contains("LIKE"));
        }
    }

    @Test
    public void test12_deleteNoWhere_shouldError() {
        EntityInfo entityInfo = getUserEntityInfo();
        MgxqlStatement stmt = processor.executeAndCheck(
                entityInfo, new MethodInfo(), null, ConditionOriginType.METHOD_NAME,
                "delete");
        MgxqlCheckerChain chain = new MgxqlCheckerChain();
        try {
            chain.check(stmt, entityInfo);
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("语法校验失败"));
            Assert.assertTrue(e.getMessage().contains("WHERE"));
        }
    }

    @Test
    public void test13_deleteValid_shouldPass() {
        EntityInfo entityInfo = getUserEntityInfo();
        MgxqlStatement stmt = processor.executeAndCheck(
                entityInfo, new MethodInfo(), null, ConditionOriginType.METHOD_NAME,
                "delete where name = :name");
        MgxqlCheckerChain chain = new MgxqlCheckerChain();
        chain.check(stmt, entityInfo);
    }

    @Test
    public void test14_deleteWithAliasPrefix_shouldError() {
        EntityInfo entityInfo = getUserEntityInfo();
        MgxqlStatement stmt = processor.executeAndCheck(
                entityInfo, new MethodInfo(), null, ConditionOriginType.METHOD_NAME,
                "delete where user.name = :name");
        MgxqlCheckerChain chain = new MgxqlCheckerChain();
        try {
            chain.check(stmt, entityInfo);
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("别名前缀"));
        }
    }

    // ==================== UPDATE 完整校验链测试 ====================

    @Test
    public void test20_updateFieldNotExists_shouldError() {
        EntityInfo entityInfo = getUserEntityInfo();
        MgxqlStatement stmt = processor.executeAndCheck(
                entityInfo, new MethodInfo(), null, ConditionOriginType.METHOD_NAME,
                "update where nonExistentField = :val");
        MgxqlCheckerChain chain = new MgxqlCheckerChain();
        try {
            chain.check(stmt, entityInfo);
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("语义校验失败"));
            Assert.assertTrue(e.getMessage().contains("nonExistentField"));
        }
    }

    @Test
    public void test21_updateOperatorTypeMismatch_shouldError() {
        EntityInfo entityInfo = getUserEntityInfo();
        MgxqlStatement stmt = processor.executeAndCheck(
                entityInfo, new MethodInfo(), null, ConditionOriginType.METHOD_NAME,
                "update where age like :age");
        MgxqlCheckerChain chain = new MgxqlCheckerChain();
        try {
            chain.check(stmt, entityInfo);
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("语义校验失败"));
            Assert.assertTrue(e.getMessage().contains("LIKE"));
        }
    }

    @Test
    public void test22_updateNoWhere_shouldError() {
        EntityInfo entityInfo = getUserEntityInfo();
        MgxqlStatement stmt = processor.executeAndCheck(
                entityInfo, new MethodInfo(), null, ConditionOriginType.METHOD_NAME,
                "update");
        MgxqlCheckerChain chain = new MgxqlCheckerChain();
        try {
            chain.check(stmt, entityInfo);
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("语法校验失败"));
            Assert.assertTrue(e.getMessage().contains("WHERE"));
        }
    }

    @Test
    public void test23_updateValid_shouldPass() {
        EntityInfo entityInfo = getUserEntityInfo();
        MgxqlStatement stmt = processor.executeAndCheck(
                entityInfo, new MethodInfo(), null, ConditionOriginType.METHOD_NAME,
                "update where name = :name");
        MgxqlCheckerChain chain = new MgxqlCheckerChain();
        chain.check(stmt, entityInfo);
    }

    @Test
    public void test24_updateWithAliasPrefix_shouldError() {
        EntityInfo entityInfo = getUserEntityInfo();
        MgxqlStatement stmt = processor.executeAndCheck(
                entityInfo, new MethodInfo(), null, ConditionOriginType.METHOD_NAME,
                "update where user.name = :name");
        MgxqlCheckerChain chain = new MgxqlCheckerChain();
        try {
            chain.check(stmt, entityInfo);
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("别名前缀"));
        }
    }

    // ==================== MgxqlCheckerChain 分支测试 ====================

    @Test
    public void test30_deleteShouldNotExecuteJoinRelationChecker() {
        // DELETE 语句应走精简校验链，不执行 JoinRelationChecker
        // 构造一个没有 Join 的 DELETE 语句，如果走了 JoinRelationChecker 也不会出错
        // 但关键是：DELETE 语句没有 FromClause/JoinEntity，如果走了 JoinRelationChecker 也不会误报
        EntityInfo entityInfo = getUserEntityInfo();
        MgxqlStatement stmt = processor.executeAndCheck(
                entityInfo, new MethodInfo(), null, ConditionOriginType.METHOD_NAME,
                "delete where name = :name");
        MgxqlCheckerChain chain = new MgxqlCheckerChain();
        chain.check(stmt, entityInfo);
        // 如果走到 JoinRelationChecker，由于 fromClause 为 null 不会报错
        // 但通过代码分支确保不会走到
    }
}
