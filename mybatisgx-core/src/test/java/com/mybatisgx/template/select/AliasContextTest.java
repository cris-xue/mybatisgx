package com.mybatisgx.template.select;

import com.mybatisgx.dsl.mgxql.model.FromClause;
import com.mybatisgx.dsl.mgxql.model.FromEntity;
import com.mybatisgx.dsl.mgxql.model.JoinEntity;
import com.mybatisgx.dsl.mgxql.model.SelectStatement;
import com.mybatisgx.model.ColumnEntityRelation;
import com.mybatisgx.model.EntityInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * AliasContext 单元测试，重点验证 resolveDbTableAlias 方法
 *
 * @author 薛承城
 * @date 2026/7/5
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AliasContextTest {

    private SelectStatement selectStatement;
    private ColumnEntityRelation rootRelation;

    @Before
    public void setUp() {
        selectStatement = new SelectStatement();
        FromClause fromClause = new FromClause();
        FromEntity primaryEntity = new FromEntity();
        primaryEntity.setEntityName("User");
        primaryEntity.setAlias("u");
        EntityInfo userEntityInfo = new EntityInfo.Builder().setTableName("t_user").setClazz(User.class).build();
        primaryEntity.setEntityInfo(userEntityInfo);
        fromClause.setPrimaryEntity(primaryEntity);

        JoinEntity joinEntity = new JoinEntity();
        joinEntity.setEntityName("Role");
        joinEntity.setAlias("r");
        EntityInfo roleEntityInfo = new EntityInfo.Builder().setTableName("t_role").setClazz(Role.class).build();
        joinEntity.setEntityInfo(roleEntityInfo);
        joinEntity.setOnLeftAlias("u");
        fromClause.addJoinEntity(joinEntity);

        selectStatement.setFromClause(fromClause);

        rootRelation = new ColumnEntityRelation();
        rootRelation.setEntityInfo(userEntityInfo);
        rootRelation.setTableNameAlias("user_1_1");

        ColumnEntityRelation roleNode = new ColumnEntityRelation();
        roleNode.setEntityInfo(roleEntityInfo);
        roleNode.setTableNameAlias("role_2_1");
        rootRelation.addComposites(roleNode);
    }

    @Test
    public void test01_resolveDbTableAlias_withTreeNode() {
        AliasContext aliasContext = AliasContext.build(selectStatement, rootRelation);
        Assert.assertEquals("user_1_1", aliasContext.resolveDbTableAlias("u"));
        Assert.assertEquals("role_2_1", aliasContext.resolveDbTableAlias("r"));
    }

    @Test
    public void test02_resolveDbTableAlias_noTreeFallback() {
        AliasContext aliasContext = AliasContext.build(selectStatement, null);
        Assert.assertEquals("u", aliasContext.resolveDbTableAlias("u"));
        Assert.assertEquals("r", aliasContext.resolveDbTableAlias("r"));
    }

    @Test
    public void test03_resolveDbTableAlias_unknownAliasFallback() {
        AliasContext aliasContext = AliasContext.build(selectStatement, rootRelation);
        Assert.assertEquals("unknown", aliasContext.resolveDbTableAlias("unknown"));
    }

    @Test
    public void test04_resolveDbTableAlias_nullParam() {
        AliasContext aliasContext = AliasContext.build(selectStatement, rootRelation);
        Assert.assertNull(aliasContext.resolveDbTableAlias(null));
    }

    private static class User {}
    private static class Role {}
}
