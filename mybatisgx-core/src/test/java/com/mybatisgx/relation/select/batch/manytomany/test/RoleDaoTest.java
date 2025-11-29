package com.mybatisgx.relation.select.batch.manytomany.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.relation.select.batch.manytomany.dao.RoleDao;
import com.mybatisgx.relation.select.batch.manytomany.dao.UserDao;
import com.mybatisgx.relation.select.batch.manytomany.dao.UserRoleDao;
import com.mybatisgx.relation.select.batch.manytomany.entity.Role;
import com.mybatisgx.relation.select.batch.manytomany.entity.User;
import com.mybatisgx.relation.select.batch.manytomany.entity.UserRole;
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

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.batch.manytomany.entity"},
                new String[]{"com.mybatisgx.relation.select.batch.manytomany.dao"}
        );
        userDao = sqlSession.getMapper(UserDao.class);
        userRoleDao = sqlSession.getMapper(UserRoleDao.class);
        roleDao = sqlSession.getMapper(RoleDao.class);

        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        List<User> userList = new ArrayList(count);
        List<Role> roleList = new ArrayList(count);
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
    public void testFindList() {
        List<Role> dbRoleList = roleDao.findList(new Role());
        Assert.assertNotNull(dbRoleList);
        for (int i = 0; i < count; i++) {
            Role dbRole = dbRoleList.get(i);
            dbRole.getUserList();
        }
    }
}
