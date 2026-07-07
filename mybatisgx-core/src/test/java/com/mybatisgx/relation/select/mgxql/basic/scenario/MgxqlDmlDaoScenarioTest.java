package com.mybatisgx.relation.select.mgxql.basic.scenario;

import com.mybatisgx.relation.select.mgxql.basic.dao.MgxqlDmlDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.UserDetail;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.dao.UserDetailDao;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.LocalDateTime;

/**
 * MGXQL DELETE / UPDATE 场景测试
 * <p>
 * DML测试使用独立数据，测试后rollback避免影响其他测试类
 *
 * @author 薛承城
 * @date 2026/7/7
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlDmlDaoScenarioTest {

    private static final String DML_CODE_1 = "DML_TEST_CODE_1";
    private static final String DML_CODE_2 = "DML_TEST_CODE_2";
    private static final String DML_CODE_UPDATED = "DML_TEST_CODE_UPDATED";

    private SqlSession sqlSession;
    private MgxqlDmlDao dmlDao;
    private UserDetailDao userDetailDao;

    @Before
    public void setUp() {
        sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.dao", "com.mybatisgx.relation.select.mgxql.basic.dao"}
        );
        dmlDao = sqlSession.getMapper(MgxqlDmlDao.class);
        userDetailDao = sqlSession.getMapper(UserDetailDao.class);
    }

    @After
    public void tearDown() {
        if (sqlSession != null) {
            sqlSession.rollback();
        }
    }

    @Test
    public void test01_deleteByMgxqlId() {
        // 先插入一条专用的DML测试数据
        User user = buildDmlUser(DML_CODE_1);
        dmlDao.insert(user);
        Long userId = user.getId();
        Assert.assertNotNull(userId);

        // 同时插入对应的UserDetail，避免@OneToOne EAGER fetch报错
        insertUserDetail(user);

        // delete by id
        int rows = dmlDao.deleteByMgxqlId(userId);
        Assert.assertTrue("deleteByMgxqlId应返回受影响行数>0", rows > 0);

        // 验证已删除
        User deletedUser = dmlDao.findById(userId);
        Assert.assertNull("删除后findById应返回null", deletedUser);
    }

    @Test
    public void test02_deleteByMgxqlCode() {
        // 先插入一条专用的DML测试数据
        User user = buildDmlUser(DML_CODE_2);
        dmlDao.insert(user);
        Long userId = user.getId();
        Assert.assertNotNull(userId);

        // 同时插入对应的UserDetail，避免@OneToOne EAGER fetch报错
        insertUserDetail(user);

        // delete by code
        int rows = dmlDao.deleteByMgxqlCode(DML_CODE_2);
        Assert.assertTrue("deleteByMgxqlCode应返回受影响行数>0", rows > 0);

        // 验证已删除
        User deletedUser = dmlDao.findById(userId);
        Assert.assertNull("删除后findById应返回null", deletedUser);
    }

    @Test
    public void test03_updateByMgxqlId() {
        // 先插入一条专用的DML测试数据
        User user = buildDmlUser(DML_CODE_1);
        dmlDao.insert(user);
        Long userId = user.getId();
        Assert.assertNotNull(userId);

        // 同时插入对应的UserDetail，避免@OneToOne EAGER fetch报错
        insertUserDetail(user);

        // update by id: 修改code
        // MGXQL update语句会设置实体所有字段，因此需要填充所有NOT NULL字段
        User updateEntity = buildDmlUser(DML_CODE_UPDATED);
        updateEntity.setId(userId);
        int rows = dmlDao.updateByMgxqlId(userId, updateEntity);
        Assert.assertTrue("updateByMgxqlId应返回受影响行数>0", rows > 0);

        // 验证字段已变更
        User updatedUser = dmlDao.findById(userId);
        Assert.assertNotNull(updatedUser);
        Assert.assertEquals("更新后code应变为新值", DML_CODE_UPDATED, updatedUser.getCode());
    }

    @Test
    public void test04_updateByMgxqlCode() {
        // 先插入一条专用的DML测试数据
        User user = buildDmlUser(DML_CODE_2);
        dmlDao.insert(user);
        Long userId = user.getId();
        Assert.assertNotNull(userId);

        // 同时插入对应的UserDetail，避免@OneToOne EAGER fetch报错
        insertUserDetail(user);

        // update by code: 修改code
        // MGXQL update语句会设置实体所有字段，因此需要填充所有NOT NULL字段
        User updateEntity = buildDmlUser(DML_CODE_UPDATED);
        updateEntity.setId(userId);
        int rows = dmlDao.updateByMgxqlCode(DML_CODE_2, updateEntity);
        Assert.assertTrue("updateByMgxqlCode应返回受影响行数>0", rows > 0);

        // 验证字段已变更
        User updatedUser = dmlDao.findById(userId);
        Assert.assertNotNull(updatedUser);
        Assert.assertEquals("更新后code应变为新值", DML_CODE_UPDATED, updatedUser.getCode());
    }

    /**
     * 构建DML测试用的User对象，填充必填字段(inputUserId, inputTime)
     */
    private User buildDmlUser(String code) {
        User user = new User();
        user.setCode(code);
        user.setInputUserId(1L);
        user.setInputTime(LocalDateTime.now());
        return user;
    }

    /**
     * 为User插入对应的UserDetail，避免@OneToOne EAGER fetch查询时报错
     */
    private void insertUserDetail(User user) {
        UserDetail userDetail = new UserDetail();
        userDetail.setCode("DML_DETAIL_" + user.getCode());
        userDetail.setUser(user);
        userDetail.setInputUserId(1L);
        userDetail.setInputTime(LocalDateTime.now());
        userDetailDao.insert(userDetail);
    }
}
