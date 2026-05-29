package com.mybatisgx.relation.select.batch_complex_id.onetomany.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.batch_complex_id.onetomany.dao.DeptDao;
import com.mybatisgx.relation.select.batch_complex_id.onetomany.dao.OrgDao;
import com.mybatisgx.relation.select.batch_complex_id.onetomany.dao.TeamDao;
import com.mybatisgx.relation.select.batch_complex_id.onetomany.dao.UserDao;
import com.mybatisgx.relation.select.batch_complex_id.onetomany.entity.Dept;
import com.mybatisgx.relation.select.batch_complex_id.onetomany.entity.Org;
import com.mybatisgx.relation.select.batch_complex_id.onetomany.entity.Team;
import com.mybatisgx.relation.select.batch_complex_id.onetomany.entity.User;
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
                new String[]{"com.mybatisgx.relation.select.batch_complex_id.onetomany.entity"},
                new String[]{"com.mybatisgx.relation.select.batch_complex_id.onetomany.dao"}
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
                MultiId<Long> multiId = new MultiId();
                multiId.setId1(222111L);
                multiId.setId2(222111L);
                org.setMultiId(multiId);
                multiId = new MultiId();
                multiId.setId1(222112L);
                multiId.setId2(222112L);
                dept.setMultiId(multiId);
                multiId = new MultiId();
                multiId.setId1(222113L);
                multiId.setId2(222113L);
                team.setMultiId(multiId);
                multiId = new MultiId();
                multiId.setId1(222114L);
                multiId.setId2(222114L);
                user.setMultiId(multiId);
            } else {
                org.getMultiId().setId1(null);
                org.getMultiId().setId2(null);
                dept.getMultiId().setId1(null);
                dept.getMultiId().setId2(null);
                team.getMultiId().setId1(null);
                team.getMultiId().setId2(null);
                user.getMultiId().setId1(null);
                user.getMultiId().setId2(null);
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
        MultiId<Long> multiId = new MultiId();
        multiId.setId1(222112L);
        multiId.setId2(222112L);
        Dept dbDept = deptDao.findById(multiId);
        Assert.assertNotNull(dbDept);

        Dept dept = deptList.get(0);
        Assert.assertEquals(dept.getMultiId().getId1(), dbDept.getMultiId().getId1());
        Assert.assertEquals(dept.getMultiId().getId2(), dbDept.getMultiId().getId2());

        Org dbOrg = dbDept.getOrg();
        Assert.assertNotNull(dbOrg);
        Org org = orgList.get(0);
        Assert.assertEquals(org.getMultiId().getId1(), dbOrg.getMultiId().getId1());
        Assert.assertEquals(org.getMultiId().getId2(), dbOrg.getMultiId().getId2());

        List<Team> dbTeamList = dbDept.getTeamList();
        Assert.assertNotNull(dbTeamList);
        Assert.assertFalse(dbTeamList.isEmpty());
        Team dbTeam = dbTeamList.get(0);
        Team team = teamList.get(0);
        Assert.assertEquals(team.getMultiId().getId1(), dbTeam.getMultiId().getId1());
        Assert.assertEquals(team.getMultiId().getId2(), dbTeam.getMultiId().getId2());

        List<User> dbUserList = dbTeam.getUserList();
        Assert.assertNotNull(dbUserList);
        Assert.assertFalse(dbUserList.isEmpty());
        User dbUser = dbUserList.get(0);
        User user = userList.get(0);
        Assert.assertEquals(user.getMultiId().getId1(), dbUser.getMultiId().getId1());
        Assert.assertEquals(user.getMultiId().getId2(), dbUser.getMultiId().getId2());
    }

    @Test
    public void testFindList() {
        List<Dept> dbDeptList = deptDao.findList(new Dept());
        Assert.assertNotNull(dbDeptList);
        Assert.assertEquals(count, dbDeptList.size());

        for (int i = 0; i < count; i++) {
            Dept dept = deptList.get(i);
            Dept dbDept = dbDeptList.get(i);

            Assert.assertEquals(dept.getMultiId().getId1(), dbDept.getMultiId().getId1());
            Assert.assertEquals(dept.getMultiId().getId2(), dbDept.getMultiId().getId2());

            Org dbOrg = dbDept.getOrg();
            Assert.assertNotNull(dbOrg);
            Org org = orgList.get(i);
            Assert.assertEquals(org.getMultiId().getId1(), dbOrg.getMultiId().getId1());
            Assert.assertEquals(org.getMultiId().getId2(), dbOrg.getMultiId().getId2());

            List<Team> dbTeamList = dbDept.getTeamList();
            Assert.assertNotNull(dbTeamList);
            Assert.assertFalse(dbTeamList.isEmpty());
            Team dbTeam = dbTeamList.get(0);
            Team team = teamList.get(i);
            Assert.assertEquals(team.getMultiId().getId1(), dbTeam.getMultiId().getId1());
            Assert.assertEquals(team.getMultiId().getId2(), dbTeam.getMultiId().getId2());

            List<User> dbUserList = dbTeam.getUserList();
            Assert.assertNotNull(dbUserList);
            Assert.assertFalse(dbUserList.isEmpty());
            User dbUser = dbUserList.get(0);
            User user = userList.get(i);
            Assert.assertEquals(user.getMultiId().getId1(), dbUser.getMultiId().getId1());
            Assert.assertEquals(user.getMultiId().getId2(), dbUser.getMultiId().getId2());
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