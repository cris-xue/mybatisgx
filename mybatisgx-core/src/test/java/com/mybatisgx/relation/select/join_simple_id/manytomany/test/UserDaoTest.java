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
public class UserDaoTest {

    private static int count = 10;
    private static long joinId = 800000;
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
                user.setId(888111L);
                role.setId(888112L);
                menu.setId(888113L);
                resource.setId(888114L);
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
        User dbUser = userDao.findById(888111L);
        Assert.assertNotNull(dbUser);

        User user = userList.get(0);
        Assert.assertEquals(user.getId(), dbUser.getId());
        Assert.assertEquals(user.getCode(), dbUser.getCode());

        List<Role> dbRoleList = dbUser.getRoleList();
        Assert.assertNotNull(dbRoleList);
        Assert.assertFalse(dbRoleList.isEmpty());
        Role dbRole = dbRoleList.get(0);
        Role role = roleList.get(0);
        Assert.assertEquals(role.getId(), dbRole.getId());
        Assert.assertEquals(role.getCode(), dbRole.getCode());

        List<Menu> dbMenuList = dbRole.getMenuList();
        Assert.assertNotNull(dbMenuList);
        Assert.assertFalse(dbMenuList.isEmpty());
        Menu dbMenu = dbMenuList.get(0);
        Menu menu = menuList.get(0);
        Assert.assertEquals(menu.getId(), dbMenu.getId());
        Assert.assertEquals(menu.getCode(), dbMenu.getCode());

        List<Resource> dbResourceList = dbMenu.getResourceList();
        Assert.assertNotNull(dbResourceList);
        Assert.assertFalse(dbResourceList.isEmpty());
        Resource dbResource = dbResourceList.get(0);
        Resource resource = resourceList.get(0);
        Assert.assertEquals(resource.getId(), dbResource.getId());
        Assert.assertEquals(resource.getCode(), dbResource.getCode());
    }

    @Test
    public void testFindList() {
        List<User> dbUserList = userDao.findList(new User());
        Assert.assertNotNull(dbUserList);
        Assert.assertEquals(count, dbUserList.size());

        for (int i = 0; i < count; i++) {
            User user = userList.get(i);
            User dbUser = dbUserList.get(i);

            Assert.assertEquals(user.getId(), dbUser.getId());
            Assert.assertEquals(user.getCode(), dbUser.getCode());

            List<Role> dbRoleList = dbUser.getRoleList();
            Assert.assertNotNull(dbRoleList);
            Assert.assertFalse(dbRoleList.isEmpty());
            Role dbRole = dbRoleList.get(0);
            Role role = roleList.get(i);
            Assert.assertEquals(role.getId(), dbRole.getId());
            Assert.assertEquals(role.getCode(), dbRole.getCode());

            List<Menu> dbMenuList = dbRole.getMenuList();
            Assert.assertNotNull(dbMenuList);
            Assert.assertFalse(dbMenuList.isEmpty());
            Menu dbMenu = dbMenuList.get(0);
            Menu menu = menuList.get(i);
            Assert.assertEquals(menu.getId(), dbMenu.getId());
            Assert.assertEquals(menu.getCode(), dbMenu.getCode());

            List<Resource> dbResourceList = dbMenu.getResourceList();
            Assert.assertNotNull(dbResourceList);
            Assert.assertFalse(dbResourceList.isEmpty());
            Resource dbResource = dbResourceList.get(0);
            Resource resource = resourceList.get(i);
            Assert.assertEquals(resource.getId(), dbResource.getId());
            Assert.assertEquals(resource.getCode(), dbResource.getCode());
        }
    }

    @Test
    public void testFindListWithCondition() {
        User condition = new User();
        condition.setCode(userList.get(0).getCode());
        List<User> dbUserList = userDao.findList(condition);
        Assert.assertNotNull(dbUserList);
        Assert.assertFalse(dbUserList.isEmpty());

        for (User dbUser : dbUserList) {
            Assert.assertEquals(userList.get(0).getCode(), dbUser.getCode());

            List<Role> dbRoleList = dbUser.getRoleList();
            Assert.assertNotNull(dbRoleList);
            Assert.assertFalse(dbRoleList.isEmpty());

            List<Menu> dbMenuList = dbRoleList.get(0).getMenuList();
            Assert.assertNotNull(dbMenuList);
            Assert.assertFalse(dbMenuList.isEmpty());

            List<Resource> dbResourceList = dbMenuList.get(0).getResourceList();
            Assert.assertNotNull(dbResourceList);
            Assert.assertFalse(dbResourceList.isEmpty());
        }
    }
}
