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

public class UserDaoTest {

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
            if (i == 0) {
                MultiId<Long> multiId = new MultiId();
                multiId.setId1(111111L);
                multiId.setId2(111111L);
                user.setMultiId(multiId);
            }
            userList.add(user);

            UserDetail userDetail = user.getUserDetail();
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
        User dbUser = userDao.findById(multiId);
        Assert.assertNotNull(dbUser);
    }

    @Test
    public void testFindList() {
        List<User> dbUserList = userDao.findList(new User());
        Assert.assertNotNull(dbUserList);
        for (int i = 0; i < count; i++) {
            User dbUser = dbUserList.get(i);
            dbUser.getUserDetail();
        }
        User dbUser = dbUserList.get(0);
        /*Assert.assertEquals(dbUser.getMultiId().getId1(), dbUser.getUserDetail().getUser().getMultiId().getId1());
        Assert.assertEquals(dbUser.getMultiId().getId2(), dbUser.getUserDetail().getUser().getMultiId().getId2());*/
    }
}
