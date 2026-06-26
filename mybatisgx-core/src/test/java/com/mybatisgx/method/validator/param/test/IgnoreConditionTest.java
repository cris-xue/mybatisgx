package com.mybatisgx.method.validator.param.test;

import com.mybatisgx.method.validator.param.dao.select.query.IgnoreConditionQueryEntityDao;
import com.mybatisgx.method.validator.param.entity.select.IgnoreConditionUserQuery;
import com.mybatisgx.method.validator.param.entity.select.ValidatorUser;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;

/**
 * 验证 @QueryColumn(ignore = true) 字段不参与自动条件生成，但仍可作为手写方法参数绑定。
 * <p>覆盖：
 * <ul>
 *     <li>test01: 查询实体含 ignore 的非实体字段（startDate/endDate）与 ignore+@Property 共存字段（fooBar）时，启动校验不报错</li>
 *     <li>test02: 自动方法仅按 name 生成条件，startDate 虽非空但不成为条件</li>
 *     <li>test03: 手写 @Statement 方法能将 ignore 字段 startDate 作为参数绑定</li>
 * </ul>
 * @author 薛承城
 * @date 2026/6/26
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IgnoreConditionTest {

    private static final String[] ENTITY_PACKAGES = {"com.mybatisgx.method.validator.param.entity.select"};
    private static final String DAO_PACKAGE = "com.mybatisgx.method.validator.param.dao.select.query";

    private SqlSession initDao() {
        return DaoTestUtils.getSqlSession(ENTITY_PACKAGES, new String[]{DAO_PACKAGE});
    }

    @Test
    public void test01_bootstrapSucceedsWithIgnoredFields() {
        SqlSession sqlSession = initDao();
        Assert.assertNotNull(sqlSession);
        IgnoreConditionQueryEntityDao dao = sqlSession.getMapper(IgnoreConditionQueryEntityDao.class);
        Assert.assertNotNull(dao);
        sqlSession.close();
    }

    @Test
    public void test02_autoMethodQueriesByNameOnly() {
        SqlSession sqlSession = initDao();
        IgnoreConditionQueryEntityDao dao = sqlSession.getMapper(IgnoreConditionQueryEntityDao.class);

        ValidatorUser user = new ValidatorUser();
        user.setId(1111L);
        user.setName("name");
        dao.insert(user);

        IgnoreConditionUserQuery query = new IgnoreConditionUserQuery();
        query.setName("name");
        query.setStartDate("2024-01-01");
        List<ValidatorUser> result = dao.findByName(query);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("name", result.get(0).getName());

        sqlSession.rollback();
        sqlSession.close();
    }

    @Test
    public void test03_manualMethodBindsIgnoredFieldAsParam() {
        SqlSession sqlSession = initDao();
        IgnoreConditionQueryEntityDao dao = sqlSession.getMapper(IgnoreConditionQueryEntityDao.class);

        ValidatorUser user = new ValidatorUser();
        user.setId(2222L);
        user.setName("name");
        dao.insert(user);

        IgnoreConditionUserQuery query = new IgnoreConditionUserQuery();
        query.setStartDate("name");
        int affected = dao.deleteByStartDate(query);
        Assert.assertEquals(1, affected);

        sqlSession.rollback();
        sqlSession.close();
    }
}
