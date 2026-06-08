package com.mybatisgx.dsl.mgxql;

import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.*;
import com.mybatisgx.model.handler.EntityInfoHandler;
import com.mybatisgx.model.handler.MethodInfoHandler;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

public class StatementRoutingTest {

    private EntityInfo entityInfo;
    private MethodInfoHandler methodInfoHandler;

    @Before
    public void setUp() {
        EntityInfoHandler entityInfoHandler = new EntityInfoHandler();
        entityInfo = entityInfoHandler.execute(StatementRoutingEntity.class);
        methodInfoHandler = new MethodInfoHandler(new MybatisgxConfiguration());
    }

    @Test
    public void test01_deleteWithStatementGoesMgxqlPath() throws Exception {
        Method method = StatementRoutingDao.class.getMethod("deleteByName", String.class);
        MethodInfo methodInfo = buildMethodInfo(method, SqlCommandType.DELETE);

        methodInfoHandler.methodConditionParse(methodInfo);

        List<ConditionInfo> conditionInfoList = methodInfo.getConditionInfoList();
        Assert.assertTrue(conditionInfoList != null && !conditionInfoList.isEmpty());
        Assert.assertEquals("delete where name = :name", methodInfo.getStatementExpression());
        Assert.assertEquals("name", conditionInfoList.get(0).getColumnName());
        Assert.assertEquals(ComparisonOperator.EQ, conditionInfoList.get(0).getComparisonOperator());
        Assert.assertEquals(ConditionOriginType.STATEMENT_METHOD_NAME, conditionInfoList.get(0).getConditionOriginType());
    }

    @Test
    public void test02_updateWithStatementGoesMgxqlPath() throws Exception {
        Method method = StatementRoutingDao.class.getMethod("updateByName", String.class, StatementRoutingEntity.class);
        MethodInfo methodInfo = buildMethodInfo(method, SqlCommandType.UPDATE);

        methodInfoHandler.methodConditionParse(methodInfo);

        List<ConditionInfo> conditionInfoList = methodInfo.getConditionInfoList();
        Assert.assertTrue(conditionInfoList != null && !conditionInfoList.isEmpty());
        Assert.assertEquals("update where name = :name", methodInfo.getStatementExpression());
        Assert.assertEquals("name", conditionInfoList.get(0).getColumnName());
        Assert.assertEquals(ConditionOriginType.STATEMENT_METHOD_NAME, conditionInfoList.get(0).getConditionOriginType());
    }

    @Test
    public void test03_deleteWithoutStatementGoesMethodNamePath() throws Exception {
        Method method = StatementRoutingDao.class.getMethod("deleteByNameAndAge", String.class, Integer.class);
        MethodInfo methodInfo = buildMethodInfo(method, SqlCommandType.DELETE);

        methodInfoHandler.methodConditionParse(methodInfo);

        List<ConditionInfo> conditionInfoList = methodInfo.getConditionInfoList();
        Assert.assertTrue(conditionInfoList != null && !conditionInfoList.isEmpty());
        Assert.assertNull(methodInfo.getStatementExpression());
        Assert.assertEquals(ConditionOriginType.METHOD_NAME, conditionInfoList.get(0).getConditionOriginType());
    }

    private MethodInfo buildMethodInfo(Method method, SqlCommandType sqlCommandType) {
        MapperInfo mapperInfo = new MapperInfo();
        mapperInfo.setEntityInfo(entityInfo);
        mapperInfo.setEntityClass(StatementRoutingEntity.class);
        mapperInfo.setIdClass(Long.class);
        mapperInfo.setQueryEntityClass(StatementRoutingEntity.class);

        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setMapperInfo(mapperInfo);
        methodInfo.setMethod(method);
        methodInfo.setMethodName(method.getName());
        methodInfo.setSqlCommandType(sqlCommandType);
        return methodInfo;
    }
}
