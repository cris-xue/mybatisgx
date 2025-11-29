package com.mybatisgx.relation.select.simple.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.simple.dao.UserDao;
import com.mybatisgx.relation.select.simple.dao.UserDetailDao;
import com.mybatisgx.relation.select.simple.entity.User;
import com.mybatisgx.relation.select.simple.entity.UserDetail;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserDaoTest {

    private static int count = 10;
    private static UserDao userDao;
    private static UserDetailDao userDetailDao;

    private static List<User> userList = new ArrayList();
    private static List<UserDetail> userDetailList = new ArrayList();

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.simple.entity"},
                new String[]{"com.mybatisgx.relation.select.simple.dao"}
        );
        userDao = sqlSession.getMapper(UserDao.class);
        userDetailDao = sqlSession.getMapper(UserDetailDao.class);

        buildData();
        userDao.insertBatch(userList, count);
        userDetailDao.insertBatch(userDetailList, count);
    }

    private static void buildData() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            if (i == 0) {
                MultiId<Long> multiId = new MultiId();
                multiId.setId1(111111L);
                multiId.setId2(111111L);
                user.setMultiId(multiId);
            }
            UserDetail userDetail = user.getUserDetail();
            if (i == 0) {
                MultiId<Long> multiId = new MultiId();
                multiId.setId1(111111L);
                multiId.setId2(111111L);
                userDetail.setMultiId(multiId);
            }
            userDetail.setUser(user);

            userList.add(user);
            userDetailList.add(userDetail);
        }
    }

    @Test
    public void testUserFindById() {
        MultiId<Long> multiId = new MultiId();
        multiId.setId1(111111L);
        multiId.setId2(111111L);
        User dbUser = userDao.findById(multiId);
        Assert.assertNotNull(dbUser);

        User user = userList.get(0);
        Assert.assertEquals(user.getMultiId().getId1(), dbUser.getMultiId().getId1());
        Assert.assertEquals(user.getMultiId().getId2(), dbUser.getMultiId().getId2());
        Assert.assertEquals(user.getUserDetail().getMultiId().getId1(), dbUser.getUserDetail().getMultiId().getId1());
        Assert.assertEquals(user.getUserDetail().getMultiId().getId2(), dbUser.getUserDetail().getMultiId().getId2());
    }

    @Test
    public void testUserFindList() {
        List<User> dbUserList = userDao.findList(new User());
        Assert.assertNotNull(dbUserList);
        for (int i = 0; i < count; i++) {
            User user = userList.get(i);
            User dbUser = dbUserList.get(i);

            Assert.assertEquals(user.getMultiId().getId1(), dbUser.getMultiId().getId1());
            Assert.assertEquals(user.getMultiId().getId2(), dbUser.getMultiId().getId2());
            Assert.assertEquals(user.getUserDetail().getMultiId().getId1(), dbUser.getUserDetail().getMultiId().getId1());
            Assert.assertEquals(user.getUserDetail().getMultiId().getId2(), dbUser.getUserDetail().getMultiId().getId2());
        }
    }

    @Test
    public void testUserDetailFindById() {
        MultiId<Long> multiId = new MultiId();
        multiId.setId1(111111L);
        multiId.setId2(111111L);
        UserDetail dbUserDetail = userDetailDao.findById(multiId);
        Assert.assertNotNull(dbUserDetail);

        UserDetail userDetail = userDetailList.get(0);
        Assert.assertEquals(userDetail.getMultiId().getId1(), dbUserDetail.getMultiId().getId1());
        Assert.assertEquals(userDetail.getMultiId().getId2(), dbUserDetail.getMultiId().getId2());
        Assert.assertEquals(userDetail.getUser().getMultiId().getId1(), dbUserDetail.getUser().getMultiId().getId1());
        Assert.assertEquals(userDetail.getUser().getMultiId().getId2(), dbUserDetail.getUser().getMultiId().getId2());
    }

    @Test
    public void testUserDetailFindList() {
        List<UserDetail> dbUserDetailList = userDetailDao.findList(new UserDetail());
        Assert.assertNotNull(dbUserDetailList);

        for (int i = 0; i < count; i++) {
            UserDetail userDetail = userDetailList.get(i);
            UserDetail dbUserDetail = dbUserDetailList.get(i);

            Assert.assertNotNull(dbUserDetail);
            Assert.assertNotNull(dbUserDetail.getUser());
            Assert.assertEquals(userDetail.getMultiId().getId1(), dbUserDetail.getMultiId().getId1());
            Assert.assertEquals(userDetail.getMultiId().getId2(), dbUserDetail.getMultiId().getId2());
            Assert.assertEquals(userDetail.getUser().getMultiId().getId1(), dbUserDetail.getUser().getMultiId().getId1());
            Assert.assertEquals(userDetail.getUser().getMultiId().getId2(), dbUserDetail.getUser().getMultiId().getId2());
        }
    }
}
