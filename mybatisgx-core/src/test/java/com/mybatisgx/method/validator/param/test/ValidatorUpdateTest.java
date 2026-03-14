package com.mybatisgx.method.validator.param.test;

import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.method.validator.param.dao.update.both.UpdateBothDao;
import com.mybatisgx.method.validator.param.dao.update.entity.UpdateEntityDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import com.mybatisgx.method.validator.param.entity.ValidatorUserQuery;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

public class ValidatorUpdateTest {

    private static final String[] ENTITY_PACKAGES = {"com.mybatisgx.method.validator.param.entity"};

    private SqlSession initDao(String daoPackage) {
        return DaoTestUtils.getSqlSession(ENTITY_PACKAGES, new String[]{daoPackage});
    }

    @Test
    public void testSelectEntity() {
        SqlSession sqlSession = initDao("com.mybatisgx.method.validator.param.dao.update.entity");
        UpdateEntityDao updateEntityDao = sqlSession.getMapper(UpdateEntityDao.class);

        ValidatorUser validatorUser = new ValidatorUser();
        validatorUser.setName("name");
        updateEntityDao.insert(validatorUser);

        validatorUser.setName("name_modify");
        int count = updateEntityDao.updateByName(validatorUser, "name");
        Assert.assertEquals(1, count);
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
        SqlSession sqlSession = initDao("com.mybatisgx.method.validator.param.dao.update.both");
        UpdateBothDao updateBothDao = sqlSession.getMapper(UpdateBothDao.class);

        ValidatorUser validatorUser = new ValidatorUser();
        validatorUser.setName("name");
        updateBothDao.insert(validatorUser);

        validatorUser.setName("name_modify");
        ValidatorUserQuery validatorUserQuery = new ValidatorUserQuery();
        validatorUserQuery.setName("name");
        int count = updateBothDao.updateByName(validatorUser, validatorUserQuery);
        Assert.assertEquals(1, count);
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
