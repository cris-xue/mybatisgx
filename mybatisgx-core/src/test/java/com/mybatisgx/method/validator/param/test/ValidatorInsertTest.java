package com.mybatisgx.method.validator.param.test;

import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.Test;

public class ValidatorInsertTest {

    private static final String[] ENTITY_PACKAGES = {"com.mybatisgx.method.validator.param.entity"};

    private void initDao(String daoPackage) {
        DaoTestUtils.getSqlSession(ENTITY_PACKAGES, new String[]{daoPackage});
    }

    @Test
    public void testInsertEntity() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.insert.entity");
        } catch (MybatisgxException e) {
            Assert.assertEquals("insertMissingEntity 方法实体参数不存在", e.getMessage());
        }
    }

    @Test
    public void testInsertQueryEntity() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.insert.query");
        } catch (MybatisgxException e) {
            Assert.assertEquals("insertValid 方法实体参数不存在", e.getMessage());
        }
    }

    @Test
    public void testInsertBoth() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.insert.both");
        } catch (MybatisgxException e) {
            Assert.assertEquals("insertValid 方法查询实体参数不允许存在", e.getMessage());
        }
    }

    @Test
    public void testInsertMiss() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.insert.miss");
        } catch (MybatisgxException e) {
            Assert.assertEquals("insertValid 方法实体参数不存在", e.getMessage());
        }
    }

    @Test
    public void testInsertMapper() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.insert.mapper");
        } catch (MybatisgxException e) {
            Assert.assertEquals("insertValid 方法实体参数和mapper定义的实体参数类型不一致", e.getMessage());
        }
    }
}
