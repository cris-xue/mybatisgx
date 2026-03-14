package com.mybatisgx.method.validator.param.test;

import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.Test;

public class ValidatorUpdateTest {

    private static final String[] ENTITY_PACKAGES = {"com.mybatisgx.method.validator.param.entity"};

    private void initDao(String daoPackage) {
        DaoTestUtils.getSqlSession(ENTITY_PACKAGES, new String[]{daoPackage});
    }

    @Test
    public void testSelectEntity() {
        initDao("com.mybatisgx.method.validator.param.dao.update.entity");
    }

    @Test
    public void testSelectQueryEntity() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.update.query");
        } catch (MybatisgxException e) {
            Assert.assertEquals("updateByName 方法查询实体参数不允许单独存在", e.getMessage());
        }
    }

    @Test
    public void testSelectBoth() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.update.both");
        } catch (MybatisgxException e) {
            Assert.assertEquals("findByName 方法实体参数和查询实体参数不允许同时存在", e.getMessage());
        }
    }

    @Test
    public void testSelectMiss() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.update.miss");
        } catch (MybatisgxException e) {
            Assert.assertEquals("updateByName 方法实体参数不存在", e.getMessage());
        }
    }

    @Test
    public void testSelectMapper() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.update.mapper");
        } catch (MybatisgxException e) {
            Assert.assertEquals("updateByName 方法实体参数和mapper定义的实体参数类型不一致", e.getMessage());
        }
    }
}
