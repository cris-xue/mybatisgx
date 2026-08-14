package com.mybatisgx.relation.select.mgxql.basic.scenario;

import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.relation.select.mgxql.basic.dao.MgxqlEntityParamSelectDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.dao.UserDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.dao.UserDetailDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.MgxqlEntityParamUserQuery;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.UserDetail;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MGXQL 实体参数 SELECT 端到端场景测试
 *
 * @author 薛承城
 * @date 2026/7/24
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlEntityParamSelectDaoScenarioTest {

    private static final String CODE_PREFIX = "MGXQL_ENTITY_PARAM_CODE_";
    private static final Long INPUT_USER_ID = 8601L;
    private static final Long UPDATE_USER_ID = 8602L;
    private static MgxqlEntityParamSelectDao mgxqlEntityParamSelectDao;
    private static List<User> userList = new ArrayList<User>();

    @BeforeClass
    public static void setUp() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.dao", "com.mybatisgx.relation.select.mgxql.basic.dao"}
        );
        mgxqlEntityParamSelectDao = sqlSession.getMapper(MgxqlEntityParamSelectDao.class);
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        UserDetailDao userDetailDao = sqlSession.getMapper(UserDetailDao.class);

        for (int i = 0; i < 5; i++) {
            User user = buildUser(CODE_PREFIX + i, INPUT_USER_ID, i == 3 ? UPDATE_USER_ID : null);
            userDao.insert(user);
            userList.add(user);
            userDetailDao.insert(buildUserDetail(user));
        }
    }

    @Test
    public void test01_entityParamPlainConditionUsesEntityFieldNamespace() {
        User query = new User();
        query.setCode(userList.get(1).getCode());

        List<User> result = mgxqlEntityParamSelectDao.findByEntityCode(query);

        assertCodes(result, userList.get(1).getCode());
    }

    @Test
    public void test02_entityParamBracketSkipsNullConditionInBoundSql() {
        MybatisgxConfiguration configuration = DaoTestUtils.getMybatisgxConfiguration(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.dao", "com.mybatisgx.relation.select.mgxql.basic.dao"}
        );
        String statementId = MgxqlEntityParamSelectDao.class.getName() + ".findByEntityBracketCode";
        MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
        User query = new User();

        BoundSql boundSql = mappedStatement.getBoundSql(query);
        String sql = boundSql.getSql();

        Assert.assertFalse("#[u.code = :code] 在实体 code 为空时不应输出 code 条件", sql.contains("code ="));
    }

    @Test
    public void test03_entityParamIfUsesCustomGuard() {
        User query = new User();
        query.setCode(userList.get(2).getCode());

        List<User> result = mgxqlEntityParamSelectDao.findByEntityIfCode(query);

        assertCodes(result, userList.get(2).getCode());
    }

    @Test
    public void test04_entityParamChooseUsesWhenBranch() {
        User query = new User();
        query.setCode(userList.get(4).getCode());

        List<User> result = mgxqlEntityParamSelectDao.findByEntityChooseCode(query);

        assertCodes(result, userList.get(4).getCode());
    }

    @Test
    public void test05_entityParamLikeUsesMgxsqlBind() {
        User query = new User();
        query.setCode("ENTITY_PARAM_CODE");

        List<User> result = mgxqlEntityParamSelectDao.findByEntityCodeLike(query);

        Assert.assertTrue("LIKE 应至少命中本测试插入的数据", result.size() >= userList.size());
        for (User user : result) {
            Assert.assertTrue(user.getCode().contains("ENTITY_PARAM_CODE"));
        }
    }

    @Test
    public void test06_queryEntityParamSimpleInUsesEntityNamespace() {
        MgxqlEntityParamUserQuery query = new MgxqlEntityParamUserQuery();
        List<Long> ids = Arrays.asList(userList.get(0).getId(), userList.get(2).getId(), userList.get(4).getId());
        query.setIdIn(ids);

        List<User> result = mgxqlEntityParamSelectDao.findByQueryEntityIdIn(query);

        Assert.assertEquals(ids.size(), result.size());
        for (User user : result) {
            Assert.assertTrue(ids.contains(user.getId()));
        }
    }

    @Test
    public void test07_queryEntityParamComplexInUsesEntityNamespace() {
        MgxqlEntityParamUserQuery query = new MgxqlEntityParamUserQuery();
        List<User> users = Arrays.asList(userList.get(1), userList.get(3));
        query.setUserList(users);
        Set<Long> ids = new HashSet<Long>();
        ids.add(userList.get(1).getId());
        ids.add(userList.get(3).getId());

        List<User> result = mgxqlEntityParamSelectDao.findByQueryEntityComplexIn(query);

        Assert.assertEquals(users.size(), result.size());
        for (User user : result) {
            Assert.assertTrue(ids.contains(user.getId()));
        }
    }

    @Test
    public void test08_entityParamBracketAndOrChainKeepsConnectors() {
        User query = new User();
        query.setCode(userList.get(0).getCode());
        query.setInputUserId(INPUT_USER_ID);
        query.setUpdateUserId(UPDATE_USER_ID);

        List<User> result = mgxqlEntityParamSelectDao.findByEntityBracketAndOr(query);

        Set<String> codes = new HashSet<String>();
        for (User user : result) {
            codes.add(user.getCode());
        }
        Assert.assertTrue(codes.contains(userList.get(0).getCode()));
        Assert.assertTrue(codes.contains(userList.get(3).getCode()));
    }

    private static User buildUser(String code, Long inputUserId, Long updateUserId) {
        User user = new User();
        user.setCode(code);
        user.setInputUserId(inputUserId);
        user.setInputTime(LocalDateTime.now());
        user.setUpdateUserId(updateUserId);
        user.setUpdateTime(updateUserId != null ? LocalDateTime.now() : null);
        return user;
    }

    private static UserDetail buildUserDetail(User user) {
        UserDetail userDetail = new UserDetail();
        userDetail.setCode("DETAIL_" + user.getCode());
        userDetail.setUser(user);
        userDetail.setInputUserId(INPUT_USER_ID);
        userDetail.setInputTime(LocalDateTime.now());
        return userDetail;
    }

    private void assertCodes(List<User> result, String code) {
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(code, result.get(0).getCode());
    }
}
