package com.mybatisgx.dao.test;

import com.github.swierkosz.fixture.generator.FixtureGenerator;
import com.mybatisgx.dao.test.entity.User;
import com.mybatisgx.dao.test.util.DaoTestUtils;
import org.junit.Assert;
import org.junit.Test;

public class UserDaoTest {

    @Test
    public void test01() {
        UserDao userDao = DaoTestUtils.getDao(User.class, UserDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        User user = fixtureGenerator.createRandomized(User.class);
        int count = userDao.insert(user);
        Assert.assertEquals(1, count);

        /*DataSource dataSource = super.dataSource();
        super.init(dataSource);

        Environment environment = new Environment.Builder("test001")
                .transactionFactory(new JdbcTransactionFactory())
                .dataSource(dataSource)
                .build();
        MybatisxConfiguration mybatisxConfiguration = new MybatisxConfiguration(environment);

        MybatisxContextLoader mybatisxContextLoader = new MybatisxContextLoader();
        mybatisxContextLoader.processEntityClass(Arrays.asList(User.class));
        mybatisxContextLoader.processDaoClass(Arrays.asList(UserDao.class));
        mybatisxContextLoader.processTemplate();

        CurdTemplateHandler curdTemplateHandler = new CurdTemplateHandler();
        curdTemplateHandler.curdMethod(mybatisxConfiguration);

        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(mybatisxConfiguration);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserDao userDao = sqlSession.getMapper(UserDao.class);

        // 自动生成填充所有字段的 User 对象
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        User user = fixtureGenerator.createRandomized(User.class);
        userDao.insert(user);*/
    }

    @Test
    public void test02() {
        UserDao userDao = DaoTestUtils.getDao(User.class, UserDao.class);
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        User user = fixtureGenerator.createRandomized(User.class);
        int insertCount = userDao.insert(user);
        Assert.assertEquals(1, insertCount);

        int deleteCount = userDao.deleteById(user.getId());
        Assert.assertEquals(1, deleteCount);
    }
}
