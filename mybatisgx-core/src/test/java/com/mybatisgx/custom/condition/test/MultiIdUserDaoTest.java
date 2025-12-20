package com.mybatisgx.custom.condition.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.custom.condition.dao.MultiIdUserDao;
import com.mybatisgx.custom.condition.entity.MultiIdUser;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MultiIdUserDaoTest {

    private static MultiIdUserDao multiIdUserDao;
    private static List<MultiIdUser> multiIdUserList = new ArrayList<>();

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.custom.condition.entity"},
                new String[]{"com.mybatisgx.custom.condition.dao"}
        );
        multiIdUserDao = sqlSession.getMapper(MultiIdUserDao.class);

        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        int count = 100;
        for (int i = 0; i < count; i++) {
            MultiIdUser multiIdUser = fixtureGenerator.createRandomized(MultiIdUser.class);
            multiIdUser.setId(null);
            multiIdUserList.add(multiIdUser);
        }
        multiIdUserDao.insertBatch(multiIdUserList, 3000);
    }

    @Test
    public void testInsert() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        MultiIdUser user = fixtureGenerator.createRandomized(MultiIdUser.class);
        user.setId(null);
        int count = multiIdUserDao.insert(user);
        Assert.assertEquals(1, count);

        MultiIdUser dbUser = multiIdUserDao.findById(user.getId());
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(user.getId().getId1(), dbUser.getId().getId1());
    }

    @Test
    public void testInsertSelective() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        MultiIdUser user = fixtureGenerator.createRandomized(MultiIdUser.class);
        user.setId(null);
        int count = multiIdUserDao.insertSelective(user);
        Assert.assertEquals(1, count);

        MultiIdUser dbUser = multiIdUserDao.findById(user.getId());
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(user.getId().getId1(), dbUser.getId().getId1());
    }

    @Test
    public void testInsertBatch() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        int count = 100;
        List<MultiIdUser> userList = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            MultiIdUser user = fixtureGenerator.createRandomized(MultiIdUser.class);
            user.setId(null);
            userList.add(user);
        }

        long startTime = System.currentTimeMillis();
        int insertBatchCount = multiIdUserDao.insertBatch(userList, 3000);
        long endTime = System.currentTimeMillis();

        Assert.assertEquals(count, insertBatchCount);
        System.out.println("insertBatchCount: " + insertBatchCount + ", time: " + (endTime - startTime));
    }
}
