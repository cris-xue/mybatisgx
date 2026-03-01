package com.mybatisgx.method.validator.param.test;

import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.Test;

public class ValidatorMethodParamEntityTest {

    private static final String[] ENTITY_PACKAGES = {"com.mybatisgx.method.validator.param.entity"};

    private void initDao(String daoPackage) {
        DaoTestUtils.getSqlSession(ENTITY_PACKAGES, new String[]{daoPackage});
    }

    // ============================
    // INSERT 异常路径
    // ============================

    /**
     * INSERT 方法缺少实体参数 → 实体参数不存在
     */
    @Test
    public void testInsertMissingEntity() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.insert.missing");
            Assert.fail("应抛出 MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertEquals("insertMissingEntity 方法实体参数不存在", e.getMessage());
        }
    }

    /**
     * INSERT 方法同时存在实体参数和查询实体参数 → 查询实体参数不允许存在
     */
    @Test
    public void testInsertHasQueryEntity() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.insert.hasquery");
            Assert.fail("应抛出 MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertEquals("insertHasQueryEntity 方法查询实体参数不允许存在", e.getMessage());
        }
    }

    /**
     * INSERT 方法实体参数类型与 Mapper 定义不一致 → 类型不一致
     */
    @Test
    public void testInsertWrongType() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.insert.wrongtype");
            Assert.fail("应抛出 MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertEquals("insertWrongType 方法实体参数和定义的实体参数类型不一致", e.getMessage());
        }
    }

    // ============================
    // INSERT 正常路径
    // ============================

    /**
     * INSERT 方法参数合法 → 初始化成功
     */
    @Test
    public void testInsertSuccess() {
        initDao("com.mybatisgx.method.validator.param.dao.insert.success");
    }

    // ============================
    // UPDATE 异常路径
    // ============================

    /**
     * UPDATE 方法仅有查询实体参数，无操作实体参数 → 查询实体参数不允许单独存在
     */
    @Test
    public void testUpdateOnlyQueryEntity() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.update.onlyquery");
            Assert.fail("应抛出 MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertEquals("updateByIdEq 方法查询实体参数不允许单独存在", e.getMessage());
        }
    }

    // ============================
    // UPDATE 正常路径
    // ============================

    /**
     * UPDATE 方法仅有操作实体参数 → 初始化成功
     */
    @Test
    public void testUpdateOnlyEntity() {
        initDao("com.mybatisgx.method.validator.param.dao.update.onlyentity");
    }

    /**
     * UPDATE 方法同时包含操作实体参数和查询实体参数 → 初始化成功
     */
    @Test
    public void testUpdateBothEntity() {
        initDao("com.mybatisgx.method.validator.param.dao.update.both");
    }

    // ============================
    // DELETE 异常路径
    // ============================

    /**
     * DELETE 方法同时包含操作实体参数和查询实体参数 → 不允许同时存在
     */
    @Test
    public void testDeleteBothEntity() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.delete.both");
            Assert.fail("应抛出 MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertEquals("deleteByNameLike 方法实体参数和查询实体参数不允许同时存在", e.getMessage());
        }
    }

    // ============================
    // DELETE 正常路径
    // ============================

    /**
     * DELETE 方法仅有操作实体参数 → 初始化成功
     */
    @Test
    public void testDeleteOnlyEntity() {
        initDao("com.mybatisgx.method.validator.param.dao.delete.success");
    }

    // ============================
    // SELECT 异常路径
    // ============================

    /**
     * SELECT 方法同时包含操作实体参数和查询实体参数 → 不允许同时存在
     */
    @Test
    public void testSelectBothEntity() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.select.both");
            Assert.fail("应抛出 MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertEquals("findByNameLike 方法实体参数和查询实体参数不允许同时存在", e.getMessage());
        }
    }

    // ============================
    // SELECT 正常路径
    // ============================

    /**
     * SELECT 方法仅有操作实体参数 → 初始化成功
     */
    @Test
    public void testSelectOnlyEntity() {
        initDao("com.mybatisgx.method.validator.param.dao.select.success");
    }

    // ============================
    // 通用校验
    // ============================

    /**
     * 非 INSERT 方法实体参数类型与 Mapper 定义不一致 → 类型不一致
     */
    @Test
    public void testGeneralEntityTypeMismatch() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.general.entitymismatch");
            Assert.fail("应抛出 MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertEquals("deleteByWrong 方法实体参数和定义的实体参数类型不一致", e.getMessage());
        }
    }

    /**
     * 查询实体参数的 @QueryEntity.value() 与 Mapper 定义的实体类不一致 → 类型不一致
     */
    @Test
    public void testGeneralQueryEntityAnnotationMismatch() {
        try {
            initDao("com.mybatisgx.method.validator.param.dao.general.querymismatch");
            Assert.fail("应抛出 MybatisgxException");
        } catch (MybatisgxException e) {
            Assert.assertEquals("deleteByWrongQuery 方法实体参数和定义的实体参数类型不一致", e.getMessage());
        }
    }
}
