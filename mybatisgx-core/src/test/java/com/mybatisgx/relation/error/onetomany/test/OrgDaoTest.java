package com.mybatisgx.relation.error.onetomany.test;

import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

public class OrgDaoTest {

    @Test
    public void testError01() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.error.onetomany.entity"},
                new String[]{"com.mybatisgx.relation.error.onetomany.dao"}
        );
    }
}
