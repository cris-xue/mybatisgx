package com.mybatisgx.relation.select.mgxql.onetoone.scenario;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.relation.select.mgxql.onetoone.dao.MgxqlJoinWhereDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.dao.UserDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.dao.UserDetailDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.dao.UserDetailItem1Dao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.dao.UserDetailItem2Dao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.UserDetail;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.UserDetailItem1;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.UserDetailItem2;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

/**
 * MGXQL JOIN + WHERE 一对一场景测试
 *
 * @author ccxuef
 * @date 2026/7/7
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlJoinWhereDaoScenarioTest {

    private static final int DATA_COUNT = 5;
    private static MgxqlJoinWhereDao mgxqlJoinWhereDao;
    private static UserDao userDao;
    private static UserDetailDao userDetailDao;
    private static UserDetailItem1Dao userDetailItem1Dao;
    private static UserDetailItem2Dao userDetailItem2Dao;
    private static List<User> userList = new ArrayList<User>();
    private static List<UserDetail> userDetailList = new ArrayList<UserDetail>();
    private static List<UserDetailItem1> userDetailItem1List = new ArrayList<UserDetailItem1>();
    private static List<UserDetailItem2> userDetailItem2List = new ArrayList<UserDetailItem2>();

    @BeforeClass
    public static void setUp() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.dao", "com.mybatisgx.relation.select.mgxql.onetoone.dao"}
        );
        mgxqlJoinWhereDao = sqlSession.getMapper(MgxqlJoinWhereDao.class);
        userDao = sqlSession.getMapper(UserDao.class);
        userDetailDao = sqlSession.getMapper(UserDetailDao.class);
        userDetailItem1Dao = sqlSession.getMapper(UserDetailItem1Dao.class);
        userDetailItem2Dao = sqlSession.getMapper(UserDetailItem2Dao.class);

        buildData();
        userDao.insertBatch(userList, DATA_COUNT);
        userDetailDao.insertBatch(userDetailList, DATA_COUNT);
        userDetailItem1Dao.insertBatch(userDetailItem1List, DATA_COUNT);
        userDetailItem2Dao.insertBatch(userDetailItem2List, DATA_COUNT);
    }

    private static void buildData() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        for (int i = 0; i < DATA_COUNT; i++) {
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
    public void test01_findUser() {
        Long userId = userList.get(0).getId();
        User result = mgxqlJoinWhereDao.findUser(userId);
        Assert.assertNotNull(result);
        Assert.assertEquals(userId, result.getId());
    }

    @Test
    public void test02_findUserJoinDetailByUserId() {
        Long userId = userList.get(0).getId();
        User result = mgxqlJoinWhereDao.findUserJoinDetailByUserId(userId);
        Assert.assertNotNull(result);
        Assert.assertEquals(userId, result.getId());
        // select u.* 只选择User列，不包含UserDetail列，因此userDetail不会被MGXQL resultMap填充
    }

    @Test
    public void test03_findUserJoinDetailByDetailCode() {
        String detailCode = userDetailList.get(0).getCode();
        List<User> result = mgxqlJoinWhereDao.findUserJoinDetailByDetailCode(detailCode);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        // select u.* 只选择User列，不包含UserDetail列，因此userDetail不会被MGXQL resultMap填充
        // 验证返回的User基本字段
        for (User user : result) {
            Assert.assertNotNull(user.getId());
        }
    }
}
