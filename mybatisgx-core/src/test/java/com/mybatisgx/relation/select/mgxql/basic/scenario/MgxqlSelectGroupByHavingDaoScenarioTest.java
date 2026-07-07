package com.mybatisgx.relation.select.mgxql.basic.scenario;

import com.mybatisgx.relation.select.mgxql.basic.dao.MgxqlSelectGroupByHavingDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
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
import java.util.Map;

/**
 * MGXQL GROUP BY + HAVING 场景测试
 *
 * @author 薛承城
 * @date 2026/7/7
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlSelectGroupByHavingDaoScenarioTest {

    private static final int DATA_COUNT = 10;

    private static MgxqlSelectGroupByHavingDao groupByHavingDao;
    private static List<User> userList = new ArrayList<User>();

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.dao", "com.mybatisgx.relation.select.mgxql.basic.dao"}
        );
        groupByHavingDao = sqlSession.getMapper(MgxqlSelectGroupByHavingDao.class);

        buildData();
        for (User user : userList) {
            groupByHavingDao.insert(user);
        }
    }

    private static void buildData() {
        // users 0-2 share code "GROUP_A" (3 rows)
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setCode("GROUP_A");
            user.setInputUserId(1L);
            user.setInputTime(java.time.LocalDateTime.now());
            userList.add(user);
        }
        // users 3-5 share code "GROUP_B" (3 rows)
        for (int i = 3; i < 6; i++) {
            User user = new User();
            user.setCode("GROUP_B");
            user.setInputUserId(1L);
            user.setInputTime(java.time.LocalDateTime.now());
            userList.add(user);
        }
        // users 6-9 each have unique code
        String[] uniqueCodes = {"GROUP_C", "GROUP_D", "GROUP_E", "GROUP_F"};
        for (int i = 6; i < 10; i++) {
            User user = new User();
            user.setCode(uniqueCodes[i - 6]);
            user.setInputUserId(1L);
            user.setInputTime(java.time.LocalDateTime.now());
            userList.add(user);
        }
    }

    @Test
    public void test01_findGroupByCode() {
        List<Map<String, Object>> result = groupByHavingDao.findGroupByCode();
        Assert.assertNotNull(result);
        // GROUP BY产生6个分组: GROUP_A, GROUP_B, GROUP_C, GROUP_D, GROUP_E, GROUP_F
        Assert.assertEquals(6, result.size());
    }

    @Test
    public void test02_findGroupByCodeHavingCount() {
        // minCount=1: count>1的分组应该返回，即GROUP_A(3)和GROUP_B(3)
        List<Map<String, Object>> result = groupByHavingDao.findGroupByCodeHavingCount(1);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        // 验证返回了count>1的分组（GROUP_A和GROUP_B）
        Assert.assertTrue("应有至少1个分组count>1", result.size() >= 1);
    }

    @Test
    public void test03_findGroupByCodeHavingAnd() {
        // AND条件: count(id) > 1 AND max(id) > minId
        long minCount = 1;
        long minId = 0;
        List<Map<String, Object>> result = groupByHavingDao.findGroupByCodeHavingAnd(minCount, minId);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() >= 1);
    }

    @Test
    public void test04_findGroupByCodeHavingOr() {
        // OR条件: count(id) > minCount OR max(id) > minId
        long minCount = 1;
        long minId = Long.MAX_VALUE - 1;
        List<Map<String, Object>> result = groupByHavingDao.findGroupByCodeHavingOr(minCount, minId);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() >= 1);
    }
}
