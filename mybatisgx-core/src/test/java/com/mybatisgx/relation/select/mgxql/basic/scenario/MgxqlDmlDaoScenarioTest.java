package com.mybatisgx.relation.select.mgxql.basic.scenario;

import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.relation.select.mgxql.basic.dao.MgxqlDmlDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.UserDetail;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.dao.UserDetailDao;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.LocalDateTime;
import java.util.HashMap;

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
    public void test03_deleteDynamicWhereSkipsNullCodeInBoundSql() {
        MybatisgxConfiguration configuration = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.dao", "com.mybatisgx.relation.select.mgxql.basic.dao"}
        );
        String statementId = MgxqlDmlDao.class.getName() + ".deleteByMgxqlDynamicCode";
        MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("code", null);
        BoundSql boundSql = mappedStatement.getBoundSql(params);
        String sql = boundSql.getSql();

        Assert.assertFalse("#[code = :code] 在 code 为空时不应输出 code 条件", sql.contains("code ="));
    }

    @Test
    public void test04_updateDynamicWhereSkipsNullCodeInBoundSql() {
        MybatisgxConfiguration configuration = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.dao", "com.mybatisgx.relation.select.mgxql.basic.dao"}
        );
        String statementId = MgxqlDmlDao.class.getName() + ".updateByMgxqlDynamicCode";
        MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("code", null);
        User entity = buildDmlUser(DML_CODE_UPDATED);
        params.put("arg1", entity);
        BoundSql boundSql = mappedStatement.getBoundSql(params);
        String sql = boundSql.getSql();

        Assert.assertTrue("UPDATE SET 静态文本应保留", sql.contains("update simple_oto_user_simple"));
        Assert.assertFalse("#[code = :code] 在 code 为空时不应输出 WHERE code 条件", sql.contains(" where") && sql.contains("code ="));
    }

    @Test
    public void test05_updateSetUsesMgxsqlSetSubset() {
        MybatisgxConfiguration configuration = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.dao", "com.mybatisgx.relation.select.mgxql.basic.dao"}
        );
        String statementId = MgxqlDmlDao.class.getName() + ".updateByMgxqlDynamicCode";
        MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("code", "missing-code");
        User entity = buildDmlUser(null);
        params.put("arg1", entity);
        BoundSql boundSql = mappedStatement.getBoundSql(params);
        String sql = boundSql.getSql();

        Assert.assertTrue("UPDATE 静态文本应保留", sql.toLowerCase().contains("update simple_oto_user_simple"));
        Assert.assertTrue("非空字段应进入 SET", sql.contains("input_user_id ="));
        Assert.assertFalse("动态 SET 不应保留旧 trim 标签文本", sql.contains("<trim"));
        Assert.assertFalse("实体 code 为空时 SET 不应输出 code 赋值", sql.contains("set code =") || sql.contains("SET code ="));
    }

    @Test
    public void test06_updateByMgxqlId() {
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
    public void test07_updateByMgxqlCode() {
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
