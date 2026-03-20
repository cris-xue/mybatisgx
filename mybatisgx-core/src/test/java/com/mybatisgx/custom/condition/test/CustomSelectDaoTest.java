package com.mybatisgx.custom.condition.test;

import com.mybatisgx.custom.condition.dao.CustomSelectDao;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author：薛承城
 * @description：一句话描述
 * @date：2026/3/20 14:03
 */
public class CustomSelectDaoTest {

    private static SqlSession sqlSession;

    @BeforeClass
    public static void beforeClass() {
        sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.custom.condition.entity"},
                new String[]{"com.mybatisgx.custom.condition.dao"}
        );
    }

    @Test
    public void testInsert() {
        CustomSelectDao customSelectDao = sqlSession.getMapper(CustomSelectDao.class);
        customSelectDao.findOne(null);
    }
}
