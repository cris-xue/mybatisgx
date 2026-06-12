package com.mybatisgx.model;

import org.junit.Assert;
import org.junit.Test;

public class EntityRefInfoTest {

    @Test
    public void test01_defaultConstructorAndSetters() {
        EntityRefInfo info = new EntityRefInfo();
        info.setEntityName("User");
        info.setAlias("user");
        Assert.assertEquals("User", info.getEntityName());
        Assert.assertEquals("user", info.getAlias());
    }

    @Test
    public void test02_fullArgsConstructor() {
        EntityRefInfo info = new EntityRefInfo("User", "user");
        Assert.assertEquals("User", info.getEntityName());
        Assert.assertEquals("user", info.getAlias());
    }
}
