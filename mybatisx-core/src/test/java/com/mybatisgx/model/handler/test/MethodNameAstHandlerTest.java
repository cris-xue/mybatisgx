package com.mybatisgx.model.handler.test;

import com.lc.mybatisx.model.ConditionInfo;
import com.lc.mybatisx.model.EntityInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.model.handler.EntityInfoHandler;
import com.lc.mybatisx.model.handler.MethodNameAstHandler;
import com.mybatisgx.model.test.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MethodNameAstHandlerTest {

    private EntityInfoHandler entityInfoHandler = new EntityInfoHandler();
    private MethodNameAstHandler methodNameAstHandler = new MethodNameAstHandler();

    @Test
    public void test01() {
        // 测试方法名中只有条件
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        methodNameAstHandler.execute(entityInfo, methodInfo, false, "findByName");

        Assert.assertEquals("select", methodInfo.getAction());
        List<ConditionInfo> conditionInfoList = methodInfo.getConditionInfoList();
        Assert.assertTrue(conditionInfoList != null && conditionInfoList.size() > 0);
        ConditionInfo conditionInfo = conditionInfoList.get(0);
        Assert.assertEquals("name", conditionInfo.getJavaColumnName());
    }

    @Test
    public void test02() {
        // 测试方法名中存在多条件
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        methodNameAstHandler.execute(entityInfo, methodInfo, false, "findByIdAndNameOrNameEqAndNameLike");

        Assert.assertEquals("select", methodInfo.getAction());
        List<ConditionInfo> conditionInfoList = methodInfo.getConditionInfoList();
        Assert.assertTrue(conditionInfoList != null && conditionInfoList.size() > 0);

        ConditionInfo conditionInfo1 = conditionInfoList.get(0);
        Assert.assertEquals("id", conditionInfo1.getJavaColumnName());
        Assert.assertEquals("Id", conditionInfo1.getOrigin());

        ConditionInfo conditionInfo2 = conditionInfoList.get(1);
        Assert.assertEquals("name", conditionInfo2.getJavaColumnName());
        Assert.assertEquals("AndName", conditionInfo2.getOrigin());

        ConditionInfo conditionInfo3 = conditionInfoList.get(2);
        Assert.assertEquals("OrNameEq", conditionInfo3.getOrigin());

        ConditionInfo conditionInfo4 = conditionInfoList.get(3);
        Assert.assertEquals("AndNameLike", conditionInfo4.getOrigin());
    }

    @Test(expected = RuntimeException.class)
    public void test10() {
        // 测试方法名中的条件在实体中不存在
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        MethodInfo methodInfo = new MethodInfo();
        methodNameAstHandler.execute(entityInfo, methodInfo, false, "findByName1");
    }
}
