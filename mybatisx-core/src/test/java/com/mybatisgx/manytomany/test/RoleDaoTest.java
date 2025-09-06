package com.mybatisgx.manytomany.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.dao.RoleDao;
import com.mybatisgx.dao.UserDao;
import com.mybatisgx.entity.Role;
import com.mybatisgx.entity.User;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoleDaoTest {

    @Test
    public void testFindList() {
        List<Class<?>> entityClassList = Arrays.asList(User.class, Role.class);
        List<Class<?>> daoClassList = Arrays.asList(UserDao.class, RoleDao.class);
        SqlSession sqlSession = DaoTestUtils.getSqlSession(entityClassList, daoClassList);
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        RoleDao roleDao = sqlSession.getMapper(RoleDao.class);

        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        int count = 10;
        List<User> userList = new ArrayList(count);
        List<Role> roleList = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            userList.add(user);

            roleList.addAll(user.getRoleList());
        }
        int insertCount = userDao.insertBatch(userList, count);
        Assert.assertEquals(count, insertCount);
        int insertCount1 = roleDao.insertBatch(roleList, count);
        Assert.assertEquals(roleList.size(), insertCount1);

        List<User> dbUserList = userDao.findList(new User());
        Assert.assertNotNull(dbUserList);
        for (int i = 0; i < count; i++) {
            User dbUser = dbUserList.get(i);
            dbUser.getUserDetail();
        }
        User dbUser = dbUserList.get(0);
        // Assert.assertEquals(dbUser.getId(), dbUser.getUserDetail().getUser().getId());
    }
}
