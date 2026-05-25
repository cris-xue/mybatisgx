package com.mybatisgx.relation.select.simple_complex_id.onetoone.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.simple_complex_id.onetoone.dao.UserDao;
import com.mybatisgx.relation.select.simple_complex_id.onetoone.dao.UserDetailDao;
import com.mybatisgx.relation.select.simple_complex_id.onetoone.entity.User;
import com.mybatisgx.relation.select.simple_complex_id.onetoone.entity.UserDetail;
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
    private static UserDao userDao; private static UserDetailDao userDetailDao;
    private static List<User> userList = new ArrayList(); private static List<UserDetail> userDetailList = new ArrayList();

    @BeforeClass public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(new String[]{"com.mybatisgx.relation.select.simple_complex_id.onetoone.entity"}, new String[]{"com.mybatisgx.relation.select.simple_complex_id.onetoone.dao"});
        userDao = sqlSession.getMapper(UserDao.class); userDetailDao = sqlSession.getMapper(UserDetailDao.class);
        buildData(); userDao.insertBatch(userList, count); userDetailDao.insertBatch(userDetailList, count);
    }

    private static void buildData() {
        FixtureGenerator fg = new FixtureGenerator(); fg.configure().ignoreCyclicReferences();
        for (int i = 0; i < count; i++) {
            User u = fg.createRandomized(User.class);
            if (i == 0) { MultiId<Long> mid = new MultiId(); mid.setId1(111111L); mid.setId2(111111L); u.setMultiId(mid); }
            else { u.getMultiId().setId1(null); u.getMultiId().setId2(null); }
            UserDetail ud = u.getUserDetail();
            if (i == 0) { MultiId<Long> mid = new MultiId(); mid.setId1(111111L); mid.setId2(111111L); ud.setMultiId(mid); }
            else { ud.getMultiId().setId1(null); ud.getMultiId().setId2(null); }
            ud.setUser(u); userList.add(u); userDetailList.add(ud);
        }
    }

    @Test public void testUserFindById() {
        MultiId<Long> mid = new MultiId(); mid.setId1(111111L); mid.setId2(111111L);
        User db = userDao.findById(mid); Assert.assertNotNull(db);
        User u = userList.get(0);
        Assert.assertEquals(u.getMultiId().getId1(), db.getMultiId().getId1());
        Assert.assertEquals(u.getMultiId().getId2(), db.getMultiId().getId2());
        Assert.assertNotNull(db.getUserDetail());
        Assert.assertEquals(u.getUserDetail().getMultiId().getId1(), db.getUserDetail().getMultiId().getId1());
        Assert.assertEquals(u.getUserDetail().getMultiId().getId2(), db.getUserDetail().getMultiId().getId2());
    }

    @Test public void testUserFindList() {
        List<User> dbList = userDao.findList(new User()); Assert.assertNotNull(dbList);
        for (int i = 0; i < count; i++) { User u = userList.get(i); User db = dbList.get(i);
            Assert.assertEquals(u.getMultiId().getId1(), db.getMultiId().getId1());
            Assert.assertEquals(u.getMultiId().getId2(), db.getMultiId().getId2());
            Assert.assertNotNull(db.getUserDetail());
            Assert.assertEquals(u.getUserDetail().getMultiId().getId1(), db.getUserDetail().getMultiId().getId1());
            Assert.assertEquals(u.getUserDetail().getMultiId().getId2(), db.getUserDetail().getMultiId().getId2());
        }
    }

    @Test public void testUserDetailFindById() {
        MultiId<Long> mid = new MultiId(); mid.setId1(111111L); mid.setId2(111111L);
        UserDetail db = userDetailDao.findById(mid); Assert.assertNotNull(db);
        UserDetail ud = userDetailList.get(0);
        Assert.assertEquals(ud.getMultiId().getId1(), db.getMultiId().getId1());
        Assert.assertEquals(ud.getMultiId().getId2(), db.getMultiId().getId2());
        Assert.assertNotNull(db.getUser());
        Assert.assertEquals(ud.getUser().getMultiId().getId1(), db.getUser().getMultiId().getId1());
        Assert.assertEquals(ud.getUser().getMultiId().getId2(), db.getUser().getMultiId().getId2());
    }

    @Test public void testUserDetailFindList() {
        List<UserDetail> dbList = userDetailDao.findList(new UserDetail()); Assert.assertNotNull(dbList);
        for (int i = 0; i < count; i++) { UserDetail ud = userDetailList.get(i); UserDetail db = dbList.get(i);
            Assert.assertNotNull(db); Assert.assertNotNull(db.getUser());
            Assert.assertEquals(ud.getMultiId().getId1(), db.getMultiId().getId1());
            Assert.assertEquals(ud.getMultiId().getId2(), db.getMultiId().getId2());
            Assert.assertEquals(ud.getUser().getMultiId().getId1(), db.getUser().getMultiId().getId1());
            Assert.assertEquals(ud.getUser().getMultiId().getId2(), db.getUser().getMultiId().getId2());
        }
    }
}
