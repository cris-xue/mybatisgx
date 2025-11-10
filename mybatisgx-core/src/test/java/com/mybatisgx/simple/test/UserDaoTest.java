package com.mybatisgx.simple.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.simple.dao.UserDao;
import com.mybatisgx.simple.entity.User;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UserDaoTest {

    @Test
    public void testInsert() {
        UserDao userDao = DaoTestUtils.getDao(User.class, UserDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);
        int count = userDao.insert(user);
        Assert.assertEquals(1, count);

        User dbUser = userDao.findById(user.getId());
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(user.getId(), dbUser.getId());
    }

    @Test
    public void testDeleteById() {
        UserDao userDao = DaoTestUtils.getDao(User.class, UserDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);
        int insertCount = userDao.insert(user);
        Assert.assertEquals(1, insertCount);

        int deleteCount = userDao.deleteById(user.getId());
        Assert.assertEquals(1, deleteCount);

        User dbUser = userDao.findById(user.getId());
        Assert.assertNull(dbUser);
    }

    @Test
    public void testUpdateById() {
        UserDao userDao = DaoTestUtils.getDao(User.class, UserDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);
        int insertCount = userDao.insert(user);
        Assert.assertEquals(1, insertCount);

        user.setName("test");
        int updateCount = userDao.updateById(user);
        Assert.assertEquals(1, updateCount);

        User dbUser = userDao.findById(user.getId());
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(user.getName(), dbUser.getName());
    }

    @Test
    public void testFindById() {
        UserDao userDao = DaoTestUtils.getDao(User.class, UserDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);
        int insertCount = userDao.insert(user);
        Assert.assertEquals(1, insertCount);

        User dbUser = userDao.findById(user.getId());
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(user.getName(), dbUser.getName());
    }

    @Test
    public void testInsertBatch() {
        UserDao userDao = DaoTestUtils.getDao(User.class, UserDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        int count = 100;
        List<User> userList = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            userList.add(user);
        }

        long startTime = System.currentTimeMillis();
        int insertBatchCount = userDao.insertBatch(userList, 3000);
        long endTime = System.currentTimeMillis();

        Assert.assertEquals(count, insertBatchCount);
        System.out.println("insertBatchCount: " + insertBatchCount + ", time: " + (endTime - startTime));
    }
}
