package com.mybatisgx.relation.select.batch_simple_id.onetomany.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.relation.select.batch_simple_id.onetomany.dao.DeptDao;
import com.mybatisgx.relation.select.batch_simple_id.onetomany.dao.OrgDao;
import com.mybatisgx.relation.select.batch_simple_id.onetomany.dao.TeamDao;
import com.mybatisgx.relation.select.batch_simple_id.onetomany.dao.UserDao;
import com.mybatisgx.relation.select.batch_simple_id.onetomany.entity.Dept;
import com.mybatisgx.relation.select.batch_simple_id.onetomany.entity.Org;
import com.mybatisgx.relation.select.batch_simple_id.onetomany.entity.Team;
import com.mybatisgx.relation.select.batch_simple_id.onetomany.entity.User;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeptDaoTest {

    private static int count = 10;
    private static OrgDao orgDao;
    private static DeptDao deptDao;
    private static TeamDao teamDao;
    private static UserDao userDao;

    private static List<Org> orgList = new ArrayList();
    private static List<Dept> deptList = new ArrayList();
    private static List<Team> teamList = new ArrayList();
    private static List<User> userList = new ArrayList();

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.batch_simple_id.onetomany.entity"},
                new String[]{"com.mybatisgx.relation.select.batch_simple_id.onetomany.dao"}
        );
        orgDao = sqlSession.getMapper(OrgDao.class);
        deptDao = sqlSession.getMapper(DeptDao.class);
        teamDao = sqlSession.getMapper(TeamDao.class);
        userDao = sqlSession.getMapper(UserDao.class);

        buildData();
        orgDao.insertBatch(orgList, count);
        deptDao.insertBatch(deptList, count);
        teamDao.insertBatch(teamList, count);
        userDao.insertBatch(userList, count);
    }

    private static void buildData() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            Team team = user.getTeam();
            Dept dept = team.getDept();
            Org org = dept.getOrg();

            if (i == 0) {
                org.setId(555211L);
                dept.setId(555212L);
                team.setId(555213L);
                user.setId(555214L);
            } else {
                org.setId(null);
                dept.setId(null);
                team.setId(null);
                user.setId(null);
            }

            team.setDept(dept);
            dept.setOrg(org);
            org.setDeptList(Collections.singletonList(dept));
            dept.setTeamList(Collections.singletonList(team));
            team.setUserList(Collections.singletonList(user));

            orgList.add(org);
            deptList.add(dept);
            teamList.add(team);
            userList.add(user);
        }
    }

    @Test
    public void testFindById() {
        Dept dbDept = deptDao.findById(555212L);
        Assert.assertNotNull(dbDept);

        Dept dept = deptList.get(0);
        Assert.assertEquals(dept.getId(), dbDept.getId());
        Assert.assertEquals(dept.getCode(), dbDept.getCode());

        Org dbOrg = dbDept.getOrg();
        Assert.assertNotNull(dbOrg);
        Org org = orgList.get(0);
        Assert.assertEquals(org.getId(), dbOrg.getId());
        Assert.assertEquals(org.getCode(), dbOrg.getCode());

        List<Team> dbTeamList = dbDept.getTeamList();
        Assert.assertNotNull(dbTeamList);
        Assert.assertFalse(dbTeamList.isEmpty());
        Team dbTeam = dbTeamList.get(0);
        Team team = teamList.get(0);
        Assert.assertEquals(team.getId(), dbTeam.getId());
        Assert.assertEquals(team.getCode(), dbTeam.getCode());

        List<User> dbUserList = dbTeam.getUserList();
        Assert.assertNotNull(dbUserList);
        Assert.assertFalse(dbUserList.isEmpty());
        User dbUser = dbUserList.get(0);
        User user = userList.get(0);
        Assert.assertEquals(user.getId(), dbUser.getId());
        Assert.assertEquals(user.getCode(), dbUser.getCode());
    }

    @Test
    public void testFindList() {
        List<Dept> dbDeptList = deptDao.findList(new Dept());
        Assert.assertNotNull(dbDeptList);
        Assert.assertEquals(count, dbDeptList.size());

        for (int i = 0; i < count; i++) {
            Dept dept = deptList.get(i);
            Dept dbDept = dbDeptList.get(i);

            Assert.assertEquals(dept.getId(), dbDept.getId());
            Assert.assertEquals(dept.getCode(), dbDept.getCode());

            Org dbOrg = dbDept.getOrg();
            Assert.assertNotNull(dbOrg);
            Org org = orgList.get(i);
            Assert.assertEquals(org.getId(), dbOrg.getId());
            Assert.assertEquals(org.getCode(), dbOrg.getCode());

            List<Team> dbTeamList = dbDept.getTeamList();
            Assert.assertNotNull(dbTeamList);
            Assert.assertFalse(dbTeamList.isEmpty());
            Team dbTeam = dbTeamList.get(0);
            Team team = teamList.get(i);
            Assert.assertEquals(team.getId(), dbTeam.getId());
            Assert.assertEquals(team.getCode(), dbTeam.getCode());

            List<User> dbUserList = dbTeam.getUserList();
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
        Dept condition = new Dept();
        condition.setCode(deptList.get(0).getCode());
        List<Dept> dbDeptList = deptDao.findList(condition);
        Assert.assertNotNull(dbDeptList);
        Assert.assertFalse(dbDeptList.isEmpty());

        for (Dept dbDept : dbDeptList) {
            Assert.assertEquals(deptList.get(0).getCode(), dbDept.getCode());

            Org dbOrg = dbDept.getOrg();
            Assert.assertNotNull(dbOrg);

            List<Team> dbTeamList = dbDept.getTeamList();
            Assert.assertNotNull(dbTeamList);
            Assert.assertFalse(dbTeamList.isEmpty());

            List<User> dbUserList = dbTeamList.get(0).getUserList();
            Assert.assertNotNull(dbUserList);
            Assert.assertFalse(dbUserList.isEmpty());
        }
    }
}
