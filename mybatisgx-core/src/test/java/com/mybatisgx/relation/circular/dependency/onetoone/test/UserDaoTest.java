package com.mybatisgx.relation.circular.dependency.onetoone.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.relation.circular.dependency.onetoone.dao.UserDao;
import com.mybatisgx.relation.circular.dependency.onetoone.dao.UserDetailDao;
import com.mybatisgx.relation.circular.dependency.onetoone.dao.UserDetailItem1Dao;
import com.mybatisgx.relation.circular.dependency.onetoone.dao.UserDetailItem2Dao;
import com.mybatisgx.relation.circular.dependency.onetoone.entity.User;
import com.mybatisgx.relation.circular.dependency.onetoone.entity.UserDetail;
import com.mybatisgx.relation.circular.dependency.onetoone.entity.UserDetailItem1;
import com.mybatisgx.relation.circular.dependency.onetoone.entity.UserDetailItem2;
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
    private static UserDao userDao;
    private static UserDetailDao userDetailDao;
    private static UserDetailItem1Dao userDetailItem1Dao;
    private static UserDetailItem2Dao userDetailItem2Dao;

    private static List<User> userList = new ArrayList();
    private static List<UserDetail> userDetailList = new ArrayList();
    private static List<UserDetailItem1> userDetailItem1List = new ArrayList();
    private static List<UserDetailItem2> userDetailItem2List = new ArrayList();
    private static Long firstUserId;

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.relation.circular.dependency.onetoone.entity"},
                new String[]{"com.mybatisgx.relation.circular.dependency.onetoone.dao"}
        );
        userDao = sqlSession.getMapper(UserDao.class);
        userDetailDao = sqlSession.getMapper(UserDetailDao.class);
        userDetailItem1Dao = sqlSession.getMapper(UserDetailItem1Dao.class);
        userDetailItem2Dao = sqlSession.getMapper(UserDetailItem2Dao.class);

        buildData();
        userDao.insertBatch(userList, count);
        userDetailDao.insertBatch(userDetailList, count);
        userDetailItem1Dao.insertBatch(userDetailItem1List, count);
        userDetailItem2Dao.insertBatch(userDetailItem2List, count);

        firstUserId = userList.get(0).getId();
    }

    private static void buildData() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            user.setId(null);

            UserDetail userDetail = user.getUserDetail();
            userDetail.setId(null);
            userDetail.setUser(user);

            UserDetailItem1 userDetailItem1 = userDetail.getUserDetailItem1();
            userDetailItem1.setId(null);
            userDetailItem1.setUserDetail(userDetail);

            UserDetailItem2 userDetailItem2 = userDetailItem1.getUserDetailItem2();
            userDetailItem2.setId(null);
            userDetailItem2.setUserDetailItem1(userDetailItem1);

            userList.add(user);
            userDetailList.add(userDetail);
            userDetailItem1List.add(userDetailItem1);
            userDetailItem2List.add(userDetailItem2);
        }
    }

    @Test
    public void testFindById() {
        // 验证循环引用
        User dbUser = userDao.findById(firstUserId);
        Assert.assertNotNull(dbUser);
        Assert.assertNotNull(dbUser.getUserDetail());
        Assert.assertNull(dbUser.getUserDetail().getUser().getUserDetail());
    }
}
