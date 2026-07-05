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
 * AliasContext 单元测试，重点验证 resolveTableAlias、getNode、getFromEntity 方法
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
    public void test01_resolveTableAlias_withTreeNode() {
        AliasContext aliasContext = AliasContext.build(selectStatement, rootRelation);
        Assert.assertEquals("user_1_1", aliasContext.resolveTableAlias("u"));
        Assert.assertEquals("role_2_1", aliasContext.resolveTableAlias("r"));
    }

    @Test
    public void test02_resolveTableAlias_noTreeFallback() {
        AliasContext aliasContext = AliasContext.build(selectStatement, null);
        Assert.assertEquals("u", aliasContext.resolveTableAlias("u"));
        Assert.assertEquals("r", aliasContext.resolveTableAlias("r"));
    }

    @Test
    public void test03_resolveTableAlias_unknownAliasFallback() {
        AliasContext aliasContext = AliasContext.build(selectStatement, rootRelation);
        Assert.assertEquals("unknown", aliasContext.resolveTableAlias("unknown"));
    }

    @Test
    public void test04_resolveTableAlias_nullParam() {
        AliasContext aliasContext = AliasContext.build(selectStatement, rootRelation);
        Assert.assertNull(aliasContext.resolveTableAlias((String) null));
    }

    @Test
    public void test05_getNode_withTreeNode() {
        AliasContext aliasContext = AliasContext.build(selectStatement, rootRelation);
        ColumnEntityRelation userNode = aliasContext.getNode("u");
        Assert.assertNotNull(userNode);
        Assert.assertEquals("user_1_1", userNode.getTableNameAlias());
        ColumnEntityRelation roleNode = aliasContext.getNode("r");
        Assert.assertNotNull(roleNode);
        Assert.assertEquals("role_2_1", roleNode.getTableNameAlias());
    }

    @Test
    public void test06_getNode_noTreeReturnsNull() {
        AliasContext aliasContext = AliasContext.build(selectStatement, null);
        Assert.assertNull(aliasContext.getNode("u"));
        Assert.assertNull(aliasContext.getNode("r"));
    }

    @Test
    public void test07_getFromEntity_primaryEntity() {
        AliasContext aliasContext = AliasContext.build(selectStatement, rootRelation);
        FromEntity fromEntity = aliasContext.getFromEntity("u");
        Assert.assertNotNull(fromEntity);
        Assert.assertEquals("User", fromEntity.getEntityName());
        Assert.assertEquals("u", fromEntity.getAlias());
    }

    @Test
    public void test08_getFromEntity_joinEntity() {
        AliasContext aliasContext = AliasContext.build(selectStatement, rootRelation);
        FromEntity fromEntity = aliasContext.getFromEntity("r");
        Assert.assertNotNull(fromEntity);
        Assert.assertEquals("Role", fromEntity.getEntityName());
        Assert.assertEquals("r", fromEntity.getAlias());
    }

    @Test
    public void test09_getFromEntity_unknownAlias() {
        AliasContext aliasContext = AliasContext.build(selectStatement, rootRelation);
        Assert.assertNull(aliasContext.getFromEntity("unknown"));
    }

    // ===== resolveTableAlias(FromEntity) 重载测试 =====

    @Test
    public void test10_resolveTableAliasFromEntity_withAlias() {
        AliasContext aliasContext = AliasContext.build(selectStatement, rootRelation);
        FromEntity primaryEntity = aliasContext.getFromEntity("u");
        Assert.assertNotNull(primaryEntity);
        Assert.assertEquals("user_1_1", aliasContext.resolveTableAlias(primaryEntity));
        FromEntity joinEntity = aliasContext.getFromEntity("r");
        Assert.assertNotNull(joinEntity);
        Assert.assertEquals("role_2_1", aliasContext.resolveTableAlias(joinEntity));
    }

    @Test
    public void test11_resolveTableAliasFromEntity_noAliasUsesEntityName() {
        // 构造一个无 alias 的 FromEntity，应使用 entityName 作为 key
        SelectStatement stmt = new SelectStatement();
        FromClause fromClause = new FromClause();
        FromEntity entity = new FromEntity();
        entity.setEntityName("User");
        // 不设置 alias
        EntityInfo userEntityInfo = new EntityInfo.Builder().setTableName("t_user").setClazz(User.class).build();
        entity.setEntityInfo(userEntityInfo);
        fromClause.setPrimaryEntity(entity);
        stmt.setFromClause(fromClause);

        ColumnEntityRelation root = new ColumnEntityRelation();
        root.setEntityInfo(userEntityInfo);
        root.setTableNameAlias("user_1_1");

        AliasContext aliasContext = AliasContext.build(stmt, root);
        // 无 alias 时 resolveKey 返回 entityName "User"
        Assert.assertEquals("user_1_1", aliasContext.resolveTableAlias(entity));
    }

    @Test
    public void test12_resolveTableAliasFromEntity_nullParam() {
        AliasContext aliasContext = AliasContext.build(selectStatement, rootRelation);
        Assert.assertNull(aliasContext.resolveTableAlias((FromEntity) null));
    }

    @Test
    public void test13_resolveTableAliasFromEntity_noTreeFallback() {
        AliasContext aliasContext = AliasContext.build(selectStatement, null);
        FromEntity primaryEntity = aliasContext.getFromEntity("u");
        Assert.assertNotNull(primaryEntity);
        // 无树节点时回退返回原始别名
        Assert.assertEquals("u", aliasContext.resolveTableAlias(primaryEntity));
    }

    private static class User {}
    private static class Role {}
}
