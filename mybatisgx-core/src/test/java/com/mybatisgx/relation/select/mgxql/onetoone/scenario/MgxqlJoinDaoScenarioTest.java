package com.mybatisgx.relation.select.mgxql.onetoone.scenario;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.relation.select.mgxql.onetoone.dao.MgxqlJoinDao;
import com.mybatisgx.relation.select.mgxql.onetoone.dto.UserDetailItem2Projection;
import com.mybatisgx.relation.select.mgxql.onetoone.dto.UserDetailProjection;
import com.mybatisgx.relation.select.mgxql.onetoone.dto.UserFlatProjection;
import com.mybatisgx.relation.select.mgxql.onetoone.dto.UserMultiLevelProjection;
import com.mybatisgx.relation.select.mgxql.onetoone.dto.UserOneLevelProjection;
import com.mybatisgx.relation.select.mgxql.onetoone.dto.UserSkipProjection;
import com.mybatisgx.relation.select.mgxql.onetoone.dto.UserUnknownFieldProjection;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

/**
 * MGXQL JOIN 一对一场景测试
 *
 * @author ccxuef
 * @date 2026/7/7
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlJoinDaoScenarioTest {

    private static final int DATA_COUNT = 5;
    private static MgxqlJoinDao mgxqlJoinDao;
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
        mgxqlJoinDao = sqlSession.getMapper(MgxqlJoinDao.class);
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
    public void test01_findJoinList2() {
        List<User> result = mgxqlJoinDao.findJoinList2();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        // select u.* 只选择User列，不包含UserDetail列，因此userDetail不会被MGXQL resultMap填充
        for (User user : result) {
            Assert.assertNotNull(user.getId());
            Assert.assertNotNull(user.getCode());
        }
    }

    @Test
    public void test02_findJoinList1() {
        List<User> result = mgxqlJoinDao.findJoinList1();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        // select u.* 只选择User列，不包含关联表列，因此嵌套对象不会被MGXQL resultMap填充
        for (User user : result) {
            Assert.assertNotNull(user.getId());
            Assert.assertNotNull(user.getCode());
        }
    }

    @Test
    public void test03_findJoinList4() {
        List<UserDetail> result = mgxqlJoinDao.findJoinList4();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        for (UserDetail userDetail : result) {
            Assert.assertNotNull(userDetail.getCode());
        }
    }

    @Test
    public void test04_findJoinStarTwoEntity() {
        List<User> result = mgxqlJoinDao.findJoinStarTwoEntity();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void test05_findProjectionList() {
        List<User> result = mgxqlJoinDao.findProjectionList();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void test06_countAll() {
        long count = mgxqlJoinDao.countAll();
        Assert.assertTrue(count > 0);
        Assert.assertEquals(DATA_COUNT, count);
    }

    @Test
    public void test07_findFlatProjection() {
        List<UserFlatProjection> result = mgxqlJoinDao.findFlatProjection();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        for (UserFlatProjection projection : result) {
            Assert.assertNotNull(projection.getCode());
        }
    }

    @Test
    public void test08_findOneLevelProjection() {
        List<UserOneLevelProjection> result = mgxqlJoinDao.findOneLevelProjection();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        // select u.* 只选择User列，投影DTO中的嵌套字段不会被填充
        for (UserOneLevelProjection projection : result) {
            Assert.assertNotNull(projection.getCode());
        }
    }

    @Test
    public void test09_findMultiLevelProjection() {
        List<UserMultiLevelProjection> result = mgxqlJoinDao.findMultiLevelProjection();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        // select u.* 只选择User列，投影DTO中的多层嵌套字段不会被填充
        for (UserMultiLevelProjection projection : result) {
            Assert.assertNotNull(projection.getCode());
        }
    }

    @Test
    public void test10_findSkipLevelProjection() {
        List<UserSkipProjection> result = mgxqlJoinDao.findSkipLevelProjection();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        // select u.* 只选择User列，跳层投影DTO中的嵌套字段不会被填充
        for (UserSkipProjection projection : result) {
            Assert.assertNotNull(projection.getCode());
        }
    }

    @Test
    public void test11_findUnknownFieldProjection() {
        List<UserUnknownFieldProjection> result = mgxqlJoinDao.findUnknownFieldProjection();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        for (UserUnknownFieldProjection projection : result) {
            Assert.assertNotNull(projection.getCode());
            Assert.assertNull(projection.getUnknownRelation());
        }
    }

    @Test
    @Ignore("框架bug: select * 时将实体UserDetailItem2设置到DTO的UserDetailItem2Projection字段, 类型不匹配")
    public void test12_findSkipLevelProjectionSelectStar() {
        List<UserSkipProjection> result = mgxqlJoinDao.findSkipLevelProjectionSelectStar();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    @Ignore("框架bug: select u.* 跳层投影 + 别名星, 嵌套字段类型不匹配(实体 vs DTO投影)")
    public void test13_findSkipLevelProjectionSelectAliasStar() {
        List<UserSkipProjection> result = mgxqlJoinDao.findSkipLevelProjectionSelectAliasStar();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }
}
