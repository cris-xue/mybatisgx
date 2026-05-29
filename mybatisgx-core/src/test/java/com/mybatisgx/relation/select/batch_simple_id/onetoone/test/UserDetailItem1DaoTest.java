package com.mybatisgx.relation.select.batch_simple_id.onetoone.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.relation.select.batch_simple_id.onetoone.dao.UserDao;
import com.mybatisgx.relation.select.batch_simple_id.onetoone.dao.UserDetailDao;
import com.mybatisgx.relation.select.batch_simple_id.onetoone.dao.UserDetailItem1Dao;
import com.mybatisgx.relation.select.batch_simple_id.onetoone.dao.UserDetailItem2Dao;
import com.mybatisgx.relation.select.batch_simple_id.onetoone.entity.User;
import com.mybatisgx.relation.select.batch_simple_id.onetoone.entity.UserDetail;
import com.mybatisgx.relation.select.batch_simple_id.onetoone.entity.UserDetailItem1;
import com.mybatisgx.relation.select.batch_simple_id.onetoone.entity.UserDetailItem2;
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
public class UserDetailItem1DaoTest {

    private static int count = 10;
    private static UserDao userDao;
    private static UserDetailDao userDetailDao;
    private static UserDetailItem1Dao userDetailItem1Dao;
    private static UserDetailItem2Dao userDetailItem2Dao;

    private static List<User> userList = new ArrayList();
    private static List<UserDetail> userDetailList = new ArrayList();
    private static List<UserDetailItem1> userDetailItem1List = new ArrayList();
    private static List<UserDetailItem2> userDetailItem2List = new ArrayList();
    private static Long firstUserDetailItem1Id;

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.batch_simple_id.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.select.batch_simple_id.onetoone.dao"}
        );
        userDao = sqlSession.getMapper(UserDao.class);
        userDetailDao = sqlSession.getMapper(UserDetailDao.class);
        userDetailItem1Dao = sqlSession.getMapper(UserDetailItem1Dao.class);
        userDetailItem2Dao = sqlSession.getMapper(UserDetailItem2Dao.class);

        buildData();
        userDao.insertBatch(userList, count);
        userDetailDao.insertBatch(userDetailList, count);
        userDetailItem1Dao.insertBatch(userDetailItem1List, count);
        userDetailItem2Dao.insertBatch(userDetailItem2List, count);

        firstUserDetailItem1Id = userDetailItem1List.get(0).getId();
    }

    private static void buildData() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            user.setId(null);

            UserDetail userDetail = user.getUserDetail();
            userDetail.setId(null);
            userDetail.setUser(user);

            UserDetailItem1 userDetailItem1 = userDetail.getUserDetailItem1();
            userDetailItem1.setId(null);
            userDetailItem1.setUserDetail(userDetail);

            UserDetailItem2 userDetailItem2 = userDetailItem1.getUserDetailItem2();
            userDetailItem2.setId(null);
            userDetailItem2.setUserDetailItem1(userDetailItem1);

            userList.add(user);
            userDetailList.add(userDetail);
            userDetailItem1List.add(userDetailItem1);
            userDetailItem2List.add(userDetailItem2);
        }
    }

    @Test
    public void testFindById() {
        UserDetailItem1 dbUserDetailItem1 = userDetailItem1Dao.findById(firstUserDetailItem1Id);
        Assert.assertNotNull(dbUserDetailItem1);

        UserDetailItem1 userDetailItem1 = userDetailItem1List.get(0);
        Assert.assertEquals(userDetailItem1.getId(), dbUserDetailItem1.getId());
        Assert.assertEquals(userDetailItem1.getCode(), dbUserDetailItem1.getCode());

        UserDetail dbUserDetail = dbUserDetailItem1.getUserDetail();
        Assert.assertNotNull(dbUserDetail);
        UserDetail userDetail = userDetailList.get(0);
        Assert.assertEquals(userDetail.getId(), dbUserDetail.getId());
        Assert.assertEquals(userDetail.getCode(), dbUserDetail.getCode());

        User dbUser = dbUserDetail.getUser();
        Assert.assertNotNull(dbUser);
        User user = userList.get(0);
        Assert.assertEquals(user.getId(), dbUser.getId());

        UserDetailItem2 dbUserDetailItem2 = dbUserDetailItem1.getUserDetailItem2();
        Assert.assertNotNull(dbUserDetailItem2);
        UserDetailItem2 userDetailItem2 = userDetailItem2List.get(0);
        Assert.assertEquals(userDetailItem2.getId(), dbUserDetailItem2.getId());
        Assert.assertEquals(userDetailItem2.getCode(), dbUserDetailItem2.getCode());
    }

    @Test
    public void testFindList() {
        List<UserDetailItem1> dbUserDetailItem1List = userDetailItem1Dao.findList(new UserDetailItem1());
        Assert.assertNotNull(dbUserDetailItem1List);
        Assert.assertEquals(count, dbUserDetailItem1List.size());

        for (int i = 0; i < count; i++) {
            UserDetailItem1 userDetailItem1 = userDetailItem1List.get(i);
            UserDetailItem1 dbUserDetailItem1 = dbUserDetailItem1List.get(i);

            Assert.assertEquals(userDetailItem1.getId(), dbUserDetailItem1.getId());

            UserDetail dbUserDetail = dbUserDetailItem1.getUserDetail();
            Assert.assertNotNull(dbUserDetail);
            UserDetail userDetail = userDetailList.get(i);
            Assert.assertEquals(userDetail.getId(), dbUserDetail.getId());

            User dbUser = dbUserDetail.getUser();
            Assert.assertNotNull(dbUser);
            User user = userList.get(i);
            Assert.assertEquals(user.getId(), dbUser.getId());

            UserDetailItem2 dbUserDetailItem2 = dbUserDetailItem1.getUserDetailItem2();
            Assert.assertNotNull(dbUserDetailItem2);
            UserDetailItem2 userDetailItem2 = userDetailItem2List.get(i);
            Assert.assertEquals(userDetailItem2.getId(), dbUserDetailItem2.getId());
        }
    }

    @Test
    public void testFindListWithCondition() {
        UserDetailItem1 condition = new UserDetailItem1();
        condition.setCode(userDetailItem1List.get(0).getCode());
        List<UserDetailItem1> dbUserDetailItem1List = userDetailItem1Dao.findList(condition);
        Assert.assertNotNull(dbUserDetailItem1List);
        Assert.assertFalse(dbUserDetailItem1List.isEmpty());

        for (UserDetailItem1 dbUserDetailItem1 : dbUserDetailItem1List) {
            Assert.assertEquals(userDetailItem1List.get(0).getCode(), dbUserDetailItem1.getCode());

            UserDetail dbUserDetail = dbUserDetailItem1.getUserDetail();
            Assert.assertNotNull(dbUserDetail);

            User dbUser = dbUserDetail.getUser();
            Assert.assertNotNull(dbUser);

            UserDetailItem2 dbUserDetailItem2 = dbUserDetailItem1.getUserDetailItem2();
            Assert.assertNotNull(dbUserDetailItem2);
        }
    }
}