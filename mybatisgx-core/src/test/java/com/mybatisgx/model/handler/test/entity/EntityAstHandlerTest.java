package com.mybatisgx.model.handler.test.entity;

import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.ConditionInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class EntityAstHandlerTest {

    @Test
    public void test01() {
        MybatisgxConfiguration configuration = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.model.handler.test.entity"},
                new String[]{"com.mybatisgx.model.handler.test.entity"}
        );

        MethodInfo methodInfo = configuration.getMethodInfo("com.mybatisgx.model.handler.test.entity.UserEntityDao.findOne");
        List<ConditionInfo> conditionInfoList = methodInfo.getConditionInfoList();

        ConditionInfo conditionInfo1 = conditionInfoList.get(2);
        Assert.assertEquals("$NameEq$", conditionInfo1.getOriginSegment());
        Assert.assertEquals("nameEq", conditionInfo1.getColumnInfo().getJavaColumnName());

        ConditionInfo conditionInfo2 = conditionInfoList.get(3);
        Assert.assertEquals("$NameEq$Eq", conditionInfo2.getOriginSegment());
        Assert.assertEquals("nameEq", conditionInfo2.getColumnInfo().getJavaColumnName());
    }
}
