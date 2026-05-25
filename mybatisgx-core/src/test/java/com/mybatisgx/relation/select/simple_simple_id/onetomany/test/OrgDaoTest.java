package com.mybatisgx.relation.select.simple_simple_id.onetomany.test;

import com.mybatisgx.relation.select.simple_simple_id.onetomany.dao.OrgDao;
import com.mybatisgx.relation.select.simple_simple_id.onetomany.dao.UserDao;
import com.mybatisgx.relation.select.simple_simple_id.onetomany.entity.Org;
import com.mybatisgx.relation.select.simple_simple_id.onetomany.entity.User;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrgDaoTest {

    private static OrgDao orgDao;
    private static UserDao userDao;

    private static List<Org> allOrgList = new ArrayList();
    private static List<User> allUserList = new ArrayList();

    private static int counter = 0;

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetomany.entity"},
                new String[]{"com.mybatisgx.relation.select.simple_simple_id.onetomany.dao"}
        );
        orgDao = sqlSession.getMapper(OrgDao.class);
        userDao = sqlSession.getMapper(UserDao.class);

        buildData();
    }

    private static void buildData() {
        // 创建根节点
        Org rootOrg = new Org(counter++, UUID.randomUUID().toString());
        allOrgList.add(rootOrg);

        List<Org> childrenOrgs = buildChildren(3, 0, rootOrg);
        List<User> users = new ArrayList();
        for (Org org : allOrgList) {
            for (int i = 0; i < 3; i++) {
                User user = new User();
                user.setCode(UUID.randomUUID().toString());
                user.setOrg(org);
                users.add(user);
            }
        }
        allUserList.addAll(users);

        orgDao.insertBatch(allOrgList, 100);
        userDao.insertBatch(allUserList, 100);
    }

    private static List<Org> buildChildren(int maxDepth, int currentDepth, Org parentOrg) {
        if (currentDepth >= maxDepth) {
            return null;
        }
        List<Org> orgList = new ArrayList();
        for (int i = 0; i < 3; i++) {
            Org org = new Org(counter++, UUID.randomUUID().toString());
            org.setParent(parentOrg);
            orgList.add(org);
            allOrgList.add(org);

            List<Org> children = buildChildren(maxDepth, currentDepth + 1, org);
            if (ObjectUtils.isNotEmpty(children)) {
                orgList.addAll(children);
            }
        }
        return orgList;
    }

    @Test
    public void testOrgFindById() {
        Org dbOrg = orgDao.findById(0);
        Assert.assertNotNull(dbOrg);
        Assert.assertEquals(Integer.valueOf(0), dbOrg.getId());

        Org rootOrg = allOrgList.get(0);
        Assert.assertEquals(rootOrg.getCode(), dbOrg.getCode());
        Assert.assertNotNull(dbOrg.getUserList());
        Assert.assertFalse(dbOrg.getUserList().isEmpty());
    }

    @Test
    public void testOrgFindList() {
        Org query = new Org();
        query.setId(0);
        Org parent = new Org();
        parent.setId(0);
        query.setParent(parent);
        List<Org> dbOrgList = orgDao.findList(query);
        Assert.assertNotNull(dbOrgList);
        Assert.assertFalse(dbOrgList.isEmpty());

        for (Org dbOrg : dbOrgList) {
            Assert.assertNotNull(dbOrg.getUserList());
            Assert.assertFalse(dbOrg.getUserList().isEmpty());
            for (User user : dbOrg.getUserList()) {
                Assert.assertNotNull(user.getOrg());
            }
        }
    }

    @Test
    public void testUserFindById() {
        User firstUser = allUserList.get(0);
        User dbUser = userDao.findById(firstUser.getId());
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(firstUser.getCode(), dbUser.getCode());
        Assert.assertNotNull(dbUser.getOrg());
        Assert.assertEquals(firstUser.getOrg().getId(), dbUser.getOrg().getId());
    }

    @Test
    public void testUserFindList() {
        List<User> dbUserList = userDao.findList(new User());
        Assert.assertNotNull(dbUserList);
        Assert.assertEquals(allUserList.size(), dbUserList.size());

        for (int i = 0; i < dbUserList.size(); i++) {
            User dbUser = dbUserList.get(i);
            Assert.assertNotNull(dbUser.getOrg());
        }
    }
}
