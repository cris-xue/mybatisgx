package com.mybatisgx.relation.select.simple_simple_id.onetoone.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.dao.UserDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.dao.UserDetailDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.UserDetail;
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
    private static Long firstUserId;
    private static Long firstUserDetailId;

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.dao"}
        );
        userDao = sqlSession.getMapper(UserDao.class);
        userDetailDao = sqlSession.getMapper(UserDetailDao.class);

        buildData();
        userDao.insertBatch(userList, count);
        userDetailDao.insertBatch(userDetailList, count);

        firstUserId = userList.get(0).getId();
        firstUserDetailId = userDetailList.get(0).getId();
    }

    private static void buildData() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            UserDetail userDetail = user.getUserDetail();
            userDetail.setUser(user);

            userList.add(user);
            userDetailList.add(userDetail);
        }
    }

    @Test
    public void testUserFindById() {
        User dbUser = userDao.findById(firstUserId);
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(firstUserId, dbUser.getId());

        User user = userList.get(0);
        Assert.assertEquals(user.getId(), dbUser.getId());
        Assert.assertEquals(user.getCode(), dbUser.getCode());
        Assert.assertNotNull(dbUser.getUserDetail());
        Assert.assertEquals(user.getUserDetail().getId(), dbUser.getUserDetail().getId());
        Assert.assertEquals(user.getUserDetail().getCode(), dbUser.getUserDetail().getCode());
    }

    @Test
    public void testUserFindList() {
        List<User> dbUserList = userDao.findList(new User());
        Assert.assertNotNull(dbUserList);
        Assert.assertEquals(count, dbUserList.size());
        for (int i = 0; i < count; i++) {
            User user = userList.get(i);
            User dbUser = dbUserList.get(i);

            Assert.assertEquals(user.getId(), dbUser.getId());
            Assert.assertNotNull(dbUser.getUserDetail());
            Assert.assertEquals(user.getUserDetail().getId(), dbUser.getUserDetail().getId());
        }
    }

    @Test
    public void testUserDetailFindById() {
        UserDetail dbUserDetail = userDetailDao.findById(firstUserDetailId);
        Assert.assertNotNull(dbUserDetail);
        Assert.assertEquals(firstUserDetailId, dbUserDetail.getId());

        UserDetail userDetail = userDetailList.get(0);
        Assert.assertEquals(userDetail.getId(), dbUserDetail.getId());
        Assert.assertEquals(userDetail.getCode(), dbUserDetail.getCode());
        Assert.assertNotNull(dbUserDetail.getUser());
        Assert.assertEquals(userDetail.getUser().getId(), dbUserDetail.getUser().getId());
    }

    @Test
    public void testUserDetailFindList() {
        List<UserDetail> dbUserDetailList = userDetailDao.findList(new UserDetail());
        Assert.assertNotNull(dbUserDetailList);
        Assert.assertEquals(count, dbUserDetailList.size());

        for (int i = 0; i < count; i++) {
            UserDetail userDetail = userDetailList.get(i);
            UserDetail dbUserDetail = dbUserDetailList.get(i);

            Assert.assertNotNull(dbUserDetail);
            Assert.assertNotNull(dbUserDetail.getUser());
            Assert.assertEquals(userDetail.getId(), dbUserDetail.getId());
            Assert.assertEquals(userDetail.getUser().getId(), dbUserDetail.getUser().getId());
        }
    }
}
