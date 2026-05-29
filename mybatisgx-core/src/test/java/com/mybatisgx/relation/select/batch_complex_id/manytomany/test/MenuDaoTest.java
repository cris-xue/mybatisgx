package com.mybatisgx.relation.select.batch_complex_id.manytomany.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.batch_complex_id.manytomany.dao.MenuDao;
import com.mybatisgx.relation.select.batch_complex_id.manytomany.dao.MenuResourceJoinDao;
import com.mybatisgx.relation.select.batch_complex_id.manytomany.dao.ResourceDao;
import com.mybatisgx.relation.select.batch_complex_id.manytomany.dao.RoleDao;
import com.mybatisgx.relation.select.batch_complex_id.manytomany.dao.RoleMenuJoinDao;
import com.mybatisgx.relation.select.batch_complex_id.manytomany.dao.UserDao;
import com.mybatisgx.relation.select.batch_complex_id.manytomany.dao.UserRoleJoinDao;
import com.mybatisgx.relation.select.batch_complex_id.manytomany.entity.Menu;
import com.mybatisgx.relation.select.batch_complex_id.manytomany.entity.MenuResourceJoin;
import com.mybatisgx.relation.select.batch_complex_id.manytomany.entity.Resource;
import com.mybatisgx.relation.select.batch_complex_id.manytomany.entity.Role;
import com.mybatisgx.relation.select.batch_complex_id.manytomany.entity.RoleMenuJoin;
import com.mybatisgx.relation.select.batch_complex_id.manytomany.entity.User;
import com.mybatisgx.relation.select.batch_complex_id.manytomany.entity.UserRoleJoin;
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
public class MenuDaoTest {

    private static int count = 10;
    private static long joinId = 300000;
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
                new String[]{"com.mybatisgx.relation.select.batch_complex_id.manytomany.entity"},
                new String[]{"com.mybatisgx.relation.select.batch_complex_id.manytomany.dao"}
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
                MultiId<Long> multiId = new MultiId();
                multiId.setId1(333111L);
                multiId.setId2(333111L);
                user.setMultiId(multiId);
                multiId = new MultiId();
                multiId.setId1(333112L);
                multiId.setId2(333112L);
                role.setMultiId(multiId);
                multiId = new MultiId();
                multiId.setId1(333113L);
                multiId.setId2(333113L);
                menu.setMultiId(multiId);
                multiId = new MultiId();
                multiId.setId1(333114L);
                multiId.setId2(333114L);
                resource.setMultiId(multiId);
            } else {
                user.getMultiId().setId1(null);
                user.getMultiId().setId2(null);
                role.getMultiId().setId1(null);
                role.getMultiId().setId2(null);
                menu.getMultiId().setId1(null);
                menu.getMultiId().setId2(null);
                resource.getMultiId().setId1(null);
                resource.getMultiId().setId2(null);
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
                join.setUserId1(user.getMultiId().getId1());
                join.setUserId2(user.getMultiId().getId2());
                join.setRoleId1(role.getMultiId().getId1());
                join.setRoleId2(role.getMultiId().getId2());
                userRoleJoinList.add(join);
            }
        }
        userRoleJoinDao.insertBatch(userRoleJoinList, userRoleJoinList.size());

        List<RoleMenuJoin> roleMenuJoinList = new ArrayList();
        for (Role role : roleList) {
            for (Menu menu : role.getMenuList()) {
                RoleMenuJoin join = new RoleMenuJoin();
                join.setId(++joinId);
                join.setRoleId1(role.getMultiId().getId1());
                join.setRoleId2(role.getMultiId().getId2());
                join.setMenuId1(menu.getMultiId().getId1());
                join.setMenuId2(menu.getMultiId().getId2());
                roleMenuJoinList.add(join);
            }
        }
        roleMenuJoinDao.insertBatch(roleMenuJoinList, roleMenuJoinList.size());

        List<MenuResourceJoin> menuResourceJoinList = new ArrayList();
        for (Menu menu : menuList) {
            for (Resource resource : menu.getResourceList()) {
                MenuResourceJoin join = new MenuResourceJoin();
                join.setId(++joinId);
                join.setMenuId1(menu.getMultiId().getId1());
                join.setMenuId2(menu.getMultiId().getId2());
                join.setResourceId1(resource.getMultiId().getId1());
                join.setResourceId2(resource.getMultiId().getId2());
                menuResourceJoinList.add(join);
            }
        }
        menuResourceJoinDao.insertBatch(menuResourceJoinList, menuResourceJoinList.size());
    }

    @Test
    public void testFindById() {
        MultiId<Long> multiId = new MultiId();
        multiId.setId1(333113L);
        multiId.setId2(333113L);
        Menu dbMenu = menuDao.findById(multiId);
        Assert.assertNotNull(dbMenu);

        Menu menu = menuList.get(0);
        Assert.assertEquals(menu.getMultiId().getId1(), dbMenu.getMultiId().getId1());
        Assert.assertEquals(menu.getMultiId().getId2(), dbMenu.getMultiId().getId2());

        List<Role> dbRoleList = dbMenu.getRoleList();
        Assert.assertNotNull(dbRoleList);
        Assert.assertFalse(dbRoleList.isEmpty());
        Role dbRole = dbRoleList.get(0);
        Role role = roleList.get(0);
        Assert.assertEquals(role.getMultiId().getId1(), dbRole.getMultiId().getId1());
        Assert.assertEquals(role.getMultiId().getId2(), dbRole.getMultiId().getId2());

        List<User> dbUserList = dbRole.getUserList();
        Assert.assertNotNull(dbUserList);
        Assert.assertFalse(dbUserList.isEmpty());
        User dbUser = dbUserList.get(0);
        User user = userList.get(0);
        Assert.assertEquals(user.getMultiId().getId1(), dbUser.getMultiId().getId1());
        Assert.assertEquals(user.getMultiId().getId2(), dbUser.getMultiId().getId2());

        List<Resource> dbResourceList = dbMenu.getResourceList();
        Assert.assertNotNull(dbResourceList);
        Assert.assertFalse(dbResourceList.isEmpty());
        Resource dbResource = dbResourceList.get(0);
        Resource resource = resourceList.get(0);
        Assert.assertEquals(resource.getMultiId().getId1(), dbResource.getMultiId().getId1());
        Assert.assertEquals(resource.getMultiId().getId2(), dbResource.getMultiId().getId2());
    }

    @Test
    public void testFindList() {
        List<Menu> dbMenuList = menuDao.findList(new Menu());
        Assert.assertNotNull(dbMenuList);
        Assert.assertEquals(count, dbMenuList.size());

        for (int i = 0; i < count; i++) {
            Menu menu = menuList.get(i);
            Menu dbMenu = dbMenuList.get(i);

            Assert.assertEquals(menu.getMultiId().getId1(), dbMenu.getMultiId().getId1());
            Assert.assertEquals(menu.getMultiId().getId2(), dbMenu.getMultiId().getId2());

            List<Role> dbRoleList = dbMenu.getRoleList();
            Assert.assertNotNull(dbRoleList);
            Assert.assertFalse(dbRoleList.isEmpty());
            Role dbRole = dbRoleList.get(0);
            Role role = roleList.get(i);
            Assert.assertEquals(role.getMultiId().getId1(), dbRole.getMultiId().getId1());
            Assert.assertEquals(role.getMultiId().getId2(), dbRole.getMultiId().getId2());

            List<User> dbUserList = dbRole.getUserList();
            Assert.assertNotNull(dbUserList);
            Assert.assertFalse(dbUserList.isEmpty());
            User dbUser = dbUserList.get(0);
            User user = userList.get(i);
            Assert.assertEquals(user.getMultiId().getId1(), dbUser.getMultiId().getId1());
            Assert.assertEquals(user.getMultiId().getId2(), dbUser.getMultiId().getId2());

            List<Resource> dbResourceList = dbMenu.getResourceList();
            Assert.assertNotNull(dbResourceList);
            Assert.assertFalse(dbResourceList.isEmpty());
            Resource dbResource = dbResourceList.get(0);
            Resource resource = resourceList.get(i);
            Assert.assertEquals(resource.getMultiId().getId1(), dbResource.getMultiId().getId1());
            Assert.assertEquals(resource.getMultiId().getId2(), dbResource.getMultiId().getId2());
        }
    }

    @Test
    public void testFindListWithCondition() {
        Menu condition = new Menu();
        condition.setCode(menuList.get(0).getCode());
        List<Menu> dbMenuList = menuDao.findList(condition);
        Assert.assertNotNull(dbMenuList);
        Assert.assertFalse(dbMenuList.isEmpty());

        for (Menu dbMenu : dbMenuList) {
            Assert.assertEquals(menuList.get(0).getCode(), dbMenu.getCode());

            List<Role> dbRoleList = dbMenu.getRoleList();
            Assert.assertNotNull(dbRoleList);
            Assert.assertFalse(dbRoleList.isEmpty());

            List<User> dbUserList = dbRoleList.get(0).getUserList();
            Assert.assertNotNull(dbUserList);
            Assert.assertFalse(dbUserList.isEmpty());

            List<Resource> dbResourceList = dbMenu.getResourceList();
            Assert.assertNotNull(dbResourceList);
            Assert.assertFalse(dbResourceList.isEmpty());
        }
    }
}