package com.mybatisgx.relation.select.mgxql.onetoone.test;

import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.EntityRelationTree;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.projection.dao.UserDao;
import com.mybatisgx.projection.dto.UserDto;
import com.mybatisgx.relation.select.mgxql.onetoone.dto.*;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * 投影 DTO 多层嵌套实体关系树构建测试
 *
 * @author ccxuef
 * @date 2026/7/1
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProjectionMultiLevelTreeTest {

    private static final String OTO_DAO_NAMESPACE = "com.mybatisgx.relation.select.mgxql.onetoone.dao.MgxqlJoinDao";
    private static final String PROJ_DAO_NAMESPACE = "com.mybatisgx.projection.dao.UserDao";

    private static MybatisgxConfiguration otoConfiguration;
    private static MapperInfo otoMapperInfo;
    private static MybatisgxConfiguration projConfiguration;
    private static MapperInfo projMapperInfo;

    @BeforeClass
    public static void setUp() {
        otoConfiguration = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.select.mgxql.onetoone.dao"}
        );
        otoMapperInfo = otoConfiguration.getMethodInfo(OTO_DAO_NAMESPACE + ".findJoinList1").getMapperInfo();

        projConfiguration = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.projection.entity"},
                new String[]{"com.mybatisgx.projection.dao"}
        );
        projMapperInfo = projConfiguration.getMethodInfo(PROJ_DAO_NAMESPACE + ".findByName").getMapperInfo();
    }

    /**
     * test01: 单层投影 DTO（UserFlatProjection 无嵌套字段）— 树只有根节点无 composites
     */
    @Test
    public void test01_flatProjectionNoComposites() {
        MethodInfo methodInfo = otoConfiguration.getMethodInfo(OTO_DAO_NAMESPACE + ".findFlatProjection");
        Class<?> returnType = methodInfo.getMethodReturnInfo().getType();
        EntityRelationTree tree = otoMapperInfo.getEntityRelationTree(returnType);

        Assert.assertNotNull("单层投影树不应为 null", tree);
        Assert.assertEquals("层级应为 1", 1, tree.getLevel());
        Assert.assertEquals("索引应为 1", 1, tree.getIndex());
        Assert.assertTrue("单层投影不应有 composites", tree.getComposites().isEmpty());
        Assert.assertEquals("entityInfo 的 clazz 应为 UserFlatProjection", UserFlatProjection.class, tree.getEntityInfo().getClazz());
    }

    /**
     * test02: 一层嵌套投影（UserOneLevelProjection 包含 UserDetailProjection userDetail）
     */
    @Test
    public void test02_oneLevelNestingProjection() {
        MethodInfo methodInfo = otoConfiguration.getMethodInfo(OTO_DAO_NAMESPACE + ".findOneLevelProjection");
        Class<?> returnType = methodInfo.getMethodReturnInfo().getType();
        EntityRelationTree tree = otoMapperInfo.getEntityRelationTree(returnType);

        Assert.assertNotNull("一层嵌套投影树不应为 null", tree);
        Assert.assertEquals("根节点层级应为 1", 1, tree.getLevel());
        Assert.assertEquals("根节点应有 1 个 composite", 1, tree.getComposites().size());
        Assert.assertEquals("根节点 entityInfo 的 clazz 应为 UserOneLevelProjection", UserOneLevelProjection.class, tree.getEntityInfo().getClazz());

        EntityRelationTree detailTree = tree.getComposites().get(0);
        Assert.assertEquals("子节点层级应为 2", 2, detailTree.getLevel());
        Assert.assertEquals("子节点 entityInfo 的 clazz 应为 UserDetailProjection", UserDetailProjection.class, detailTree.getEntityInfo().getClazz());
        Assert.assertNotNull("子节点的 columnInfo 不应为 null（关系字段 userDetail）", detailTree.getColumnInfo());
        Assert.assertEquals("columnInfo 的 javaColumnName 应为 userDetail", "userDetail", detailTree.getColumnInfo().getJavaColumnName());
        Assert.assertNull("投影子树的 middleEntityInfo 应为 null", detailTree.getMiddleEntityInfo());
        // 二层路径链下 UserDetailItem1 不在路径链中，userDetailItem1 应被忽略
        Assert.assertTrue("UserDetailProjection 在二层路径链下不应有 composites", detailTree.getComposites().isEmpty());
    }

    /**
     * test03: 多层嵌套投影（UserMultiLevelProjection → UserDetailProjection → UserDetailItem1Projection → UserDetailItem2Projection）
     */
    @Test
    public void test03_multiLevelNestingProjection() {
        MethodInfo methodInfo = otoConfiguration.getMethodInfo(OTO_DAO_NAMESPACE + ".findMultiLevelProjection");
        Class<?> returnType = methodInfo.getMethodReturnInfo().getType();
        EntityRelationTree tree = otoMapperInfo.getEntityRelationTree(returnType);

        Assert.assertNotNull("多层嵌套投影树不应为 null", tree);
        Assert.assertEquals("根节点层级应为 1", 1, tree.getLevel());
        Assert.assertEquals("根节点应有 1 个 composite（userDetail）", 1, tree.getComposites().size());
        Assert.assertEquals("根节点 entityInfo 的 clazz 应为 UserMultiLevelProjection", UserMultiLevelProjection.class, tree.getEntityInfo().getClazz());

        // Level 2: UserDetailProjection
        EntityRelationTree detailTree = tree.getComposites().get(0);
        Assert.assertEquals("UserDetail 节点层级应为 2", 2, detailTree.getLevel());
        Assert.assertEquals("UserDetail 节点 entityInfo 的 clazz 应为 UserDetailProjection", UserDetailProjection.class, detailTree.getEntityInfo().getClazz());
        Assert.assertEquals("UserDetail 节点应有 1 个 composite（userDetailItem1）", 1, detailTree.getComposites().size());

        // Level 3: UserDetailItem1Projection
        EntityRelationTree item1Tree = detailTree.getComposites().get(0);
        Assert.assertEquals("UserDetailItem1 节点层级应为 3", 3, item1Tree.getLevel());
        Assert.assertEquals("UserDetailItem1 节点 entityInfo 的 clazz 应为 UserDetailItem1Projection", UserDetailItem1Projection.class, item1Tree.getEntityInfo().getClazz());
        Assert.assertEquals("UserDetailItem1 节点应有 1 个 composite（userDetailItem2）", 1, item1Tree.getComposites().size());

        // Level 4: UserDetailItem2Projection
        EntityRelationTree item2Tree = item1Tree.getComposites().get(0);
        Assert.assertEquals("UserDetailItem2 节点层级应为 4", 4, item2Tree.getLevel());
        Assert.assertEquals("UserDetailItem2 节点 entityInfo 的 clazz 应为 UserDetailItem2Projection", UserDetailItem2Projection.class, item2Tree.getEntityInfo().getClazz());
        Assert.assertTrue("UserDetailItem2 节点不应有 composites", item2Tree.getComposites().isEmpty());
        Assert.assertNull("UserDetailItem2 子树的 middleEntityInfo 应为 null", item2Tree.getMiddleEntityInfo());
    }

    /**
     * test04: 跳层匹配（UserSkipProjection 直接包含 userDetailItem2，跳过 UserDetailItem1）
     */
    @Test
    public void test04_skipLevelProjection() {
        MethodInfo methodInfo = otoConfiguration.getMethodInfo(OTO_DAO_NAMESPACE + ".findSkipLevelProjection");
        Class<?> returnType = methodInfo.getMethodReturnInfo().getType();
        EntityRelationTree tree = otoMapperInfo.getEntityRelationTree(returnType);

        Assert.assertNotNull("跳层投影树不应为 null", tree);
        Assert.assertEquals("根节点层级应为 1", 1, tree.getLevel());
        Assert.assertEquals("根节点应有 1 个 composite（userDetailItem2）", 1, tree.getComposites().size());
        Assert.assertEquals("根节点 entityInfo 的 clazz 应为 UserSkipProjection", UserSkipProjection.class, tree.getEntityInfo().getClazz());

        EntityRelationTree item2Tree = tree.getComposites().get(0);
        Assert.assertEquals("UserDetailItem2 节点层级应为 2", 2, item2Tree.getLevel());
        Assert.assertEquals("UserDetailItem2 节点 entityInfo 的 clazz 应为 UserDetailItem2Projection", UserDetailItem2Projection.class, item2Tree.getEntityInfo().getClazz());
        Assert.assertNotNull("UserDetailItem2 节点的 columnInfo 不应为 null", item2Tree.getColumnInfo());
        Assert.assertEquals("columnInfo 的 javaColumnName 应为 userDetailItem2", "userDetailItem2", item2Tree.getColumnInfo().getJavaColumnName());
        Assert.assertNull("UserDetailItem2 子树的 middleEntityInfo 应为 null", item2Tree.getMiddleEntityInfo());
    }

    /**
     * test05: 字段名不匹配（UserUnknownFieldProjection 包含 unknownRelation 字段）
     */
    @Test
    public void test05_unknownFieldIgnored() {
        MethodInfo methodInfo = otoConfiguration.getMethodInfo(OTO_DAO_NAMESPACE + ".findUnknownFieldProjection");
        Class<?> returnType = methodInfo.getMethodReturnInfo().getType();
        EntityRelationTree tree = otoMapperInfo.getEntityRelationTree(returnType);

        Assert.assertNotNull("字段不匹配投影树不应为 null", tree);
        Assert.assertEquals("根节点层级应为 1", 1, tree.getLevel());
        Assert.assertTrue("unknownRelation 字段不匹配，不应产生 composites", tree.getComposites().isEmpty());
        Assert.assertEquals("根节点 entityInfo 的 clazz 应为 UserUnknownFieldProjection",
                UserUnknownFieldProjection.class, tree.getEntityInfo().getClazz());
    }

    /**
     * test06: 非 MGXQL 路径（无 SelectStatement）— 验证退回扁平逻辑
     */
    @Test
    public void test06_nonMgxqlFallback() {
        MethodInfo methodInfo = projConfiguration.getMethodInfo(PROJ_DAO_NAMESPACE + ".findByName");
        Class<?> returnType = methodInfo.getMethodReturnInfo().getType();
        EntityRelationTree tree = projMapperInfo.getEntityRelationTree(returnType);

        Assert.assertNotNull("非 MGXQL 投影树不应为 null", tree);
        Assert.assertEquals("层级应为 1", 1, tree.getLevel());
        Assert.assertEquals("索引应为 1", 1, tree.getIndex());
        Assert.assertTrue("非 MGXQL 投影不应有 composites", tree.getComposites().isEmpty());
        Assert.assertEquals("entityInfo 的 clazz 应为 UserDto", UserDto.class, tree.getEntityInfo().getClazz());
    }
}
