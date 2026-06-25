package com.mybatisgx.dsl.test;

import com.mybatisgx.dsl.mgxql.model.WhereClause;
import com.mybatisgx.dsl.mgxql.model.WhereConditionNode;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.Test;

public class EntityAstHandlerTest {

    @Test
    public void test01() {
        MybatisgxConfiguration configuration = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.dsl.test.entity"},
                new String[]{"com.mybatisgx.dsl.test.dao"}
        );

        MethodInfo methodInfo = configuration.getMethodInfo("com.mybatisgx.dsl.test.dao.UserEntityDao.findOne");
        WhereClause whereClause = methodInfo.getMgxqlStatement().getWhereClause();

        WhereConditionNode whereConditionNode1 = whereClause.getRootExpression().getNodes().get(2);
        Assert.assertEquals("nameEq", whereConditionNode1.getColumnInfo().getJavaColumnName());

        WhereConditionNode whereConditionNode2 = whereClause.getRootExpression().getNodes().get(2);
        Assert.assertEquals("nameEq", whereConditionNode2.getColumnInfo().getJavaColumnName());
    }
}
