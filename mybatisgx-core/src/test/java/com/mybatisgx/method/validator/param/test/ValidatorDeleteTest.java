package com.mybatisgx.method.validator.param.test;

import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.method.validator.param.dao.delete.entity.DeleteEntityDao;
import com.mybatisgx.method.validator.param.dao.delete.miss.DeleteMissEntityDao;
import com.mybatisgx.method.validator.param.dao.delete.query.DeleteQueryEntityDao;
import com.mybatisgx.method.validator.param.entity.delete.ValidatorUser;
import com.mybatisgx.method.validator.param.entity.delete.ValidatorUserQuery;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

public class ValidatorDeleteTest {

    private static final String[] ENTITY_PACKAGES = {"com.mybatisgx.method.validator.param.entity.delete"};

    private SqlSession initDao(String daoPackage) {
        return DaoTestUtils.getSqlSession(ENTITY_PACKAGES, new String[]{daoPackage});
    }

    @Test
    public void testDeleteEntity() {
        SqlSession sqlSession = initDao("com.mybatisgx.method.validator.param.dao.delete.entity");
        DeleteEntityDao deleteEntityDao = sqlSession.getMapper(DeleteEntityDao.class);

        ValidatorUser validatorUser = new ValidatorUser();
        validatorUser.setName("name");
        deleteEntityDao.insert(validatorUser);

        int count = deleteEntityDao.deleteByName(validatorUser);
        Assert.assertEquals(1, count);

        ValidatorUser queryData = deleteEntityDao.findById(validatorUser.getId());
        Assert.assertNull(queryData);

        sqlSession.rollback();
    }

    @Test
    public void testDeleteQueryEntity() {
        SqlSession sqlSession = initDao("com.mybatisgx.method.validator.param.dao.delete.query");
        DeleteQueryEntityDao deleteQueryEntityDao = sqlSession.getMapper(DeleteQueryEntityDao.class);

        ValidatorUserQuery validatorUserQuery = new ValidatorUserQuery();
        validatorUserQuery.setName("name");
        deleteQueryEntityDao.insert(validatorUserQuery);

        int count = deleteQueryEntityDao.deleteByName(validatorUserQuery);
        Assert.assertEquals(1, count);

        ValidatorUser queryData = deleteQueryEntityDao.findById(validatorUserQuery.getId());
        Assert.assertNull(queryData);

        sqlSession.rollback();
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
        SqlSession sqlSession = initDao("com.mybatisgx.method.validator.param.dao.delete.miss");
        DeleteMissEntityDao deleteMissEntityDao = sqlSession.getMapper(DeleteMissEntityDao.class);

        ValidatorUserQuery validatorUserQuery = new ValidatorUserQuery();
        validatorUserQuery.setName("name");
        deleteMissEntityDao.insert(validatorUserQuery);

        int count = deleteMissEntityDao.deleteByName("name");
        Assert.assertEquals(1, count);

        ValidatorUser queryData = deleteMissEntityDao.findById(validatorUserQuery.getId());
        Assert.assertNull(queryData);

        sqlSession.rollback();
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
