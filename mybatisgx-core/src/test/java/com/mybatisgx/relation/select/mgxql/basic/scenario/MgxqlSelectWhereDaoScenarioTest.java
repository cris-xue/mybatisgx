package com.mybatisgx.relation.select.mgxql.basic.scenario;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.relation.select.mgxql.basic.dao.MgxqlSelectWhereDao;
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
import java.util.Arrays;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlSelectWhereDaoScenarioTest {

    private static final int DATA_COUNT = 10;
    private static MgxqlSelectWhereDao mgxqlSelectWhereDao;
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
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.dao", "com.mybatisgx.relation.select.mgxql.basic.dao"}
        );
        mgxqlSelectWhereDao = sqlSession.getMapper(MgxqlSelectWhereDao.class);
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
            // 设置可预测的code值，方便LIKE等测试断言
            if (i == 0) {
                user.setCode(null);
            } else {
                user.setCode("usercode_" + i);
            }

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
    public void test01_findUserById() {
        User expected = userList.get(1);
        User result = mgxqlSelectWhereDao.findUserById(expected.getId());
        Assert.assertNotNull(result);
        Assert.assertEquals(expected.getId(), result.getId());
        Assert.assertEquals(expected.getCode(), result.getCode());
    }

    @Test
    public void test02_findByCodeNotEq() {
        String code = userList.get(1).getCode();
        List<User> result = mgxqlSelectWhereDao.findByCodeNotEq(code);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);
        for (User user : result) {
            Assert.assertNotEquals(code, user.getCode());
        }
        // 排除code为null的记录和指定code的记录，剩余应全部返回
        // userList中code为null的有1条(index 0)，code等于指定值的有1条，所以期望返回 DATA_COUNT - 2 = 8
        Assert.assertEquals(DATA_COUNT - 2, result.size());
    }

    @Test
    public void test03_findByIdLessThan() {
        Long threshold = userList.get(5).getId();
        List<User> result = mgxqlSelectWhereDao.findByIdLessThan(threshold);
        Assert.assertNotNull(result);
        for (User user : result) {
            Assert.assertTrue(user.getId() < threshold);
        }
        // id小于threshold的有5条(index 0-4)
        Assert.assertEquals(5, result.size());
    }

    @Test
    public void test04_findByIdLessThanEqual() {
        Long threshold = userList.get(5).getId();
        List<User> result = mgxqlSelectWhereDao.findByIdLessThanEqual(threshold);
        Assert.assertNotNull(result);
        for (User user : result) {
            Assert.assertTrue(user.getId() <= threshold);
        }
        // id小于等于threshold的有6条(index 0-5)
        Assert.assertEquals(6, result.size());
    }

    @Test
    public void test05_findByIdGreaterThan() {
        Long threshold = userList.get(5).getId();
        List<User> result = mgxqlSelectWhereDao.findByIdGreaterThan(threshold);
        Assert.assertNotNull(result);
        for (User user : result) {
            Assert.assertTrue(user.getId() > threshold);
        }
        // id大于threshold的有4条(index 6-9)
        Assert.assertEquals(4, result.size());
    }

    @Test
    public void test06_findByIdGreaterThanEqual() {
        Long threshold = userList.get(5).getId();
        List<User> result = mgxqlSelectWhereDao.findByIdGreaterThanEqual(threshold);
        Assert.assertNotNull(result);
        for (User user : result) {
            Assert.assertTrue(user.getId() >= threshold);
        }
        // id大于等于threshold的有5条(index 5-9)
        Assert.assertEquals(5, result.size());
    }

    @Test
    public void test07_findByCodeLike() {
        String pattern = "%usercode%";
        List<User> result = mgxqlSelectWhereDao.findByCodeLike(pattern);
        Assert.assertNotNull(result);
        // code匹配 %usercode% 的有9条(index 1-9)，index 0的code为null不匹配
        Assert.assertEquals(9, result.size());
        for (User user : result) {
            Assert.assertNotNull(user.getCode());
            Assert.assertTrue(user.getCode().contains("usercode"));
        }
    }

    @Test
    public void test08_findByCodeLeftLike() {
        String pattern = "usercode%";
        List<User> result = mgxqlSelectWhereDao.findByCodeLeftLike(pattern);
        Assert.assertNotNull(result);
        // code以"usercode"开头的有9条(index 1-9)
        Assert.assertEquals(9, result.size());
        for (User user : result) {
            Assert.assertNotNull(user.getCode());
            Assert.assertTrue(user.getCode().startsWith("usercode"));
        }
    }

    @Test
    public void test09_findByCodeRightLike() {
        String pattern = "%_5";
        List<User> result = mgxqlSelectWhereDao.findByCodeRightLike(pattern);
        Assert.assertNotNull(result);
        // code以"_5"结尾的只有usercode_5(index 5)
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("usercode_5", result.get(0).getCode());
    }

    @Test
    public void test10_findByIdIn() {
        Long id1 = userList.get(1).getId();
        Long id2 = userList.get(3).getId();
        Long id3 = userList.get(7).getId();
        List<Long> ids = Arrays.asList(id1, id2, id3);
        List<User> result = mgxqlSelectWhereDao.findByIdIn(ids);
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        for (User user : result) {
            Assert.assertTrue(ids.contains(user.getId()));
        }
    }

    @Test
    public void test11_findByIdBetween() {
        Long low = userList.get(2).getId();
        Long high = userList.get(6).getId();
        List<Long> ids = Arrays.asList(low, high);
        List<User> result = mgxqlSelectWhereDao.findByIdBetween(ids);
        Assert.assertNotNull(result);
        // id在low和high之间的有5条(index 2-6)
        Assert.assertEquals(5, result.size());
        for (User user : result) {
            Assert.assertTrue(user.getId() >= low && user.getId() <= high);
        }
    }

    @Test
    public void test12_findByCodeIsNull() {
        List<User> result = mgxqlSelectWhereDao.findByCodeIsNull();
        Assert.assertNotNull(result);
        // 只有index 0的code为null
        Assert.assertEquals(1, result.size());
        Assert.assertNull(result.get(0).getCode());
    }

    @Test
    public void test13_findByCodeIsNotNull() {
        List<User> result = mgxqlSelectWhereDao.findByCodeIsNotNull();
        Assert.assertNotNull(result);
        // index 1-9的code不为null
        Assert.assertEquals(9, result.size());
        for (User user : result) {
            Assert.assertNotNull(user.getCode());
        }
    }

    @Test
    public void test14_findOrCondition() {
        Long id = userList.get(1).getId();
        String code = userList.get(5).getCode();
        List<User> result = mgxqlSelectWhereDao.findOrCondition(id, code);
        Assert.assertNotNull(result);
        // 匹配id=index1 OR code=usercode_5的记录
        for (User user : result) {
            boolean matchId = user.getId().equals(id);
            boolean matchCode = code.equals(user.getCode());
            Assert.assertTrue(matchId || matchCode);
        }
        // 至少有2条匹配
        Assert.assertTrue(result.size() >= 2);
    }

    @Test
    public void test15_findAndOrCondition() {
        Long id = userList.get(3).getId();
        String code = "%usercode%";
        List<User> result = mgxqlSelectWhereDao.findAndOrCondition(id, code);
        Assert.assertNotNull(result);
        // 条件: id > :id AND (code like :code OR code IS NOT NULL)
        // id > userList.get(3).getId()的是index 4-9(6条)
        // 这些记录的code都匹配 like %usercode% 且都不为null，所以6条都应返回
        Assert.assertEquals(6, result.size());
        for (User user : result) {
            Assert.assertTrue(user.getId() > id);
        }
    }

    @Test
    public void test16_findNotIn() {
        Long id1 = userList.get(1).getId();
        Long id2 = userList.get(3).getId();
        List<Long> ids = Arrays.asList(id1, id2);
        List<User> result = mgxqlSelectWhereDao.findNotIn(ids);
        Assert.assertNotNull(result);
        // 排除2条，剩余8条
        Assert.assertEquals(DATA_COUNT - 2, result.size());
        for (User user : result) {
            Assert.assertFalse(ids.contains(user.getId()));
        }
    }

    @Test
    public void test17_findNotLike() {
        String pattern = "%usercode_5%";
        List<User> result = mgxqlSelectWhereDao.findNotLike(pattern);
        Assert.assertNotNull(result);
        // 不匹配 %usercode_5% 的记录：排除usercode_5(1条)和code为null的记录(1条)
        // SQL标准: NULL NOT LIKE pattern 返回 NULL，NULL行不会被返回
        Assert.assertEquals(8, result.size());
        for (User user : result) {
            Assert.assertNotNull(user.getCode());
            Assert.assertFalse(user.getCode().contains("usercode_5"));
        }
    }

    @Test
    public void test18_findNotBetween() {
        Long low = userList.get(2).getId();
        Long high = userList.get(6).getId();
        List<Long> ids = Arrays.asList(low, high);
        List<User> result = mgxqlSelectWhereDao.findNotBetween(ids);
        Assert.assertNotNull(result);
        // id不在[low, high]范围内的有5条(index 0,1,7,8,9)
        Assert.assertEquals(5, result.size());
        for (User user : result) {
            Assert.assertTrue(user.getId() < low || user.getId() > high);
        }
    }
}
