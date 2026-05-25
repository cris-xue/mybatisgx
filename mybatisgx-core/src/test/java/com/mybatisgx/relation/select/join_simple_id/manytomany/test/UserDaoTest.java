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

public class UserDaoTest {
    private static int count = 10;
    private static UserDao userDao; private static UserRoleDao userRoleDao; private static RoleDao roleDao;
    private static List<User> userList; private static List<Role> roleList;

    @BeforeClass public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(new String[]{"com.mybatisgx.relation.select.join_simple_id.manytomany.entity"}, new String[]{"com.mybatisgx.relation.select.join_simple_id.manytomany.dao"});
        userDao = sqlSession.getMapper(UserDao.class); userRoleDao = sqlSession.getMapper(UserRoleDao.class); roleDao = sqlSession.getMapper(RoleDao.class);
        FixtureGenerator fg = new FixtureGenerator(); fg.configure().ignoreCyclicReferences();
        userList = new ArrayList(count); roleList = new ArrayList(count);
        for (int i = 0; i < count; i++) { User u = fg.createRandomized(User.class); userList.add(u); roleList.addAll(u.getRoleList()); }
        List<UserRole> urList = new ArrayList(count); long id = 100000;
        for (User u : userList) { for (Role r : u.getRoleList()) { UserRole ur = new UserRole(); ur.setId(++id); ur.setUserId(u.getId()); ur.setRoleId(r.getId()); urList.add(ur); } }
        Assert.assertEquals(userList.size(), userDao.insertBatch(userList, 100));
        Assert.assertEquals(urList.size(), userRoleDao.insertBatch(urList, 100));
        Assert.assertEquals(roleList.size(), roleDao.insertBatch(roleList, 100));
    }

    @Test public void testFindById() {
        User first = userList.get(0); User db = userDao.findById(first.getId());
        Assert.assertNotNull(db); Assert.assertEquals(first.getId(), db.getId()); Assert.assertEquals(first.getName(), db.getName());
        Assert.assertNotNull(db.getRoleList()); Assert.assertEquals(first.getRoleList().size(), db.getRoleList().size());
        for (int i = 0; i < first.getRoleList().size(); i++) Assert.assertEquals(first.getRoleList().get(i).getId(), db.getRoleList().get(i).getId());
    }

    @Test public void testFindList() {
        List<User> dbList = userDao.findList(new User());
        Assert.assertNotNull(dbList); Assert.assertEquals(count, dbList.size());
        for (int i = 0; i < count; i++) { User db = dbList.get(i); User u = userList.get(i); Assert.assertEquals(u.getId(), db.getId()); Assert.assertNotNull(db.getRoleList()); Assert.assertEquals(u.getRoleList().size(), db.getRoleList().size()); }
    }
}
