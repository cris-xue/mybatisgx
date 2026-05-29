package com.mybatisgx.relation.select.join_simple_id.manytomany.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.relation.select.join_simple_id.manytomany.dao.MenuDao;
import com.mybatisgx.relation.select.join_simple_id.manytomany.dao.MenuResourceJoinDao;
import com.mybatisgx.relation.select.join_simple_id.manytomany.dao.ResourceDao;
import com.mybatisgx.relation.select.join_simple_id.manytomany.dao.RoleDao;
import com.mybatisgx.relation.select.join_simple_id.manytomany.dao.RoleMenuJoinDao;
import com.mybatisgx.relation.select.join_simple_id.manytomany.dao.UserDao;
import com.mybatisgx.relation.select.join_simple_id.manytomany.dao.UserRoleJoinDao;
import com.mybatisgx.relation.select.join_simple_id.manytomany.entity.Menu;
import com.mybatisgx.relation.select.join_simple_id.manytomany.entity.MenuResourceJoin;
import com.mybatisgx.relation.select.join_simple_id.manytomany.entity.Resource;
import com.mybatisgx.relation.select.join_simple_id.manytomany.entity.Role;
import com.mybatisgx.relation.select.join_simple_id.manytomany.entity.RoleMenuJoin;
import com.mybatisgx.relation.select.join_simple_id.manytomany.entity.User;
import com.mybatisgx.relation.select.join_simple_id.manytomany.entity.UserRoleJoin;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ResourceDaoTest {

    private static int count = 10;
    private static long joinId = 1100000;
    private static UserDao userDao;
    private static RoleDao roleDao;
    private static MenuDao menuDao;
    private static ResourceDao resourceDao;
    private static UserRoleJoinDao userRoleJoinDao;
    private static RoleMenuJoinDao roleMenuJoinDao;
    private static MenuResourceJoinDao menuResourceJoinDao;

    private static List<User> userList = new ArrayList();
    private static List<Role> roleList = new ArrayList();
    private static List<Menu> menuList = new ArrayList();
    private static List<Resource> resourceList = new ArrayList();

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.join_simple_id.manytomany.entity"},
                new String[]{"com.mybatisgx.relation.select.join_simple_id.manytomany.dao"}
        );
        userDao = sqlSession.getMapper(UserDao.class);
        roleDao = sqlSession.getMapper(RoleDao.class);
        menuDao = sqlSession.getMapper(MenuDao.class);
        resourceDao = sqlSession.getMapper(ResourceDao.class);
        userRoleJoinDao = sqlSession.getMapper(UserRoleJoinDao.class);
        roleMenuJoinDao = sqlSession.getMapper(RoleMenuJoinDao.class);
        menuResourceJoinDao = sqlSession.getMapper(MenuResourceJoinDao.class);

        buildData();
        userDao.insertBatch(userList, count);
        roleDao.insertBatch(roleList, count);
        menuDao.insertBatch(menuList, count);
        resourceDao.insertBatch(resourceList, count);

        insertJoinData();
    }

    private static void buildData() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            Role role = fixtureGenerator.createRandomized(Role.class);
            Menu menu = fixtureGenerator.createRandomized(Menu.class);
            Resource resource = fixtureGenerator.createRandomized(Resource.class);

            user.setRoleList(new ArrayList<>());
            role.setUserList(new ArrayList<>());
            role.setMenuList(new ArrayList<>());
            menu.setRoleList(new ArrayList<>());
            menu.setResourceList(new ArrayList<>());
            resource.setMenuList(new ArrayList<>());

            user.getRoleList().add(role);
            role.getUserList().add(user);
            role.getMenuList().add(menu);
            menu.getRoleList().add(role);
            menu.getResourceList().add(resource);
            resource.getMenuList().add(menu);

            if (i == 0) {
                user.setId(888411L);
                role.setId(888412L);
                menu.setId(888413L);
                resource.setId(888414L);
            } else {
                user.setId(null);
                role.setId(null);
                menu.setId(null);
                resource.setId(null);
            }

            userList.add(user);
            roleList.add(role);
            menuList.add(menu);
            resourceList.add(resource);
        }
    }

    private static void insertJoinData() {
        List<UserRoleJoin> userRoleJoinList = new ArrayList();
        for (User user : userList) {
            for (Role role : user.getRoleList()) {
                UserRoleJoin join = new UserRoleJoin();
                join.setId(++joinId);
                join.setUserId(user.getId());
                join.setRoleId(role.getId());
                userRoleJoinList.add(join);
            }
        }
        userRoleJoinDao.insertBatch(userRoleJoinList, userRoleJoinList.size());

        List<RoleMenuJoin> roleMenuJoinList = new ArrayList();
        for (Role role : roleList) {
            for (Menu menu : role.getMenuList()) {
                RoleMenuJoin join = new RoleMenuJoin();
                join.setId(++joinId);
                join.setRoleId(role.getId());
                join.setMenuId(menu.getId());
                roleMenuJoinList.add(join);
            }
        }
        roleMenuJoinDao.insertBatch(roleMenuJoinList, roleMenuJoinList.size());

        List<MenuResourceJoin> menuResourceJoinList = new ArrayList();
        for (Menu menu : menuList) {
            for (Resource resource : menu.getResourceList()) {
                MenuResourceJoin join = new MenuResourceJoin();
                join.setId(++joinId);
                join.setMenuId(menu.getId());
                join.setResourceId(resource.getId());
                menuResourceJoinList.add(join);
            }
        }
        menuResourceJoinDao.insertBatch(menuResourceJoinList, menuResourceJoinList.size());
    }

    @Test
    public void testFindById() {
        Resource dbResource = resourceDao.findById(888414L);
        Assert.assertNotNull(dbResource);

        Resource resource = resourceList.get(0);
        Assert.assertEquals(resource.getId(), dbResource.getId());
        Assert.assertEquals(resource.getCode(), dbResource.getCode());

        List<Menu> dbMenuList = dbResource.getMenuList();
        Assert.assertNotNull(dbMenuList);
        Assert.assertFalse(dbMenuList.isEmpty());
        Menu dbMenu = dbMenuList.get(0);
        Menu menu = menuList.get(0);
        Assert.assertEquals(menu.getId(), dbMenu.getId());
        Assert.assertEquals(menu.getCode(), dbMenu.getCode());

        List<Role> dbRoleList = dbMenu.getRoleList();
        Assert.assertNotNull(dbRoleList);
        Assert.assertFalse(dbRoleList.isEmpty());
        Role dbRole = dbRoleList.get(0);
        Role role = roleList.get(0);
        Assert.assertEquals(role.getId(), dbRole.getId());
        Assert.assertEquals(role.getCode(), dbRole.getCode());

        List<User> dbUserList = dbRole.getUserList();
        Assert.assertNotNull(dbUserList);
        Assert.assertFalse(dbUserList.isEmpty());
        User dbUser = dbUserList.get(0);
        User user = userList.get(0);
        Assert.assertEquals(user.getId(), dbUser.getId());
        Assert.assertEquals(user.getCode(), dbUser.getCode());
    }

    @Test
    public void testFindList() {
        List<Resource> dbResourceList = resourceDao.findList(new Resource());
        Assert.assertNotNull(dbResourceList);
        Assert.assertEquals(count, dbResourceList.size());

        for (int i = 0; i < count; i++) {
            Resource resource = resourceList.get(i);
            Resource dbResource = dbResourceList.get(i);

            Assert.assertEquals(resource.getId(), dbResource.getId());
            Assert.assertEquals(resource.getCode(), dbResource.getCode());

            List<Menu> dbMenuList = dbResource.getMenuList();
            Assert.assertNotNull(dbMenuList);
            Assert.assertFalse(dbMenuList.isEmpty());
            Menu dbMenu = dbMenuList.get(0);
            Menu menu = menuList.get(i);
            Assert.assertEquals(menu.getId(), dbMenu.getId());
            Assert.assertEquals(menu.getCode(), dbMenu.getCode());

            List<Role> dbRoleList = dbMenu.getRoleList();
            Assert.assertNotNull(dbRoleList);
            Assert.assertFalse(dbRoleList.isEmpty());
            Role dbRole = dbRoleList.get(0);
            Role role = roleList.get(i);
            Assert.assertEquals(role.getId(), dbRole.getId());
            Assert.assertEquals(role.getCode(), dbRole.getCode());

            List<User> dbUserList = dbRole.getUserList();
            Assert.assertNotNull(dbUserList);
            Assert.assertFalse(dbUserList.isEmpty());
            User dbUser = dbUserList.get(0);
            User user = userList.get(i);
            Assert.assertEquals(user.getId(), dbUser.getId());
            Assert.assertEquals(user.getCode(), dbUser.getCode());
        }
    }

    @Test
    public void testFindListWithCondition() {
        Resource condition = new Resource();
        condition.setCode(resourceList.get(0).getCode());
        List<Resource> dbResourceList = resourceDao.findList(condition);
        Assert.assertNotNull(dbResourceList);
        Assert.assertFalse(dbResourceList.isEmpty());

        for (Resource dbResource : dbResourceList) {
            Assert.assertEquals(resourceList.get(0).getCode(), dbResource.getCode());

            List<Menu> dbMenuList = dbResource.getMenuList();
            Assert.assertNotNull(dbMenuList);
            Assert.assertFalse(dbMenuList.isEmpty());

            List<Role> dbRoleList = dbMenuList.get(0).getRoleList();
            Assert.assertNotNull(dbRoleList);
            Assert.assertFalse(dbRoleList.isEmpty());

            List<User> dbUserList = dbRoleList.get(0).getUserList();
            Assert.assertNotNull(dbUserList);
            Assert.assertFalse(dbUserList.isEmpty());
        }
    }
}
