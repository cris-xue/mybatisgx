package com.mybatisgx.custom.condition.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.custom.condition.dao.UserDao;
import com.mybatisgx.custom.condition.entity.User;
import com.mybatisgx.custom.condition.entity.UserQuery;
import com.mybatisgx.handler.page.Page;
import com.mybatisgx.handler.page.Pageable;
import com.mybatisgx.util.DaoTestUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDaoTest {

    private static UserDao userDao;
    private static List<User> userList = new ArrayList<>();

    @BeforeClass
    public static void beforeClass() {
        SqlSession sqlSession = DaoTestUtils.getSqlSession(
                new String[]{"com.mybatisgx.custom.condition.entity"},
                new String[]{"com.mybatisgx.custom.condition.dao"}
        );
        userDao = sqlSession.getMapper(UserDao.class);

        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        int count = 100;
        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            userList.add(user);
        }
        userDao.insertBatch(userList, 3000);
    }

    @Test
    public void testInsert() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);
        int count = userDao.insert(user);
        Assert.assertEquals(1, count);

        User dbUser = userDao.findById(user.getId());
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(user.getId(), dbUser.getId());
    }

    @Test
    public void testInsertSelective() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);
        int count = userDao.insertSelective(user);
        Assert.assertEquals(1, count);

        User dbUser = userDao.findById(user.getId());
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(user.getId(), dbUser.getId());
    }

    @Test
    public void testInsertBatch() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();

        int count = 100;
        List<User> userList = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            User user = fixtureGenerator.createRandomized(User.class);
            userList.add(user);
        }

        long startTime = System.currentTimeMillis();
        int insertBatchCount = userDao.insertBatch(userList, 3000);
        long endTime = System.currentTimeMillis();

        Assert.assertEquals(count, insertBatchCount);
        System.out.println("insertBatchCount: " + insertBatchCount + ", time: " + (endTime - startTime));
    }

    @Test
    public void testDeleteById() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);
        int insertCount = userDao.insert(user);
        Assert.assertEquals(1, insertCount);

        int deleteCount = userDao.deleteById(user.getId());
        Assert.assertEquals(1, deleteCount);

        User dbUser = userDao.findById(user.getId());
        Assert.assertNull(dbUser);
    }

    @Test
    public void testDeleteBatchById() {
        List<Long> deleteList = new ArrayList<>();
        int count = userList.size() / 2;
        for (int index = count - 1; index < userList.size(); index++) {
            deleteList.add(userList.get(index).getId());
        }

        int deleteCount = userDao.deleteBatchById(deleteList, 100);
        Assert.assertEquals(deleteList.size(), deleteCount);

        User dbUser = userDao.findById(deleteList.get(0));
        Assert.assertNull(dbUser);
    }

    @Test
    public void testUpdateById() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);
        int insertCount = userDao.insert(user);
        Assert.assertEquals(1, insertCount);

        user.setName("test");
        int updateCount = userDao.updateById(user);
        Assert.assertEquals(1, updateCount);

        User dbUser = userDao.findById(user.getId());
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(user.getName(), dbUser.getName());
    }

    @Test
    public void testUpdateByIdSelective() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);
        int insertCount = userDao.insert(user);
        Assert.assertEquals(1, insertCount);

        user.setName("test");
        int updateCount = userDao.updateByIdSelective(user);
        Assert.assertEquals(1, updateCount);

        User dbUser = userDao.findById(user.getId());
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(user.getName(), dbUser.getName());
    }

    @Test
    public void testUpdateBatchById() {
        int updateCount = userDao.updateBatchById(userList, 3000);
        Assert.assertEquals(userList.size(), updateCount);
    }

    @Test
    public void testFindById() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);
        int insertCount = userDao.insert(user);
        Assert.assertEquals(1, insertCount);

        User dbUser = userDao.findById(user.getId());
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(user.getName(), dbUser.getName());
    }

    @Test
    public void testFindByOne() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);
        int insertCount = userDao.insert(user);
        Assert.assertEquals(1, insertCount);

        User queryUser = new User();
        queryUser.setId(user.getId());
        User dbUser = userDao.findOne(queryUser);
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(user.getName(), dbUser.getName());
    }

    @Test
    public void testFindPage() {
        Pageable pageable = new Pageable(1, 10);
        Page<User> page2 = userDao.findPage(new User(), pageable);
        Assert.assertNotNull(page2);
    }

    @Test
    public void testFindPageParamNotNull() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);
        Pageable pageable = new Pageable(1, 10);
        userDao.findPage(user, pageable);
    }

    @Test
    public void testFindList() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);
        userDao.findList(user);
    }

    @Test
    public void testCustomQuery01() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);
        UserQuery userQuery = fixtureGenerator.createRandomized(UserQuery.class);

        userDao.insertNew(user, user);
        userDao.findByNameLike("name");
        userDao.findByNameLikeAndId(user.getName(), user.getId());
        userDao.findByInputTimeBetween(Arrays.asList(LocalDateTime.now(), LocalDateTime.now()));
        userDao.findByIdIn(Arrays.asList(111L, 2222L));
        userDao.findListNew(userQuery);
        userDao.findListNew1111(1111L, userQuery);
        userDao.findListNew2222(1111L, userQuery);
        userDao.countByNameByName("name");
        userDao.findTop5ByNameLikeOrderByNameDesc("name");
    }

    @Test
    public void testCustomQuery02() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        fixtureGenerator.configure().ignoreCyclicReferences();
        User user = fixtureGenerator.createRandomized(User.class);

        userDao.insertNew(user, user);
        userDao.findByIdNotInAndNameNotLike(Arrays.asList(111L, 2222L), "name");
    }

    @Test
    public void testCustomQuery03() {
        userDao.findByNameNotNullAndNameLike("name");
        userDao.findByNameNotNull();
        userDao.findByNameIsNotNull();
        userDao.findByNameIsNull();
        userDao.findByNameIsNullAndNameIsNotNullAndNameNotNull();
    }

    @Test
    public void testCustomQuery04() {
        userDao.findCustomSql(111L, "name", Arrays.asList("name1", "name2"));
    }
}
