package com.mybatisgx.method.validator.param.test;

import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.Test;

public class ValidatorSelectTest {

    private static final String[] ENTITY_PACKAGES = {"com.mybatisgx.method.validator.param.entity"};

    private void initDao(String daoPackage) {
        DaoTestUtils.getSqlSession(ENTITY_PACKAGES, new String[]{daoPackage});
    }

    @Test
    public void testSelectEntity() {
        initDao("com.mybatisgx.method.validator.param.dao.select.entity");
    }

    @Test
    public void testSelectQueryEntity() {
        initDao("com.mybatisgx.method.validator.param.dao.select.query");
    }

    @Test
    public void testSelectBoth() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.select.both");
        } catch (MybatisgxException e) {
            Assert.assertEquals("findByName 方法实体参数和查询实体参数不允许同时存在", e.getMessage());
        }
    }

    @Test
    public void testSelectMiss() {
        initDao("com.mybatisgx.method.validator.param.dao.select.miss");
    }

    @Test
    public void testSelectMapper() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.select.mapper");
        } catch (MybatisgxException e) {
            Assert.assertEquals("findByName 方法实体参数和mapper定义的实体参数类型不一致", e.getMessage());
        }
    }
}
