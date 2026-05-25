package com.mybatisgx.relation.select.batch_simple_id.manytomany.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.relation.select.batch_simple_id.manytomany.dao.RoleDao;
import com.mybatisgx.relation.select.batch_simple_id.manytomany.dao.UserDao;
import com.mybatisgx.relation.select.batch_simple_id.manytomany.dao.UserRoleDao;
import com.mybatisgx.relation.select.batch_simple_id.manytomany.entity.Role;
import com.mybatisgx.relation.select.batch_simple_id.manytomany.entity.User;
import com.mybatisgx.relation.select.batch_simple_id.manytomany.entity.UserRole;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UserDaoTest {

    private static int count = 10;
    private static UserDao userDao;
    private static UserRoleDao userRoleDao;
    private static RoleDao roleDao;

    private static List<User> userList;
    private static List<Role> roleList;

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.batch_simple_id.manytomany.entity"},
                new String[]{"com.mybatisgx.relation.select.batch_simple_id.manytomany.dao"}
        );
        userDao = sqlSession.getMapper(UserDao.class);
        userRoleDao = sqlSession.getMapper(UserRoleDao.class);
        roleDao = sqlSession.getMapper(RoleDao.class);

        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        userList = new ArrayList(count);
        roleList = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            userList.add(user);
            roleList.addAll(user.getRoleList());
        }

        List<UserRole> userRoleList = new ArrayList(count);
        long i = 100000;
        for (User user : userList) {
            for (Role role : user.getRoleList()) {
                UserRole userRole = new UserRole();
                userRole.setId(++i);
                userRole.setUserId(user.getId());
                userRole.setRoleId(role.getId());
                userRoleList.add(userRole);
            }
        }

        int insertCount = userDao.insertBatch(userList, 100);
        Assert.assertEquals(userList.size(), insertCount);

        int userRoleInsertCount = userRoleDao.insertBatch(userRoleList, 100);
        Assert.assertEquals(userRoleList.size(), userRoleInsertCount);

        int insertCount1 = roleDao.insertBatch(roleList, 100);
        Assert.assertEquals(roleList.size(), insertCount1);
    }

    @Test
    public void testFindById() {
        User firstUser = userList.get(0);
        User dbUser = userDao.findById(firstUser.getId());
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(firstUser.getId(), dbUser.getId());
        Assert.assertEquals(firstUser.getName(), dbUser.getName());
        Assert.assertNotNull(dbUser.getRoleList());
        Assert.assertEquals(firstUser.getRoleList().size(), dbUser.getRoleList().size());
        for (int i = 0; i < firstUser.getRoleList().size(); i++) {
            Assert.assertEquals(firstUser.getRoleList().get(i).getId(), dbUser.getRoleList().get(i).getId());
        }
    }

    @Test
    public void testFindList() {
        List<User> dbUserList = userDao.findList(new User());
        Assert.assertNotNull(dbUserList);
        Assert.assertEquals(count, dbUserList.size());

        for (int i = 0; i < count; i++) {
            User dbUser = dbUserList.get(i);
            User user = userList.get(i);
            Assert.assertEquals(user.getId(), dbUser.getId());
            Assert.assertNotNull(dbUser.getRoleList());
            Assert.assertEquals(user.getRoleList().size(), dbUser.getRoleList().size());
        }
    }
}
