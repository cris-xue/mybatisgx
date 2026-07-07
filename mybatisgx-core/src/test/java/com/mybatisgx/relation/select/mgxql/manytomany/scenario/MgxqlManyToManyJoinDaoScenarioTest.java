package com.mybatisgx.relation.select.mgxql.manytomany.scenario;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.relation.select.mgxql.manytomany.dao.MgxqlManyToManyJoinDao;
import com.mybatisgx.relation.select.mgxql.manytomany.dto.MenuProjection;
import com.mybatisgx.relation.select.mgxql.manytomany.dto.RoleProjection;
import com.mybatisgx.relation.select.mgxql.manytomany.dto.UserFlatProjection;
import com.mybatisgx.relation.select.mgxql.manytomany.dto.UserProjection;
import com.mybatisgx.relation.select.mgxql.manytomany.dto.UserSkipProjection;
import com.mybatisgx.relation.select.mgxql.manytomany.dto.UserUnknownFieldProjection;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.dao.MenuDao;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.dao.RoleDao;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.dao.RoleMenuJoinDao;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.dao.UserDao;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.dao.UserRoleJoinDao;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.entity.Menu;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.entity.Role;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.entity.RoleMenuJoin;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.entity.User;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.entity.UserRoleJoin;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

/**
 * MGXQL JOIN 多对多场景测试
 *
 * @author ccxuef
 * @date 2026/7/7
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxqlManyToManyJoinDaoScenarioTest {

    private static final int DATA_COUNT = 5;
    private static long joinId = 600000;
    private static MgxqlManyToManyJoinDao mgxqlManyToManyJoinDao;
    private static UserDao userDao;
    private static RoleDao roleDao;
    private static MenuDao menuDao;
    private static UserRoleJoinDao userRoleJoinDao;
    private static RoleMenuJoinDao roleMenuJoinDao;
    private static List<User> userList = new ArrayList<User>();
    private static List<Role> roleList = new ArrayList<Role>();
    private static List<Menu> menuList = new ArrayList<Menu>();

    @BeforeClass
    public static void setUp() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.manytomany.entity"},
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.manytomany.dao", "com.mybatisgx.relation.select.mgxql.manytomany.dao"}
        );
        mgxqlManyToManyJoinDao = sqlSession.getMapper(MgxqlManyToManyJoinDao.class);
        userDao = sqlSession.getMapper(UserDao.class);
        roleDao = sqlSession.getMapper(RoleDao.class);
        menuDao = sqlSession.getMapper(MenuDao.class);
        userRoleJoinDao = sqlSession.getMapper(UserRoleJoinDao.class);
        roleMenuJoinDao = sqlSession.getMapper(RoleMenuJoinDao.class);

        buildData();
        userDao.insertBatch(userList, DATA_COUNT);
        roleDao.insertBatch(roleList, DATA_COUNT);
        menuDao.insertBatch(menuList, DATA_COUNT);
        insertJoinData();
    }

    private static void buildData() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        for (int i = 0; i < DATA_COUNT; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            Role role = fixtureGenerator.createRandomized(Role.class);
            Menu menu = fixtureGenerator.createRandomized(Menu.class);

            user.setId(null);
            role.setId(null);
            menu.setId(null);

            user.setRoleList(new ArrayList<Role>());
            role.setUserList(new ArrayList<User>());
            role.setMenuList(new ArrayList<Menu>());
            menu.setRoleList(new ArrayList<Role>());

            user.getRoleList().add(role);
            role.getUserList().add(user);
            role.getMenuList().add(menu);
            menu.getRoleList().add(role);

            userList.add(user);
            roleList.add(role);
            menuList.add(menu);
        }
    }

    private static void insertJoinData() {
        List<UserRoleJoin> userRoleJoinList = new ArrayList<UserRoleJoin>();
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

        List<RoleMenuJoin> roleMenuJoinList = new ArrayList<RoleMenuJoin>();
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
    }

    @Test
    public void test01_findJoinStarManyToMany() {
        List<User> result = mgxqlManyToManyJoinDao.findJoinStarManyToMany();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        for (User user : result) {
            Assert.assertNotNull(user.getId());
            Assert.assertNotNull(user.getCode());
            List<Role> roleListResult = user.getRoleList();
            Assert.assertNotNull(roleListResult);
            Assert.assertFalse(roleListResult.isEmpty());
            for (Role role : roleListResult) {
                Assert.assertNotNull(role.getId());
                Assert.assertNotNull(role.getCode());
            }
        }
    }

    @Test
    public void test02_findJoinAliasStar() {
        List<User> result = mgxqlManyToManyJoinDao.findJoinAliasStar();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void test03_findFlatProjection() {
        List<UserFlatProjection> result = mgxqlManyToManyJoinDao.findFlatProjection();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        for (UserFlatProjection projection : result) {
            Assert.assertNotNull(projection.getCode());
        }
    }

    @Test
    public void test04_findOneLevelProjection() {
        List<UserProjection> result = mgxqlManyToManyJoinDao.findOneLevelProjection();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        // select u.* 只选择User列，投影DTO中的嵌套roleList不会被填充
        for (UserProjection projection : result) {
            Assert.assertNotNull(projection.getCode());
        }
    }

    @Test
    public void test05_findMultiLevelProjection() {
        List<UserProjection> result = mgxqlManyToManyJoinDao.findMultiLevelProjection();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        // select u.* 只选择User列，投影DTO中的多层嵌套字段不会被填充
        for (UserProjection projection : result) {
            Assert.assertNotNull(projection.getCode());
        }
    }

    @Test
    public void test06_findSkipLevelProjection() {
        List<UserSkipProjection> result = mgxqlManyToManyJoinDao.findSkipLevelProjection();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        // select u.* 只选择User列，跳层投影DTO中的嵌套menuList不会被填充
        for (UserSkipProjection projection : result) {
            Assert.assertNotNull(projection.getCode());
        }
    }

    @Test
    public void test07_findUnknownFieldProjection() {
        List<UserUnknownFieldProjection> result = mgxqlManyToManyJoinDao.findUnknownFieldProjection();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        for (UserUnknownFieldProjection projection : result) {
            Assert.assertNotNull(projection.getCode());
            Assert.assertNull(projection.getUnknownList());
        }
    }
}
