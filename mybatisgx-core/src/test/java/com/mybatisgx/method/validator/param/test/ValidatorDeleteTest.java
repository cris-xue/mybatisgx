package com.mybatisgx.method.validator.param.test;

import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.Test;

public class ValidatorDeleteTest {

    private static final String[] ENTITY_PACKAGES = {"com.mybatisgx.method.validator.param.entity"};

    private void initDao(String daoPackage) {
        DaoTestUtils.getSqlSession(ENTITY_PACKAGES, new String[]{daoPackage});
    }

    @Test
    public void testDeleteEntity() {
        initDao("com.mybatisgx.method.validator.param.dao.delete.entity");
    }

    @Test
    public void testDeleteQueryEntity() {
        initDao("com.mybatisgx.method.validator.param.dao.delete.query");
    }

    @Test
    public void testDeleteBoth() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.delete.both");
        } catch (MybatisgxException e) {
            Assert.assertEquals("deleteByName 方法实体参数和查询实体参数不允许同时存在", e.getMessage());
        }
    }

    @Test
    public void testDeleteMiss() {
        initDao("com.mybatisgx.method.validator.param.dao.delete.miss");
    }

    @Test
    public void testDeleteMapper() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.delete.mapper");
        } catch (MybatisgxException e) {
            Assert.assertEquals("deleteByName 方法实体参数和mapper定义的实体参数类型不一致", e.getMessage());
        }
    }
}
