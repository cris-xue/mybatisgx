package com.mybatisgx.dsl.test;

import com.mybatisgx.model.ConditionInfo;
import com.mybatisgx.model.ConditionOriginType;
import org.junit.Assert;
import org.junit.Test;

public class ConditionInfoTest {

    @Test
    public void test01_optionalDefaultValue() {
        ConditionInfo conditionInfo = new ConditionInfo(0, ConditionOriginType.METHOD_NAME);
        Assert.assertEquals(Boolean.FALSE, conditionInfo.getOptional());
    }

    @Test
    public void test02_optionalSetValue() {
        ConditionInfo conditionInfo = new ConditionInfo(0, ConditionOriginType.METHOD_NAME);
        conditionInfo.setOptional(true);
        Assert.assertEquals(Boolean.TRUE, conditionInfo.getOptional());
    }
}
