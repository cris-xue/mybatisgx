package com.mybatisgx.simple.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.entity.UserDetail;
import com.mybatisgx.dao.UserDetailDao;
import com.mybatisgx.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UserDetailDaoTest {

    @Test
    public void testInsert() {
        UserDetailDao userDetailDao = DaoTestUtils.getDao(UserDetail.class, UserDetailDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        UserDetail userDetail = fixtureGenerator.createRandomized(UserDetail.class);
        int count = userDetailDao.insert(userDetail);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testDeleteById() {
        UserDetailDao userDetailDao = DaoTestUtils.getDao(UserDetail.class, UserDetailDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        UserDetail userDetail = fixtureGenerator.createRandomized(UserDetail.class);
        int insertCount = userDetailDao.insert(userDetail);
        Assert.assertEquals(1, insertCount);

        int deleteCount = userDetailDao.deleteById(userDetail.getId());
        Assert.assertEquals(1, deleteCount);
    }

    @Test
    public void testUpdateById() {
        UserDetailDao userDetailDao = DaoTestUtils.getDao(UserDetail.class, UserDetailDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        UserDetail userDetail = fixtureGenerator.createRandomized(UserDetail.class);
        int insertCount = userDetailDao.insert(userDetail);
        Assert.assertEquals(1, insertCount);

        userDetail.setCode("test");
        int updateCount = userDetailDao.updateById(userDetail);
        Assert.assertEquals(1, updateCount);

        UserDetail dbUserDetail = userDetailDao.findById(userDetail.getId());
        Assert.assertNotNull(dbUserDetail);
        Assert.assertEquals(userDetail.getCode(), dbUserDetail.getCode());
    }

    @Test
    public void testFindById() {
        UserDetailDao userDetailDao = DaoTestUtils.getDao(UserDetail.class, UserDetailDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        UserDetail userDetail = fixtureGenerator.createRandomized(UserDetail.class);
        int insertCount = userDetailDao.insert(userDetail);
        Assert.assertEquals(1, insertCount);

        UserDetail dbUserDetail = userDetailDao.findById(userDetail.getId());
        Assert.assertNotNull(dbUserDetail);
        Assert.assertEquals(userDetail.getCode(), dbUserDetail.getCode());
    }

    @Test
    public void testInsertBatch() {
        UserDetailDao userDetailDao = DaoTestUtils.getDao(UserDetail.class, UserDetailDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        int count = 5000;
        List<UserDetail> userDetailList = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            UserDetail userDetail = fixtureGenerator.createRandomized(UserDetail.class);
            userDetailList.add(userDetail);
        }

        long startTime = System.currentTimeMillis();
        int insertBatchCount = userDetailDao.insertBatch(userDetailList, 3000);
        long endTime = System.currentTimeMillis();

        Assert.assertEquals(count, insertBatchCount);
        System.out.println("insertBatchCount: " + insertBatchCount + ", time: " + (endTime - startTime));
    }
}
