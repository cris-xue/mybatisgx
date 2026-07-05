package com.mybatisgx.relation.select.mgxql.onetoone.test;

import com.mybatisgx.relation.select.mgxql.onetoone.dao.MgxqlJoinWhereDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.BeforeClass;
import org.junit.Test;

public class MgxqlJoinWhereDaoTest {

    private static MgxqlJoinWhereDao mgxqlJoinWhereDao;

    @BeforeClass
    public static void setUp() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.select.mgxql.onetoone.dao"}
        );
        mgxqlJoinWhereDao = sqlSession.getMapper(MgxqlJoinWhereDao.class);
    }

    @Test
    public void test01() {
        User user = mgxqlJoinWhereDao.findUser(1L);
    }
}
