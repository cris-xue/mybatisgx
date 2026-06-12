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

    @Test
    public void test03_joinInfoInheritsEntityRef() {
        JoinInfo join = new JoinInfo();
        join.setEntityName("Role");
        join.setAlias("role");
        join.setJoinType(com.mybatisgx.dsl.mgxql.model.JoinType.LEFT);
        join.setOnLeftAlias("user");
        join.setOnRightAlias("role");
        Assert.assertEquals("Role", join.getEntityName());
        Assert.assertEquals("role", join.getAlias());
        Assert.assertEquals(com.mybatisgx.dsl.mgxql.model.JoinType.LEFT, join.getJoinType());
        Assert.assertEquals("user", join.getOnLeftAlias());
        Assert.assertEquals("role", join.getOnRightAlias());
        Assert.assertTrue(join instanceof EntityRefInfo);
    }
}
