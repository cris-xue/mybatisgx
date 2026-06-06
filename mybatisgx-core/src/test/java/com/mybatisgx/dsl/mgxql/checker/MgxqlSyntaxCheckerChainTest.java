package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.exception.MybatisgxException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * 语法校验链集成测试
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlSyntaxCheckerChainTest {

    private MgxqlSyntaxCheckerChain chain;

    @Before
    public void setUp() {
        chain = new MgxqlSyntaxCheckerChain();
    }

    @Test
    public void test01_validSingleEntityNoAlias() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(org.apache.ibatis.mapping.SqlCommandType.SELECT);
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN_ALL);
        item.setFieldName("*");
        stmt.addSelectItem(item);
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", null));
        stmt.setFromClause(fromClause);

        // 单实体裸字段，不应报错
        chain.check(stmt);
    }

    @Test
    public void test02_validMultiEntityWithAliases() {
        MgxqlStatement stmt = buildValidMultiEntityStmt();

        // 多实体全部合规，不应报错
        chain.check(stmt);
    }

    @Test(expected = MybatisgxException.class)
    public void test03_multiEntityMissingAlias() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(org.apache.ibatis.mapping.SqlCommandType.SELECT);
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN_ALL);
        item.setEntityAlias("user");
        item.setFieldName("*");
        stmt.addSelectItem(item);
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", "user"));
        fromClause.addJoinEntity(new JoinEntity("Role", null, JoinType.LEFT));
        stmt.setFromClause(fromClause);

        // JOIN实体无别名，应报错
        chain.check(stmt);
    }

    @Test(expected = MybatisgxException.class)
    public void test04_multiEntityBareField() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(org.apache.ibatis.mapping.SqlCommandType.SELECT);
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN);
        item.setFieldName("id");
        stmt.addSelectItem(item);
        setMultiEntityFrom(stmt);

        // 多实体裸字段，应报错
        chain.check(stmt);
    }

    @Test
    public void test05_syntaxCheckFailsWithCorrectMessage() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(org.apache.ibatis.mapping.SqlCommandType.SELECT);
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN);
        item.setFieldName("id");
        stmt.addSelectItem(item);
        setMultiEntityFrom(stmt);

        try {
            chain.check(stmt);
            Assert.fail("Expected MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertTrue(e.getMessage().contains("MGXQL语法校验失败"));
        }
    }

    private MgxqlStatement buildValidMultiEntityStmt() {
        MgxqlStatement stmt = new MgxqlStatement();
        stmt.setCommandType(org.apache.ibatis.mapping.SqlCommandType.SELECT);
        SelectItem item = new SelectItem();
        item.setType(SelectItemType.COLUMN);
        item.setEntityAlias("user");
        item.setFieldName("id");
        stmt.addSelectItem(item);
        setMultiEntityFrom(stmt);
        return stmt;
    }

    private void setMultiEntityFrom(MgxqlStatement stmt) {
        FromClause fromClause = new FromClause();
        fromClause.setPrimaryEntity(new FromEntity("User", "user"));
        JoinEntity join = new JoinEntity("Role", "role", JoinType.LEFT);
        join.setOnLeftAlias("user");
        join.setOnRightAlias("role");
        fromClause.addJoinEntity(join);
        stmt.setFromClause(fromClause);
    }
}
