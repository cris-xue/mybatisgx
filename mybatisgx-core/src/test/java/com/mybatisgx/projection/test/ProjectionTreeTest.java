package com.mybatisgx.projection.test;

import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.EntityRelationTree;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.projection.dao.UserDao;
import com.mybatisgx.projection.dto.UserDto;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * 非 MGXQL 路径投影树构建测试：验证退回扁平逻辑
 *
 * @author ccxuef
 * @date 2026/7/4
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProjectionTreeTest {

    private static final String PROJ_DAO_NAMESPACE = "com.mybatisgx.projection.dao.UserDao";

    private static MybatisgxConfiguration projConfiguration;
    private static MapperInfo projMapperInfo;

    @BeforeClass
    public static void setUp() {
        projConfiguration = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.projection.entity"},
                new String[]{"com.mybatisgx.projection.dao"}
        );
        projMapperInfo = projConfiguration.getMethodInfo(PROJ_DAO_NAMESPACE + ".findByName").getMapperInfo();
    }

    /**
     * test01: 非 MGXQL 路径（无 SelectStatement）— 验证退回扁平逻辑
     */
    @Test
    public void test01_nonMgxqlFallback() {
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
