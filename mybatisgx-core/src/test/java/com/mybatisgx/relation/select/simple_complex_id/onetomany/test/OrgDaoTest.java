package com.mybatisgx.relation.select.simple_complex_id.onetomany.test;

import com.mybatisgx.relation.select.simple_complex_id.onetomany.dao.OrgDao;
import com.mybatisgx.relation.select.simple_complex_id.onetomany.dao.UserDao;
import com.mybatisgx.relation.select.simple_complex_id.onetomany.entity.Org;
import com.mybatisgx.relation.select.simple_complex_id.onetomany.entity.User;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert; import org.junit.BeforeClass; import org.junit.Test;
import java.util.ArrayList; import java.util.List; import java.util.UUID;

public class OrgDaoTest {
    private static OrgDao orgDao; private static UserDao userDao;
    private static List<Org> allOrgList = new ArrayList(); private static List<User> allUserList = new ArrayList();
    private static int counter = 0;

    @BeforeClass public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(new String[]{"com.mybatisgx.relation.select.simple_complex_id.onetomany.entity"}, new String[]{"com.mybatisgx.relation.select.simple_complex_id.onetomany.dao"});
        orgDao = sqlSession.getMapper(OrgDao.class); userDao = sqlSession.getMapper(UserDao.class); buildData();
    }

    private static void buildData() {
        Org root = new Org(counter++, UUID.randomUUID().toString()); allOrgList.add(root); buildChildren(3, 0, root);
        List<User> users = new ArrayList();
        for (Org o : allOrgList) for (int i = 0; i < 3; i++) { User u = new User(); u.setCode(UUID.randomUUID().toString()); u.setOrg(o); users.add(u); }
        allUserList.addAll(users); orgDao.insertBatch(allOrgList, 100); userDao.insertBatch(allUserList, 100);
    }

    private static List<Org> buildChildren(int max, int depth, Org parent) {
        if (depth >= max) return null;
        List<Org> list = new ArrayList();
        for (int i = 0; i < 3; i++) { Org o = new Org(counter++, UUID.randomUUID().toString()); o.setParent(parent); list.add(o); allOrgList.add(o); List<Org> c = buildChildren(max, depth + 1, o); if (ObjectUtils.isNotEmpty(c)) list.addAll(c); }
        return list;
    }

    @Test public void testOrgFindById() { Org db = orgDao.findById(0); Assert.assertNotNull(db); Assert.assertEquals(Integer.valueOf(0), db.getId()); Assert.assertEquals(allOrgList.get(0).getCode(), db.getCode()); Assert.assertNotNull(db.getUserList()); Assert.assertFalse(db.getUserList().isEmpty()); }
    @Test public void testOrgFindList() { Org q = new Org(); q.setId(0); Org p = new Org(); p.setId(0); q.setParent(p); List<Org> list = orgDao.findList(q); Assert.assertNotNull(list); Assert.assertFalse(list.isEmpty()); for (Org o : list) { Assert.assertNotNull(o.getUserList()); Assert.assertFalse(o.getUserList().isEmpty()); for (User u : o.getUserList()) Assert.assertNotNull(u.getOrg()); } }
    @Test public void testUserFindById() { User first = allUserList.get(0); User db = userDao.findById(first.getId()); Assert.assertNotNull(db); Assert.assertEquals(first.getCode(), db.getCode()); Assert.assertNotNull(db.getOrg()); Assert.assertEquals(first.getOrg().getId(), db.getOrg().getId()); }
    @Test public void testUserFindList() { List<User> list = userDao.findList(new User()); Assert.assertNotNull(list); Assert.assertEquals(allUserList.size(), list.size()); for (User u : list) Assert.assertNotNull(u.getOrg()); }
}
