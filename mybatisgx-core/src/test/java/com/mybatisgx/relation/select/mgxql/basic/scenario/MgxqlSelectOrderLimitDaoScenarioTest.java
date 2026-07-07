package com.mybatisgx.relation.select.mgxql.basic.scenario;

import com.mybatisgx.relation.select.mgxql.basic.dao.MgxqlSelectOrderLimitDao;
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

/**
 * MGXQL ORDER BY + LIMIT 场景测试
 *
 * @author 薛承城
 * @date 2026/7/7
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlSelectOrderLimitDaoScenarioTest {

    private static final int DATA_COUNT = 10;

    private static SqlSession sqlSession;
    private static MgxqlSelectOrderLimitDao orderLimitDao;
    private static List<User> userList = new ArrayList<User>();

    @BeforeClass
    public static void beforeClass() {
        sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.dao", "com.mybatisgx.relation.select.mgxql.basic.dao"}
        );
        orderLimitDao = sqlSession.getMapper(MgxqlSelectOrderLimitDao.class);

        buildData();
        for (User user : userList) {
            orderLimitDao.insert(user);
        }
    }

    private static void buildData() {
        // 10个User，部分code相同用于多字段排序测试
        String[] codes = {"CODE_A", "CODE_A", "CODE_B", "CODE_B", "CODE_C",
                          "CODE_C", "CODE_D", "CODE_D", "CODE_E", "CODE_E"};
        for (int i = 0; i < DATA_COUNT; i++) {
            User user = new User();
            user.setCode(codes[i]);
            user.setInputUserId(1L);
            user.setInputTime(java.time.LocalDateTime.now());
            userList.add(user);
        }
    }

    @Test
    public void test01_findOrderByIdAsc() {
        List<User> result = orderLimitDao.findOrderByIdAsc();
        Assert.assertNotNull(result);
        Assert.assertEquals(DATA_COUNT, result.size());

        // 验证升序：每个user的id <= 下一个user的id
        for (int i = 0; i < result.size() - 1; i++) {
            Long currentId = result.get(i).getId();
            Long nextId = result.get(i + 1).getId();
            Assert.assertTrue(
                    "升序排列时id应递增，当前id=" + currentId + "，下一个id=" + nextId,
                    currentId.compareTo(nextId) <= 0
            );
        }
    }

    @Test
    public void test02_findOrderByIdDesc() {
        List<User> result = orderLimitDao.findOrderByIdDesc();
        Assert.assertNotNull(result);
        Assert.assertEquals(DATA_COUNT, result.size());

        // 验证降序：每个user的id >= 下一个user的id
        for (int i = 0; i < result.size() - 1; i++) {
            Long currentId = result.get(i).getId();
            Long nextId = result.get(i + 1).getId();
            Assert.assertTrue(
                    "降序排列时id应递减，当前id=" + currentId + "，下一个id=" + nextId,
                    currentId.compareTo(nextId) >= 0
            );
        }
    }

    @Test
    public void test03_findOrderByCodeAndId() {
        // order by u.code asc, u.id desc
        List<User> result = orderLimitDao.findOrderByCodeAndId();
        Assert.assertNotNull(result);
        Assert.assertEquals(DATA_COUNT, result.size());

        // 验证排序：code升序，同code内id降序
        for (int i = 0; i < result.size() - 1; i++) {
            String currentCode = result.get(i).getCode();
            String nextCode = result.get(i + 1).getCode();
            int codeCompare = currentCode.compareTo(nextCode);

            // code必须升序
            Assert.assertTrue(
                    "code应升序排列，当前code=" + currentCode + "，下一个code=" + nextCode,
                    codeCompare <= 0
            );

            // 同code内id必须降序
            if (codeCompare == 0) {
                Long currentId = result.get(i).getId();
                Long nextId = result.get(i + 1).getId();
                Assert.assertTrue(
                        "相同code内id应降序，code=" + currentCode + "，当前id=" + currentId + "，下一个id=" + nextId,
                        currentId.compareTo(nextId) >= 0
                );
            }
        }
    }

    @Test
    public void test04_findLimit() {
        // limit 0, 5: 从第0行开始取5条
        List<User> result = orderLimitDao.findLimit();
        Assert.assertNotNull(result);
        Assert.assertEquals(5, result.size());
    }

    @Test
    public void test05_findLimitOffset() {
        // limit 5, 5: 从第5行开始取5条
        List<User> page1 = orderLimitDao.findLimit();
        List<User> page2 = orderLimitDao.findLimitOffset();

        Assert.assertNotNull(page2);
        Assert.assertEquals(5, page2.size());

        // 第二页的第一条id应该大于第一页最后一条id（因为都是按id升序）
        Long page1MaxId = page1.get(page1.size() - 1).getId();
        Long page2FirstId = page2.get(0).getId();
        Assert.assertTrue(
                "第二页首条id应大于第一页末条id，page1MaxId=" + page1MaxId + "，page2FirstId=" + page2FirstId,
                page2FirstId.compareTo(page1MaxId) > 0
        );
    }
}
