package com.mybatisgx.relation.select.simple_simple_id.manytomany.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.dao.RoleDao;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.dao.UserDao;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.dao.UserRoleDao;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.entity.Role;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.entity.User;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.entity.UserRole;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RoleDaoTest {

    private static int count = 10;
    private static UserDao userDao;
    private static UserRoleDao userRoleDao;
    private static RoleDao roleDao;

    private static List<Role> roleList;

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.manytomany.entity"},
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.manytomany.dao"}
        );
        userDao = sqlSession.getMapper(UserDao.class);
        userRoleDao = sqlSession.getMapper(UserRoleDao.class);
        roleDao = sqlSession.getMapper(RoleDao.class);

        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        List<User> userList = new ArrayList(count);
        roleList = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            userList.add(user);
            roleList.addAll(user.getRoleList());
        }

        List<UserRole> userRoleList = new ArrayList(count);
        long i = 200000;
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
        Role firstRole = roleList.get(0);
        Role dbRole = roleDao.findById(firstRole.getId());
        Assert.assertNotNull(dbRole);
        Assert.assertEquals(firstRole.getId(), dbRole.getId());
        Assert.assertEquals(firstRole.getName(), dbRole.getName());
        Assert.assertNotNull(dbRole.getUserList());
        Assert.assertFalse(dbRole.getUserList().isEmpty());
    }

    @Test
    public void testFindList() {
        List<Role> dbRoleList = roleDao.findList(new Role());
        Assert.assertNotNull(dbRoleList);
        Assert.assertEquals(roleList.size(), dbRoleList.size());

        for (int i = 0; i < dbRoleList.size(); i++) {
            Role dbRole = dbRoleList.get(i);
            Role role = roleList.get(i);
            Assert.assertEquals(role.getId(), dbRole.getId());
            Assert.assertNotNull(dbRole.getUserList());
            Assert.assertFalse(dbRole.getUserList().isEmpty());
        }
    }
}
