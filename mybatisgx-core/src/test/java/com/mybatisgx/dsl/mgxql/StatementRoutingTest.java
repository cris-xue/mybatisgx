package com.mybatisgx.dsl.mgxql;

import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.handler.EntityInfoHandler;
import com.mybatisgx.model.handler.MgxqlHandler;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

public class StatementRoutingTest {

    private EntityInfo entityInfo;
    private MgxqlHandler mgxqlHandler;

    @Before
    public void setUp() {
        EntityInfoHandler entityInfoHandler = new EntityInfoHandler();
        entityInfo = entityInfoHandler.execute(StatementRoutingEntity.class);
        mgxqlHandler = new MgxqlHandler();
    }

    @Test
    public void test01_deleteWithStatementGoesMgxqlPath() throws Exception {
        Method method = StatementRoutingDao.class.getMethod("deleteByName", String.class);
        MethodInfo methodInfo = buildMethodInfo(method, SqlCommandType.DELETE);

        mgxqlHandler.methodConditionParse(methodInfo);

        MgxqlStatement mgxqlStatement = methodInfo.getMgxqlStatement();
        Assert.assertNotNull(mgxqlStatement);
        Assert.assertEquals("delete StatementRoutingEntity where name = :name", mgxqlStatement.getDsl());
        Assert.assertEquals(MgxqlSourceType.MANUAL, mgxqlStatement.getMgxqlSourceType());
        Assert.assertNotNull(mgxqlStatement.getWhereClause());
        WhereExpression rootExpr = mgxqlStatement.getWhereClause().getRootExpression();
        Assert.assertNotNull(rootExpr);
        Assert.assertFalse(rootExpr.getNodes().isEmpty());
        WhereConditionNode node = rootExpr.getNodes().get(0);
        Assert.assertEquals("name", node.getFieldName());
    }

    @Test
    public void test02_updateWithStatementGoesMgxqlPath() throws Exception {
        Method method = StatementRoutingDao.class.getMethod("updateByName", String.class, StatementRoutingEntity.class);
        MethodInfo methodInfo = buildMethodInfo(method, SqlCommandType.UPDATE);

        mgxqlHandler.methodConditionParse(methodInfo);

        MgxqlStatement mgxqlStatement = methodInfo.getMgxqlStatement();
        Assert.assertNotNull(mgxqlStatement);
        Assert.assertEquals("update StatementRoutingEntity where name = :name", mgxqlStatement.getDsl());
        Assert.assertEquals(MgxqlSourceType.MANUAL, mgxqlStatement.getMgxqlSourceType());
        Assert.assertNotNull(mgxqlStatement.getWhereClause());
        WhereExpression rootExpr = mgxqlStatement.getWhereClause().getRootExpression();
        Assert.assertNotNull(rootExpr);
        Assert.assertFalse(rootExpr.getNodes().isEmpty());
        WhereConditionNode node = rootExpr.getNodes().get(0);
        Assert.assertEquals("name", node.getFieldName());
    }

    @Test
    public void test03_deleteWithoutStatementGoesMethodNamePath() throws Exception {
        Method method = StatementRoutingDao.class.getMethod("deleteByNameAndAge", String.class, Integer.class);
        MethodInfo methodInfo = buildMethodInfo(method, SqlCommandType.DELETE);

        mgxqlHandler.methodConditionParse(methodInfo);

        MgxqlStatement mgxqlStatement = methodInfo.getMgxqlStatement();
        Assert.assertNotNull(mgxqlStatement);
        Assert.assertEquals(MgxqlSourceType.METHOD_NAME, mgxqlStatement.getMgxqlSourceType());
        Assert.assertNull(methodInfo.getStatementExpression());
        Assert.assertNotNull(mgxqlStatement.getWhereClause());
        WhereExpression rootExpr = mgxqlStatement.getWhereClause().getRootExpression();
        Assert.assertNotNull(rootExpr);
        Assert.assertFalse(rootExpr.getNodes().isEmpty());
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
