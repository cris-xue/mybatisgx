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
 * 验证 mgxqlSourceType 感知的参数绑定策略。
 * ENTITY 来源时，BoundParamEntry.paramPath 应使用查询实体字段名（如 idBetween, nameLike）
 * 而非列名（如 id, name）。
 *
 * @author 薛承城
 * @date 2026/6/24
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlBindingTest {

    private static MybatisgxConfiguration configuration;

    @BeforeClass
    public static void setUp() {
        configuration = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.custom.condition.entity"},
                new String[]{"com.mybatisgx.custom.condition.dao"}
        );
    }

    @Test
    public void test01_entitySourceBetweenUsesQueryEntityFieldName() {
        MethodInfo methodInfo = configuration.getMethodInfo("com.mybatisgx.custom.condition.dao.UserDao.findList");
        Assert.assertNotNull("findList MethodInfo should exist", methodInfo);
        Assert.assertEquals("findList should have ENTITY source",
                MgxqlSourceType.ENTITY, methodInfo.getMgxqlStatement().getMgxqlSourceType());

        WhereExpression rootExpr = methodInfo.getMgxqlStatement().getWhereClause().getRootExpression();
        WhereConditionNode betweenNode = findConditionByOperator(rootExpr, ComparisonOperator.BETWEEN);
        Assert.assertNotNull("Should have a BETWEEN condition from UserQuery.idBetween", betweenNode);

        BoundParam boundParam = betweenNode.getBoundParam();
        Assert.assertNotNull(boundParam);
        Assert.assertFalse("BETWEEN boundParam should have entries", boundParam.getEntries().isEmpty());

        List<String> paramPath = boundParam.getEntries().get(0).getParamPath();
        Assert.assertNotNull("BETWEEN paramPath should not be null", paramPath);
        Assert.assertFalse("BETWEEN paramPath should not be empty", paramPath.isEmpty());
        Assert.assertEquals("BETWEEN paramPath should use query entity field name 'idBetween', not column name 'id'",
                "idBetween", paramPath.get(paramPath.size() - 1));
    }

    @Test
    public void test02_entitySourceLikeUsesQueryEntityFieldName() {
        MethodInfo methodInfo = configuration.getMethodInfo("com.mybatisgx.custom.condition.dao.UserDao.findList");
        Assert.assertNotNull("findList MethodInfo should exist", methodInfo);
        Assert.assertEquals("findList should have ENTITY source",
                MgxqlSourceType.ENTITY, methodInfo.getMgxqlStatement().getMgxqlSourceType());

        WhereExpression rootExpr = methodInfo.getMgxqlStatement().getWhereClause().getRootExpression();
        WhereConditionNode likeNode = findConditionByOperator(rootExpr, ComparisonOperator.LIKE);
        Assert.assertNotNull("Should have a LIKE condition from UserQuery.nameLike", likeNode);

        BoundParam boundParam = likeNode.getBoundParam();
        Assert.assertNotNull(boundParam);
        Assert.assertFalse("LIKE boundParam should have entries", boundParam.getEntries().isEmpty());

        List<String> paramPath = boundParam.getEntries().get(0).getParamPath();
        Assert.assertNotNull("LIKE paramPath should not be null", paramPath);
        Assert.assertFalse("LIKE paramPath should not be empty", paramPath.isEmpty());
        Assert.assertEquals("LIKE paramPath should use query entity field name 'nameLike'",
                "nameLike", paramPath.get(paramPath.size() - 1));
    }

    private WhereConditionNode findConditionByOperator(WhereExpression expression, ComparisonOperator targetOperator) {
        if (expression == null || expression.getNodes() == null) {
            return null;
        }
        for (WhereConditionNode node : expression.getNodes()) {
            if (node.getSubExpression() != null) {
                WhereConditionNode found = findConditionByOperator(node.getSubExpression(), targetOperator);
                if (found != null) {
                    return found;
                }
            } else if (node.getOperator() == targetOperator) {
                return node;
            }
        }
        return null;
    }
}
