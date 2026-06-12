package com.mybatisgx.model;

import org.junit.Assert;
import org.junit.Test;

public class GroupByFieldInfoTest {

    @Test
    public void test01_defaultConstructorAndSetters() {
        GroupByFieldInfo info = new GroupByFieldInfo();
        info.setEntityAlias("user");
        info.setFieldName("status");
        Assert.assertEquals("user", info.getEntityAlias());
        Assert.assertEquals("status", info.getFieldName());
    }

    @Test
    public void test02_fullArgsConstructor() {
        GroupByFieldInfo info = new GroupByFieldInfo("user", "status");
        Assert.assertEquals("user", info.getEntityAlias());
        Assert.assertEquals("status", info.getFieldName());
    }

    @Test
    public void test03_nullEntityAlias() {
        GroupByFieldInfo info = new GroupByFieldInfo(null, "status");
        Assert.assertNull(info.getEntityAlias());
        Assert.assertEquals("status", info.getFieldName());
    }
}
