package com.mybatisgx.onetoone.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.dao.UserDao;
import com.mybatisgx.entity.User;
import com.mybatisgx.entity.UserDetail;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDaoTest {

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
    public void testFindList() {
        List<Class<?>> entityClassList = Arrays.asList(User.class, UserDetail.class);
        List<Class<?>> daoClassList = Arrays.asList(UserDao.class);
        UserDao userDao = DaoTestUtils.getDao(entityClassList, daoClassList, UserDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        int count = 10;
        List<User> userList = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            userList.add(user);
        }
        int insertCount = userDao.insertBatch(userList, count);
        Assert.assertEquals(count, insertCount);

        List<User> dbUserList = userDao.findList(new User());
        Assert.assertNotNull(dbUserList);
        for (int i = 0; i < count; i++) {
            User user = userList.get(i);
            User dbUser = dbUserList.get(i);
            dbUser.getUserDetail();
            // Assert.assertEquals(user.getUserDetail().getId(), dbUser.getUserDetail().getId());
        }
    }
}
