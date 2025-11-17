package com.mybatisgx.model.handler.test;

import com.mybatisgx.model.ConditionInfo;
import com.mybatisgx.model.ConditionOriginType;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.handler.EntityInfoHandler;
import com.mybatisgx.model.handler.MybatisgxSyntaxHandler;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MethodNameAstHandlerTest {

    private EntityInfoHandler entityInfoHandler = new EntityInfoHandler();
    private MybatisgxSyntaxHandler mybatisgxSyntaxHandler = new MybatisgxSyntaxHandler();

    @Test
    public void test01() {
        // 测试方法名中只有条件
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        mybatisgxSyntaxHandler.execute(entityInfo, methodInfo, null, ConditionOriginType.METHOD_NAME, "findByName");

        Assert.assertEquals(SqlCommandType.SELECT, methodInfo.getSqlCommandType());
        List<ConditionInfo> conditionInfoList = methodInfo.getConditionInfoList();
        Assert.assertTrue(conditionInfoList != null && conditionInfoList.size() > 0);
        ConditionInfo conditionInfo = conditionInfoList.get(0);
        Assert.assertEquals("name", conditionInfo.getColumnName());
    }

    @Test
    public void test02() {
        // 测试方法名中存在多条件
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        mybatisgxSyntaxHandler.execute(entityInfo, methodInfo, null, ConditionOriginType.METHOD_NAME, "findByIdAndNameOrNameEqAndNameLike");

        Assert.assertEquals(SqlCommandType.SELECT, methodInfo.getSqlCommandType());
        List<ConditionInfo> conditionInfoList = methodInfo.getConditionInfoList();
        Assert.assertTrue(conditionInfoList != null && conditionInfoList.size() > 0);

        ConditionInfo conditionInfo1 = conditionInfoList.get(0);
        Assert.assertEquals("id", conditionInfo1.getColumnName());
        Assert.assertEquals("Id", conditionInfo1.getOriginSegment());

        ConditionInfo conditionInfo2 = conditionInfoList.get(1);
        Assert.assertEquals("name", conditionInfo2.getColumnName());
        Assert.assertEquals("AndName", conditionInfo2.getOriginSegment());

        ConditionInfo conditionInfo3 = conditionInfoList.get(2);
        Assert.assertEquals("OrNameEq", conditionInfo3.getOriginSegment());

        ConditionInfo conditionInfo4 = conditionInfoList.get(3);
        Assert.assertEquals("AndNameLike", conditionInfo4.getOriginSegment());
    }

    @Test(expected = RuntimeException.class)
    public void test10() {
        // 测试方法名中的条件在实体中不存在
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        mybatisgxSyntaxHandler.execute(entityInfo, methodInfo, null, ConditionOriginType.METHOD_NAME, "findByName1");
    }
}
