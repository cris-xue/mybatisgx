package com.mybatisgx.onetoone.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.onetoone.dao.UserDao;
import com.mybatisgx.onetoone.dao.UserDetailDao;
import com.mybatisgx.onetoone.entity.User;
import com.mybatisgx.onetoone.entity.UserDetail;
import com.mybatisgx.onetoone.entity.UserDetailItem1;
import com.mybatisgx.onetoone.entity.UserDetailItem2;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDetailDaoTest {

    private static int count = 10;
    private static UserDao userDao;
    private static UserDetailDao userDetailDao;

    @BeforeClass
    public static void beforeClass() {
        List<Class<?>> entityClassList = Arrays.asList(User.class, UserDetail.class, UserDetailItem1.class, UserDetailItem2.class);
        List<Class<?>> daoClassList = Arrays.asList(UserDao.class, UserDetailDao.class);
        SqlSession sqlSession = DaoTestUtils.getSqlSession(entityClassList, daoClassList);
        userDao = sqlSession.getMapper(UserDao.class);
        userDetailDao = sqlSession.getMapper(UserDetailDao.class);

        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        List<User> userList = new ArrayList(count);
        List<UserDetail> userDetailList = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            userList.add(user);

            UserDetail userDetail = user.getUserDetail();
            if (i == 0) {
                MultiId<Long> multiId = new MultiId();
                multiId.setId1(111111L);
                multiId.setId2(111111L);
                userDetail.setMultiId(multiId);
            }
            userDetail.setUser(user);
            userDetailList.add(userDetail);
        }
        userDao.insertBatch(userList, count);
        userDetailDao.insertBatch(userDetailList, count);
    }

    @Test
    public void testFindById() {
        MultiId<Long> multiId = new MultiId();
        multiId.setId1(111111L);
        multiId.setId2(111111L);
        UserDetail dbUserDetail = userDetailDao.findById(multiId);
        Assert.assertNotNull(dbUserDetail);
    }

    @Test
    public void testFindList() {
        List<UserDetail> dbUserDetailList = userDetailDao.findList(new UserDetail());
        Assert.assertNotNull(dbUserDetailList);
        for (int i = 0; i < count; i++) {
            UserDetail dbUserDetail = dbUserDetailList.get(i);
        }
        UserDetail dbUserDetail = dbUserDetailList.get(0);
        // Assert.assertEquals(dbUser.getId(), dbUser.getUserDetail().getUser().getId());
    }
}
