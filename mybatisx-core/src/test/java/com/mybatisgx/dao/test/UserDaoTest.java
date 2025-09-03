package com.mybatisgx.dao.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.dao.test.entity.User;
import com.mybatisgx.dao.test.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UserDaoTest {

    @Test
    public void test01() {
        UserDao userDao = DaoTestUtils.getDao(User.class, UserDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        User user = fixtureGenerator.createRandomized(User.class);
        int count = userDao.insert(user);
        Assert.assertEquals(1, count);
    }

    @Test
    public void test02() {
        UserDao userDao = DaoTestUtils.getDao(User.class, UserDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        User user = fixtureGenerator.createRandomized(User.class);
        int insertCount = userDao.insert(user);
        Assert.assertEquals(1, insertCount);

        int deleteCount = userDao.deleteById(user.getId());
        Assert.assertEquals(1, deleteCount);
    }

    @Test
    public void testInsertBatch() {
        UserDao userDao = DaoTestUtils.getDao(User.class, UserDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();

        int count = 100000;
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
