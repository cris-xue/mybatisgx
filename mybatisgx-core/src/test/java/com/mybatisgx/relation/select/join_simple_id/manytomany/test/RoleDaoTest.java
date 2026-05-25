package com.mybatisgx.relation.select.join_simple_id.manytomany.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.relation.select.join_simple_id.manytomany.dao.RoleDao;
import com.mybatisgx.relation.select.join_simple_id.manytomany.dao.UserDao;
import com.mybatisgx.relation.select.join_simple_id.manytomany.dao.UserRoleDao;
import com.mybatisgx.relation.select.join_simple_id.manytomany.entity.Role;
import com.mybatisgx.relation.select.join_simple_id.manytomany.entity.User;
import com.mybatisgx.relation.select.join_simple_id.manytomany.entity.UserRole;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoTest {
    private static int count = 10;
    private static UserDao userDao; private static UserRoleDao userRoleDao; private static RoleDao roleDao;
    private static List<Role> roleList;

    @BeforeClass public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(new String[]{"com.mybatisgx.relation.select.join_simple_id.manytomany.entity"}, new String[]{"com.mybatisgx.relation.select.join_simple_id.manytomany.dao"});
        userDao = sqlSession.getMapper(UserDao.class); userRoleDao = sqlSession.getMapper(UserRoleDao.class); roleDao = sqlSession.getMapper(RoleDao.class);
        FixtureGenerator fg = new FixtureGenerator(); fg.configure().ignoreCyclicReferences();
        List<User> userList = new ArrayList(count); roleList = new ArrayList(count);
        for (int i = 0; i < count; i++) { User u = fg.createRandomized(User.class); userList.add(u); roleList.addAll(u.getRoleList()); }
        List<UserRole> urList = new ArrayList(count); long id = 200000;
        for (User u : userList) { for (Role r : u.getRoleList()) { UserRole ur = new UserRole(); ur.setId(++id); ur.setUserId(u.getId()); ur.setRoleId(r.getId()); urList.add(ur); } }
        Assert.assertEquals(userList.size(), userDao.insertBatch(userList, 100));
        Assert.assertEquals(urList.size(), userRoleDao.insertBatch(urList, 100));
        Assert.assertEquals(roleList.size(), roleDao.insertBatch(roleList, 100));
    }

    @Test public void testFindById() {
        Role first = roleList.get(0); Role db = roleDao.findById(first.getId());
        Assert.assertNotNull(db); Assert.assertEquals(first.getId(), db.getId()); Assert.assertEquals(first.getName(), db.getName());
        Assert.assertNotNull(db.getUserList()); Assert.assertFalse(db.getUserList().isEmpty());
    }

    @Test public void testFindList() {
        List<Role> dbList = roleDao.findList(new Role());
        Assert.assertNotNull(dbList); Assert.assertEquals(roleList.size(), dbList.size());
        for (int i = 0; i < dbList.size(); i++) { Role db = dbList.get(i); Role r = roleList.get(i); Assert.assertEquals(r.getId(), db.getId()); Assert.assertNotNull(db.getUserList()); Assert.assertFalse(db.getUserList().isEmpty()); }
    }
}
