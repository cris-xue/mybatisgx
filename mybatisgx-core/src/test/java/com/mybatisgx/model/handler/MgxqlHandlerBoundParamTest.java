package com.mybatisgx.model.handler;

import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.dsl.mgxql.model.expression.ConditionColumnExpression;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * MgxqlHandler 绑定层 ConditionColumnExpression.tableAlias 透传测试
 *
 * @author 薛承城
 * @date 2026/6/30
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlHandlerBoundParamTest {

    private static final String DAO_NAMESPACE = "com.mybatisgx.dsl.mgxql.binding.dao.MgxqlBoundParamDao";
    private static MybatisgxConfiguration configuration;

    @BeforeClass
    public static void setUp() {
        configuration = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetomany.entity"},
                new String[]{"com.mybatisgx.dsl.mgxql.binding.dao"}
        );
    }

    @Test
    public void test01_joinWhereConditionHasTableAlias() {
        MethodInfo methodInfo = configuration.getMethodInfo(DAO_NAMESPACE + ".findJoinWhereWithAlias");
        Assert.assertNotNull("findJoinWhereWithAlias 应存在", methodInfo);

        MgxqlStatement mgxqlStatement = methodInfo.getMgxqlStatement();
        Assert.assertNotNull("MGXQL 语句应非空", mgxqlStatement);
        WhereClause whereClause = mgxqlStatement.getWhereClause();
        Assert.assertNotNull("WHERE 子句应非空", whereClause);

        WhereConditionNode node = whereClause.getRootExpression().getNodes().get(0);
        Assert.assertEquals("JOIN 场景 WHERE 条件 entityAlias 应为 u", "u", node.getFieldAlias());

        BoundParam boundParam = node.getBoundParam();
        Assert.assertNotNull("boundParam 应非空", boundParam);
        Assert.assertFalse("boundParam 应有 entries", boundParam.getEntries().isEmpty());

        ConditionColumnExpression expr = (ConditionColumnExpression) boundParam.getEntries().get(0).getSqlExpression();
        Assert.assertEquals("JOIN 场景 tableAlias 应为 u", "u", expr.getTableAlias());
        Assert.assertEquals("toSql() 应返回 u.code", "u.code", expr.toSql());
    }

    @Test
    public void test02_nonJoinWhereConditionWithAlias() {
        MethodInfo methodInfo = configuration.getMethodInfo(DAO_NAMESPACE + ".findWhereWithAlias");
        Assert.assertNotNull("findWhereWithAlias 应存在", methodInfo);

        MgxqlStatement mgxqlStatement = methodInfo.getMgxqlStatement();
        Assert.assertNotNull("MGXQL 语句应非空", mgxqlStatement);
        WhereClause whereClause = mgxqlStatement.getWhereClause();
        Assert.assertNotNull("WHERE 子句应非空", whereClause);

        WhereConditionNode node = whereClause.getRootExpression().getNodes().get(0);
        Assert.assertEquals("非 JOIN 但有别名场景 WHERE 条件 entityAlias 应为 u", "u", node.getFieldAlias());

        BoundParam boundParam = node.getBoundParam();
        Assert.assertNotNull("boundParam 应非空", boundParam);
        Assert.assertFalse("boundParam 应有 entries", boundParam.getEntries().isEmpty());

        ConditionColumnExpression expr = (ConditionColumnExpression) boundParam.getEntries().get(0).getSqlExpression();
        Assert.assertEquals("非 JOIN 有别名场景 tableAlias 应为 u", "u", expr.getTableAlias());
        Assert.assertEquals("toSql() 应返回 u.code", "u.code", expr.toSql());
    }
}
