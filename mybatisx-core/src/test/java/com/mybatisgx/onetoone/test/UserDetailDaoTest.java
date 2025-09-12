package com.mybatisgx.onetoone.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.onetoone.dao.UserDao;
import com.mybatisgx.onetoone.dao.UserDetailDao;
import com.mybatisgx.onetoone.entity.User;
import com.mybatisgx.onetoone.entity.UserDetail;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDetailDaoTest {

    @Test
    public void testFindById() {
        List<Class<?>> entityClassList = Arrays.asList(User.class, UserDetail.class);
        List<Class<?>> daoClassList = Arrays.asList(UserDao.class, UserDetailDao.class);
        SqlSession sqlSession = DaoTestUtils.getSqlSession(entityClassList, daoClassList);
        UserDetailDao userDetailDao = sqlSession.getMapper(UserDetailDao.class);

        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        UserDetail userDetail = fixtureGenerator.createRandomized(UserDetail.class);
        int insertCount = userDetailDao.insert(userDetail);
        Assert.assertEquals(1, insertCount);

        UserDetail dbUserDetail = userDetailDao.findById(userDetail.getId());
        Assert.assertNotNull(dbUserDetail);
        Assert.assertEquals(userDetail.getCode(), dbUserDetail.getCode());
    }

    @Test
    public void testFindList() {
        List<Class<?>> entityClassList = Arrays.asList(User.class, UserDetail.class);
        List<Class<?>> daoClassList = Arrays.asList(UserDao.class, UserDetailDao.class);
        SqlSession sqlSession = DaoTestUtils.getSqlSession(entityClassList, daoClassList);
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        UserDetailDao userDetailDao = sqlSession.getMapper(UserDetailDao.class);

        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        int count = 10;
        List<User> userList = new ArrayList(count);
        List<UserDetail> userDetailList = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            userList.add(user);

            UserDetail userDetail = user.getUserDetail();
            userDetail.setUser(user);
            userDetailList.add(user.getUserDetail());
        }
        int insertCount = userDao.insertBatch(userList, count);
        Assert.assertEquals(count, insertCount);
        int insertCount1 = userDetailDao.insertBatch(userDetailList, count);
        Assert.assertEquals(count, insertCount1);

        List<UserDetail> dbUserDetailList = userDetailDao.findList(new UserDetail());
        Assert.assertNotNull(dbUserDetailList);
        for (int i = 0; i < count; i++) {
            UserDetail dbUserDetail = dbUserDetailList.get(i);
        }
        UserDetail dbUserDetail = dbUserDetailList.get(0);
        // Assert.assertEquals(dbUser.getId(), dbUser.getUserDetail().getUser().getId());
    }
}
