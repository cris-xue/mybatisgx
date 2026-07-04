package com.mybatisgx.relation.select.mgxql.manytomany.test;

import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.EntityRelationTree;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.relation.select.mgxql.manytomany.dto.*;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * 多对多投影 DTO 嵌套实体关系树构建测试
 *
 * @author ccxuef
 * @date 2026/7/4
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProjectionMultiLevelTreeTest {

    private static final String DAO_NS = "com.mybatisgx.relation.select.mgxql.manytomany.dao.MgxqlManyToManyJoinDao";

    private static MybatisgxConfiguration configuration;
    private static MapperInfo mapperInfo;

    @BeforeClass
    public static void setUp() {
        configuration = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.manytomany.entity"},
                new String[]{"com.mybatisgx.relation.select.mgxql.manytomany.dao"}
        );
        mapperInfo = configuration.getMethodInfo(DAO_NS + ".findFlatProjection").getMapperInfo();
    }

    /**
     * test01: 单层投影 DTO（UserFlatProjection 无嵌套字段）— 树只有根节点无 composites
     */
    @Test
    public void test01_flatProjectionNoComposites() {
        MethodInfo methodInfo = configuration.getMethodInfo(DAO_NS + ".findFlatProjection");
        Class<?> returnType = methodInfo.getMethodReturnInfo().getType();
        EntityRelationTree tree = mapperInfo.getEntityRelationTree(returnType);

        Assert.assertNotNull("单层投影树不应为 null", tree);
        Assert.assertEquals("层级应为 1", 1, tree.getLevel());
        Assert.assertEquals("索引应为 1", 1, tree.getIndex());
        Assert.assertTrue("单层投影不应有 composites", tree.getComposites().isEmpty());
        Assert.assertEquals("entityInfo 的 clazz 应为 UserFlatProjection", UserFlatProjection.class, tree.getEntityInfo().getClazz());
    }

    /**
     * test02: 一层嵌套投影（UserProjection 包含 List<RoleProjection> roleList）
     */
    @Test
    public void test02_oneLevelNestingProjection() {
        MethodInfo methodInfo = configuration.getMethodInfo(DAO_NS + ".findOneLevelProjection");
        Class<?> returnType = methodInfo.getMethodReturnInfo().getType();
        EntityRelationTree tree = mapperInfo.getEntityRelationTree(returnType);

        Assert.assertNotNull("一层嵌套投影树不应为 null", tree);
        Assert.assertEquals("根节点层级应为 1", 1, tree.getLevel());
        Assert.assertEquals("根节点应有 1 个 composite（roleList）", 1, tree.getComposites().size());
        Assert.assertEquals("根节点 entityInfo 的 clazz 应为 UserProjection", UserProjection.class, tree.getEntityInfo().getClazz());

        EntityRelationTree roleTree = tree.getComposites().get(0);
        Assert.assertEquals("Role 节点层级应为 2", 2, roleTree.getLevel());
        Assert.assertEquals("Role 节点 entityInfo 的 clazz 应为 RoleProjection", RoleProjection.class, roleTree.getEntityInfo().getClazz());
        Assert.assertNotNull("Role 节点的 columnInfo 不应为 null（关系字段 roleList）", roleTree.getColumnInfo());
        Assert.assertEquals("columnInfo 的 javaColumnName 应为 roleList", "roleList", roleTree.getColumnInfo().getJavaColumnName());
        // 多对多场景下有中间表
        Assert.assertNotNull("多对多投影子树的 middleEntityInfo 不应为 null", roleTree.getMiddleEntityInfo());
    }

    /**
     * test03: 跳层匹配（UserSkipProjection 直接包含 List<MenuProjection> menuList，跳过 Role）
     * 投影树 level/index 从完整实体关系树获取，保持与完整树一致
     */
    @Test
    public void test03_skipLevelProjection() {
        MethodInfo methodInfo = configuration.getMethodInfo(DAO_NS + ".findSkipLevelProjection");
        Class<?> returnType = methodInfo.getMethodReturnInfo().getType();
        EntityRelationTree tree = mapperInfo.getEntityRelationTree(returnType);

        Assert.assertNotNull("跳层投影树不应为 null", tree);
        Assert.assertEquals("根节点层级应为 1", 1, tree.getLevel());
        Assert.assertEquals("根节点应有 1 个 composite（menuList）", 1, tree.getComposites().size());
        Assert.assertEquals("根节点 entityInfo 的 clazz 应为 UserSkipProjection", UserSkipProjection.class, tree.getEntityInfo().getClazz());

        EntityRelationTree menuTree = tree.getComposites().get(0);
        // Menu 在完整树中是 level 3（User→Role→Menu），跳层不影响 level
        Assert.assertEquals("Menu 节点层级应为 3（与完整树一致，跳层不影响 level）", 3, menuTree.getLevel());
        Assert.assertEquals("Menu 节点 entityInfo 的 clazz 应为 MenuProjection", MenuProjection.class, menuTree.getEntityInfo().getClazz());
        Assert.assertNotNull("Menu 节点的 columnInfo 不应为 null", menuTree.getColumnInfo());
        Assert.assertEquals("columnInfo 的 javaColumnName 应为 menuList", "menuList", menuTree.getColumnInfo().getJavaColumnName());
        // 多对多场景下有中间表
        Assert.assertNotNull("跳层多对多投影子树的 middleEntityInfo 不应为 null", menuTree.getMiddleEntityInfo());
    }

    /**
     * test04: 字段名不匹配（UserUnknownFieldProjection 包含 unknownList 字段）
     */
    @Test
    public void test04_unknownFieldIgnored() {
        MethodInfo methodInfo = configuration.getMethodInfo(DAO_NS + ".findUnknownFieldProjection");
        Class<?> returnType = methodInfo.getMethodReturnInfo().getType();
        EntityRelationTree tree = mapperInfo.getEntityRelationTree(returnType);

        Assert.assertNotNull("字段不匹配投影树不应为 null", tree);
        Assert.assertEquals("根节点层级应为 1", 1, tree.getLevel());
        Assert.assertTrue("unknownList 字段不匹配，不应产生 composites", tree.getComposites().isEmpty());
        Assert.assertEquals("根节点 entityInfo 的 clazz 应为 UserUnknownFieldProjection",
                UserUnknownFieldProjection.class, tree.getEntityInfo().getClazz());
    }
}
