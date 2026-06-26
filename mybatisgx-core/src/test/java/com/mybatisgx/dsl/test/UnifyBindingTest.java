package com.mybatisgx.dsl.test;

import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;

/**
 * 验证 bindConditionParam 统一优先级链（不分 mgxqlSourceType）：
 * ① @Param → ② @Param 全小写 → ③ queryEntity → entity（early return）→ ④ argN。
 *
 * @author 薛承城
 * @date 2026/6/26
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UnifyBindingTest {

    private static MybatisgxConfiguration customConditionConfig;
    private static MybatisgxConfiguration updateBothConfig;

    @BeforeClass
    public static void setUp() {
        customConditionConfig = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.custom.condition.entity"},
                new String[]{"com.mybatisgx.custom.condition.dao"}
        );
        updateBothConfig = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.method.validator.param.entity.update"},
                new String[]{"com.mybatisgx.method.validator.param.dao.update.both"}
        );
    }

    /**
     * ENTITY 来源跳过 @Param：条件派生自 query 实体字段，应直接绑 query 实体（wrapper=true），
     * 而非 @Param（后缀解析与 @Param 基础名匹配冲突，如 idIn 不可误绑 @Param("id")）。
     * findListNew1111(@Param("id") Long id, UserQuery userQuery) → id 条件应绑 query 实体。
     */
    @Test
    public void test01_entitySourceSkipsParamBindsQueryEntity() {
        MethodInfo methodInfo = customConditionConfig.getMethodInfo("com.mybatisgx.custom.condition.dao.UserDao.findListNew1111");
        Assert.assertNotNull(methodInfo);
        Assert.assertEquals(MgxqlSourceType.ENTITY, methodInfo.getMgxqlStatement().getMgxqlSourceType());

        WhereConditionNode idNode = findConditionByField(
                methodInfo.getMgxqlStatement().getWhereClause().getRootExpression(), "id");
        Assert.assertNotNull("应存在 id 条件", idNode);
        Assert.assertNotNull(idNode.getMethodParamInfo());
        Assert.assertTrue("ENTITY 应跳过 @Param、绑 query 实体字段（wrapper=true）",
                idNode.getMethodParamInfo().getWrapper());
    }

    /**
     * UPDATE 双参数（entity + queryEntity）时，WHERE 条件绑定 queryEntity。
     * updateByNameLike(user, query) → name like 条件经后缀解析命中 query 实体的 nameLike（entity 无此字段），
     * 证明 query 优先于 entity。
     */
    @Test
    public void test02_updateDualParamQueryEntityWins() {
        MethodInfo methodInfo = updateBothConfig.getMethodInfo(
                "com.mybatisgx.method.validator.param.dao.update.both.UpdateBothDao.updateByNameLike");
        Assert.assertNotNull(methodInfo);

        WhereConditionNode nameNode = findConditionByField(
                methodInfo.getMgxqlStatement().getWhereClause().getRootExpression(), "name");
        Assert.assertNotNull("应存在 name 条件", nameNode);
        Assert.assertEquals(ComparisonOperator.LIKE, nameNode.getOperator());

        Assert.assertNotNull(nameNode.getMethodParamInfo());
        Assert.assertTrue("UPDATE 条件应绑定到实体型参数（wrapper=true）",
                nameNode.getMethodParamInfo().getWrapper());
        List<String> paramPath = nameNode.getBoundParam().getEntries().get(0).getParamPath();
        Assert.assertFalse(paramPath.isEmpty());
        Assert.assertEquals("后缀解析应命中 query 实体的 nameLike（query 胜），而非 entity 的 name",
                "nameLike", paramPath.get(paramPath.size() - 1));
    }

    /**
     * METHOD_NAME 来源同样走统一链（sanity）。
     * findByNameLike(String name) → name like 条件绑定到方法参数。
     */
    @Test
    public void test03_methodNameSourceBindsViaUnifiedChain() {
        MethodInfo methodInfo = customConditionConfig.getMethodInfo("com.mybatisgx.custom.condition.dao.UserDao.findByNameLike");
        Assert.assertNotNull(methodInfo);
        Assert.assertEquals(MgxqlSourceType.METHOD_NAME, methodInfo.getMgxqlStatement().getMgxqlSourceType());

        WhereConditionNode nameNode = findConditionByField(
                methodInfo.getMgxqlStatement().getWhereClause().getRootExpression(), "name");
        Assert.assertNotNull("应存在 name 条件", nameNode);
        Assert.assertNotNull(nameNode.getMethodParamInfo());
    }

    private WhereConditionNode findConditionByField(WhereExpression expression, String fieldName) {
        if (expression == null || expression.getNodes() == null) {
            return null;
        }
        for (WhereConditionNode node : expression.getNodes()) {
            if (node.getSubExpression() != null) {
                WhereConditionNode found = findConditionByField(node.getSubExpression(), fieldName);
                if (found != null) {
                    return found;
                }
            } else if (fieldName.equals(node.getFieldName())) {
                return node;
            }
        }
        return null;
    }
}
