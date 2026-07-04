package com.mybatisgx.dsl.mgxql.checker.semantic;

import com.mybatisgx.dsl.mgxql.checker.CheckerContext;
import com.mybatisgx.dsl.mgxql.checker.EntityChecker;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.dsl.test.entity.Role;
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
 * 实体存在性校验器单元测试
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EntityCheckerTest {

    private EntityChecker checker;
    private EntityInfoHandler entityInfoHandler;
    private EntityInfo userEntityInfo;
    private EntityInfo roleEntityInfo;

    @Before
    public void setUp() {
        checker = new EntityChecker();
        entityInfoHandler = new EntityInfoHandler();
        userEntityInfo = entityInfoHandler.execute(User.class);
        roleEntityInfo = entityInfoHandler.execute(Role.class);
    }

    @Test
    public void test01_deleteWithEntityInfo_shouldPass() {
        MgxqlStatement stmt = new ModifyStatement();
        stmt.setCommandType(SqlCommandType.DELETE);
        CheckerContext context = new CheckerContext(userEntityInfo);
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test02_deleteWithoutEntityInfo_shouldError() {
        MgxqlStatement stmt = new ModifyStatement();
        stmt.setCommandType(SqlCommandType.DELETE);
        CheckerContext context = new CheckerContext(null);
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("缺少实体信息"));
    }

    @Test
    public void test03_updateWithEntityInfo_shouldPass() {
        MgxqlStatement stmt = new ModifyStatement();
        stmt.setCommandType(SqlCommandType.UPDATE);
        CheckerContext context = new CheckerContext(userEntityInfo);
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test04_selectSingleEntity_shouldResolve() {
        SelectStatement stmt = new SelectStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", null));
        stmt.setFromClause(fromClause);

        CheckerContext context = new CheckerContext(userEntityInfo);
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test05_selectNonExistentEntity_shouldError() {
        SelectStatement stmt = new SelectStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("NonExistent", null));
        stmt.setFromClause(fromClause);

        CheckerContext context = new CheckerContext(userEntityInfo);
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        Assert.assertTrue(context.getErrors().get(0).contains("NonExistent"));
    }

    @Test
    public void test06_selectWithJoin_shouldRegisterAlias() {
        SelectStatement stmt = new SelectStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", "user"));
        fromClause.addJoinEntity(new JoinEntity("Role", "role", JoinType.LEFT));
        stmt.setFromClause(fromClause);

        CheckerContext context = new CheckerContext(userEntityInfo);
        context.registerEntityName("Role", roleEntityInfo);
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
        Assert.assertNotNull(context.getEntityInfoByAlias("user"));
        Assert.assertNotNull(context.getEntityInfoByAlias("role"));
    }
}
