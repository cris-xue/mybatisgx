package com.mybatisgx.relation.select.join_complex_id.onetoone.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.join_complex_id.onetoone.dao.UserDao;
import com.mybatisgx.relation.select.join_complex_id.onetoone.dao.UserDetailDao;
import com.mybatisgx.relation.select.join_complex_id.onetoone.dao.UserDetailItem1Dao;
import com.mybatisgx.relation.select.join_complex_id.onetoone.dao.UserDetailItem2Dao;
import com.mybatisgx.relation.select.join_complex_id.onetoone.entity.User;
import com.mybatisgx.relation.select.join_complex_id.onetoone.entity.UserDetail;
import com.mybatisgx.relation.select.join_complex_id.onetoone.entity.UserDetailItem1;
import com.mybatisgx.relation.select.join_complex_id.onetoone.entity.UserDetailItem2;
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
public class UserDetailDaoTest {

    private static int count = 10;
    private static UserDao userDao;
    private static UserDetailDao userDetailDao;
    private static UserDetailItem1Dao userDetailItem1Dao;
    private static UserDetailItem2Dao userDetailItem2Dao;

    private static List<User> userList = new ArrayList();
    private static List<UserDetail> userDetailList = new ArrayList();
    private static List<UserDetailItem1> userDetailItem1List = new ArrayList();
    private static List<UserDetailItem2> userDetailItem2List = new ArrayList();

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.join_complex_id.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.select.join_complex_id.onetoone.dao"}
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
    }

    private static void buildData() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            if (i == 0) {
                MultiId<Long> multiId = new MultiId();
                multiId.setId1(222111L);
                multiId.setId2(222111L);
                user.setMultiId(multiId);
            } else {
                MultiId<Long> multiId = user.getMultiId();
                multiId.setId1(null);
                multiId.setId2(null);
            }

            UserDetail userDetail = user.getUserDetail();
            if (i == 0) {
                MultiId<Long> multiId = new MultiId();
                multiId.setId1(222112L);
                multiId.setId2(222112L);
                userDetail.setMultiId(multiId);
            } else {
                MultiId<Long> multiId = userDetail.getMultiId();
                multiId.setId1(null);
                multiId.setId2(null);
            }
            userDetail.setUser(user);

            UserDetailItem1 userDetailItem1 = userDetail.getUserDetailItem1();
            if (i == 0) {
                MultiId<Long> multiId = new MultiId();
                multiId.setId1(222113L);
                multiId.setId2(222113L);
                userDetailItem1.setMultiId(multiId);
            } else {
                MultiId<Long> multiId = userDetailItem1.getMultiId();
                multiId.setId1(null);
                multiId.setId2(null);
            }
            userDetailItem1.setUserDetail(userDetail);

            UserDetailItem2 userDetailItem2 = userDetailItem1.getUserDetailItem2();
            if (i == 0) {
                MultiId<Long> multiId = new MultiId();
                multiId.setId1(222114L);
                multiId.setId2(222114L);
                userDetailItem2.setMultiId(multiId);
            } else {
                MultiId<Long> multiId = userDetailItem2.getMultiId();
                multiId.setId1(null);
                multiId.setId2(null);
            }
            userDetailItem2.setUserDetailItem1(userDetailItem1);

            userList.add(user);
            userDetailList.add(userDetail);
            userDetailItem1List.add(userDetailItem1);
            userDetailItem2List.add(userDetailItem2);
        }
    }

    @Test
    public void testFindById() {
        MultiId<Long> multiId = new MultiId();
        multiId.setId1(222112L);
        multiId.setId2(222112L);
        UserDetail dbUserDetail = userDetailDao.findById(multiId);
        Assert.assertNotNull(dbUserDetail);

        UserDetail userDetail = userDetailList.get(0);
        Assert.assertEquals(userDetail.getMultiId().getId1(), dbUserDetail.getMultiId().getId1());
        Assert.assertEquals(userDetail.getMultiId().getId2(), dbUserDetail.getMultiId().getId2());

        User dbUser = dbUserDetail.getUser();
        Assert.assertNotNull(dbUser);
        User user = userList.get(0);
        Assert.assertEquals(user.getMultiId().getId1(), dbUser.getMultiId().getId1());
        Assert.assertEquals(user.getMultiId().getId2(), dbUser.getMultiId().getId2());

        UserDetailItem1 dbUserDetailItem1 = dbUserDetail.getUserDetailItem1();
        Assert.assertNotNull(dbUserDetailItem1);
        UserDetailItem1 userDetailItem1 = userDetailItem1List.get(0);
        Assert.assertEquals(userDetailItem1.getMultiId().getId1(), dbUserDetailItem1.getMultiId().getId1());
        Assert.assertEquals(userDetailItem1.getMultiId().getId2(), dbUserDetailItem1.getMultiId().getId2());

        UserDetailItem2 dbUserDetailItem2 = dbUserDetailItem1.getUserDetailItem2();
        Assert.assertNotNull(dbUserDetailItem2);
        UserDetailItem2 userDetailItem2 = userDetailItem2List.get(0);
        Assert.assertEquals(userDetailItem2.getMultiId().getId1(), dbUserDetailItem2.getMultiId().getId1());
        Assert.assertEquals(userDetailItem2.getMultiId().getId2(), dbUserDetailItem2.getMultiId().getId2());
    }

    @Test
    public void testFindList() {
        List<UserDetail> dbUserDetailList = userDetailDao.findList(new UserDetail());
        Assert.assertNotNull(dbUserDetailList);
        Assert.assertEquals(count, dbUserDetailList.size());

        for (int i = 0; i < count; i++) {
            UserDetail userDetail = userDetailList.get(i);
            UserDetail dbUserDetail = dbUserDetailList.get(i);

            Assert.assertEquals(userDetail.getMultiId().getId1(), dbUserDetail.getMultiId().getId1());
            Assert.assertEquals(userDetail.getMultiId().getId2(), dbUserDetail.getMultiId().getId2());

            User dbUser = dbUserDetail.getUser();
            Assert.assertNotNull(dbUser);
            User user = userList.get(i);
            Assert.assertEquals(user.getMultiId().getId1(), dbUser.getMultiId().getId1());
            Assert.assertEquals(user.getMultiId().getId2(), dbUser.getMultiId().getId2());

            UserDetailItem1 dbUserDetailItem1 = dbUserDetail.getUserDetailItem1();
            Assert.assertNotNull(dbUserDetailItem1);
            UserDetailItem1 userDetailItem1 = userDetailItem1List.get(i);
            Assert.assertEquals(userDetailItem1.getMultiId().getId1(), dbUserDetailItem1.getMultiId().getId1());
            Assert.assertEquals(userDetailItem1.getMultiId().getId2(), dbUserDetailItem1.getMultiId().getId2());

            UserDetailItem2 dbUserDetailItem2 = dbUserDetailItem1.getUserDetailItem2();
            Assert.assertNotNull(dbUserDetailItem2);
            UserDetailItem2 userDetailItem2 = userDetailItem2List.get(i);
            Assert.assertEquals(userDetailItem2.getMultiId().getId1(), dbUserDetailItem2.getMultiId().getId1());
            Assert.assertEquals(userDetailItem2.getMultiId().getId2(), dbUserDetailItem2.getMultiId().getId2());
        }
    }

    @Test
    public void testFindListWithCondition() {
        UserDetail condition = new UserDetail();
        condition.setCode(userDetailList.get(0).getCode());
        List<UserDetail> dbUserDetailList = userDetailDao.findList(condition);
        Assert.assertNotNull(dbUserDetailList);
        Assert.assertFalse(dbUserDetailList.isEmpty());

        for (UserDetail dbUserDetail : dbUserDetailList) {
            Assert.assertEquals(userDetailList.get(0).getCode(), dbUserDetail.getCode());

            User dbUser = dbUserDetail.getUser();
            Assert.assertNotNull(dbUser);

            UserDetailItem1 dbUserDetailItem1 = dbUserDetail.getUserDetailItem1();
            Assert.assertNotNull(dbUserDetailItem1);

            UserDetailItem2 dbUserDetailItem2 = dbUserDetailItem1.getUserDetailItem2();
            Assert.assertNotNull(dbUserDetailItem2);
        }
    }
}
