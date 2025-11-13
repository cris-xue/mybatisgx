package com.mybatisgx.simple.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.dao.Page;
import com.mybatisgx.dao.Pageable;
import com.mybatisgx.simple.dao.UserDao;
import com.mybatisgx.simple.entity.User;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UserDaoTest {

    private static UserDao userDao;
    private static List<User> userList = new ArrayList<>();

    @BeforeClass
    public static void beforeClass() {
        userDao = DaoTestUtils.getDao(User.class, UserDao.class);

        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        int count = 100;
        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            userList.add(user);
        }
        userDao.insertBatch(userList, 3000);
    }

    @Test
    public void testInsert() {
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
    public void testInsertBatch() {
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

    @Test
    public void testDeleteById() {
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
    public void testFindPage() {
        Pageable pageable = new Pageable();
        pageable.setPageNo(1);
        pageable.setPageSize(10);
        Page<User> page2 = userDao.findPage(new User(), pageable);
        Assert.assertNotNull(page2);
    }
}
