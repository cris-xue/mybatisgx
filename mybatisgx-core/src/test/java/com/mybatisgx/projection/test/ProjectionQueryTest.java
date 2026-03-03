package com.mybatisgx.projection.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.projection.dao.UserDao;
import com.mybatisgx.projection.dto.UserDto;
import com.mybatisgx.projection.entity.User;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class ProjectionQueryTest {

    private static UserDao userDao;

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.projection.entity"},
                new String[]{"com.mybatisgx.projection.dao"}
        );
        userDao = sqlSession.getMapper(UserDao.class);
    }

    @Test
    public void testFindById() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);
        int insertCount = userDao.insert(user);
        Assert.assertEquals(1, insertCount);

        List<UserDto> dbUserList = userDao.findByName(user.getName());
        Assert.assertNotNull(dbUserList);
        Assert.assertEquals(user.getName(), dbUserList.get(0).getName());
    }
}
