package com.mybatisgx.method.validator.param.test;

import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.method.validator.param.dao.select.entity.SelectEntityDao;
import com.mybatisgx.method.validator.param.dao.select.miss.SelectMissEntityDao;
import com.mybatisgx.method.validator.param.dao.select.query.SelectQueryEntityDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import com.mybatisgx.method.validator.param.entity.ValidatorUserQuery;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ValidatorSelectTest {

    private static final String[] ENTITY_PACKAGES = {"com.mybatisgx.method.validator.param.entity"};

    private SqlSession initDao(String daoPackage) {
        return DaoTestUtils.getSqlSession(ENTITY_PACKAGES, new String[]{daoPackage});
    }

    @Test
    public void testSelectEntity() {
        SqlSession sqlSession = initDao("com.mybatisgx.method.validator.param.dao.select.entity");
        SelectEntityDao selectEntityDao = sqlSession.getMapper(SelectEntityDao.class);

        ValidatorUser validatorUser = new ValidatorUser();
        validatorUser.setId(1111L);
        validatorUser.setName("name");
        selectEntityDao.insert(validatorUser);

        List<ValidatorUser> validatorUserList = selectEntityDao.findByName(validatorUser);
        Assert.assertEquals(1, validatorUserList.size());
        Assert.assertEquals("name", validatorUserList.get(0).getName());
    }

    @Test
    public void testSelectQueryEntity() {
        SqlSession sqlSession = initDao("com.mybatisgx.method.validator.param.dao.select.query");
        SelectQueryEntityDao selectQueryEntityDao = sqlSession.getMapper(SelectQueryEntityDao.class);

        ValidatorUser validatorUser = new ValidatorUser();
        validatorUser.setId(1111L);
        validatorUser.setName("name");
        selectQueryEntityDao.insert(validatorUser);

        ValidatorUserQuery validatorUserQuery = new ValidatorUserQuery();
        validatorUserQuery.setName("name");
        List<ValidatorUser> validatorUserList = selectQueryEntityDao.findByName(validatorUserQuery);
        Assert.assertEquals(1, validatorUserList.size());
        Assert.assertEquals("name", validatorUserList.get(0).getName());
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
        SqlSession sqlSession = initDao("com.mybatisgx.method.validator.param.dao.select.miss");
        SelectMissEntityDao selectMissEntityDao = sqlSession.getMapper(SelectMissEntityDao.class);

        ValidatorUser validatorUser = new ValidatorUser();
        validatorUser.setName("name");
        selectMissEntityDao.insert(validatorUser);

        ValidatorUserQuery validatorUserQuery = new ValidatorUserQuery();
        validatorUserQuery.setName("name");
        List<ValidatorUser> validatorUserList = selectMissEntityDao.findByName("name");
        Assert.assertEquals(1, validatorUserList.size());
        Assert.assertEquals("name", validatorUserList.get(0).getName());
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
