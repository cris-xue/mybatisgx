package com.mybatisgx.dsl.mgxql.checker.semantic;

import com.mybatisgx.dsl.mgxql.checker.CheckerContext;
import com.mybatisgx.dsl.mgxql.checker.JoinRelationChecker;
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
 * JOIN关系校验器单元测试
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JoinRelationCheckerTest {

    private JoinRelationChecker checker;
    private EntityInfo userEntityInfo;
    private EntityInfo roleEntityInfo;

    @Before
    public void setUp() {
        checker = new JoinRelationChecker();
        EntityInfoHandler entityInfoHandler = new EntityInfoHandler();
        userEntityInfo = entityInfoHandler.execute(User.class);
        roleEntityInfo = entityInfoHandler.execute(Role.class);
    }

    private SelectStatement buildSelectWithJoin(String primaryEntity, String primaryAlias,
                                                 String joinEntity, String joinAlias) {
        SelectStatement stmt = new SelectStatement();
        stmt.setCommandType(SqlCommandType.SELECT);
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity(primaryEntity, primaryAlias));
        fromClause.addJoinEntity(new JoinEntity(joinEntity, joinAlias, JoinType.LEFT));
        stmt.setFromClause(fromClause);
        return stmt;
    }

    @Test
    public void test01_noJoin_shouldPass() {
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
    public void test02_joinWithRelation_shouldPass() {
        SelectStatement stmt = buildSelectWithJoin("User", "user", "Role", "role");

        CheckerContext context = new CheckerContext(userEntityInfo);
        context.registerAlias("role", roleEntityInfo);
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test03_joinWithoutRegisteredAlias_shouldSkip() {
        SelectStatement stmt = buildSelectWithJoin("User", "user", "Role", "role");

        CheckerContext context = new CheckerContext(userEntityInfo);
        // 不注册 role 别名，JoinRelationChecker 应跳过
        checker.check(stmt, context);
        Assert.assertFalse(context.hasErrors());
    }

    @Test
    public void test04_joinWithNoRelation_shouldError() {
        SelectStatement stmt = buildSelectWithJoin("User", "user", "User", "user2");

        CheckerContext context = new CheckerContext(userEntityInfo);
        // 注册 user2 别名指向 User 自身（User 与 User 无关联关系）
        context.registerAlias("user2", userEntityInfo);
        checker.check(stmt, context);
        Assert.assertTrue(context.hasErrors());
        boolean hasRelationError = false;
        for (String error : context.getErrors()) {
            if (error.contains("不存在关联关系")) {
                hasRelationError = true;
            }
        }
        Assert.assertTrue(hasRelationError);
    }
}
