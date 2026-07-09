package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxsql.dao.MgxsqlTestDao;
import com.mybatisgx.dsl.mgxsql.entity.MgxsqlTestUser;
import com.mybatisgx.ext.scripting.xmltags.MgxsqlLanguageDriver;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * MgxsqlLanguageDriver 集成测试：使用 @Lang + @Select 的 DAO 方法
 * <p>
 * 直接使用 MyBatis 原生 API（不走 MybatisgxContextLoader），
 * 验证 MgxsqlLanguageDriver 在 MyBatis 框架下正确工作。
 *
 * @author 薛承城
 * @date 2026/7/9
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxsqlLanguageDriverIntegrationTest {

    private static SqlSessionFactory sqlSessionFactory;
    private SqlSession sqlSession;
    private MgxsqlTestDao dao;

    @BeforeClass
    public static void beforeClass() throws Exception {
        // 初始化 H2 内存数据库
        UnpooledDataSource dataSource = new UnpooledDataSource(
                "org.h2.Driver",
                "jdbc:h2:mem:mgxsql_test;MODE=MySQL;DB_CLOSE_DELAY=-1",
                "sa", ""
        );
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("test", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.setMapUnderscoreToCamelCase(true);

        // 注册 Mapper（MyBatis 原生方式，@Select/@Update 由 MapperAnnotationBuilder 解析）
        configuration.addMapper(MgxsqlTestDao.class);

        sqlSessionFactory = new DefaultSqlSessionFactory(configuration);

        // 建表
        try (SqlSession session = sqlSessionFactory.openSession()) {
            Connection conn = session.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS mgxsql_test_user ("
                    + "id BIGINT NOT NULL, "
                    + "user_name VARCHAR(256), "
                    + "age INT, "
                    + "PRIMARY KEY (id))");
            stmt.close();
            conn.commit();
        }
    }

    @AfterClass
    public static void afterClass() {
    }

    @Before
    public void setUp() {
        sqlSession = sqlSessionFactory.openSession();
        dao = sqlSession.getMapper(MgxsqlTestDao.class);
    }

    @After
    public void tearDown() {
        if (sqlSession != null) {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    private void insertTestUser(Long id, String userName, Integer age) throws Exception {
        Connection conn = sqlSession.getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO mgxsql_test_user (id, user_name, age) VALUES (?, ?, ?)");
        ps.setLong(1, id);
        ps.setString(2, userName);
        ps.setInt(3, age);
        ps.executeUpdate();
        ps.close();
    }

    @Test
    public void test01_findByIdTransparency() throws Exception {
        // 不含 mgxsql 语法的标准 MyBatis 查询（透传测试）
        insertTestUser(1L, "alice", 30);

        MgxsqlTestUser found = dao.findById(1L);
        Assert.assertNotNull("应能通过 findById 查到记录", found);
        Assert.assertEquals("alice", found.getUserName());
    }

    @Test
    public void test02_findByNameWithMgxsql() throws Exception {
        insertTestUser(2L, "bob", 25);

        // mgxsql 语法：where #user_name = :userName
        List<MgxsqlTestUser> users = dao.findByName("bob");
        Assert.assertNotNull("查询结果不应为 null", users);
        Assert.assertFalse("应能查到记录", users.isEmpty());
        Assert.assertEquals("bob", users.get(0).getUserName());
    }

    @Test
    public void test03_findByNameWithNullParam() throws Exception {
        insertTestUser(3L, "charlie", 28);

        // mgxsql 语法：参数为 null 时 # 条件不拼接
        List<MgxsqlTestUser> users = dao.findByName(null);
        Assert.assertNotNull("查询结果不应为 null", users);
        // name 为 null 时 <if> 不拼接条件，应查到所有记录
    }

    @Test
    public void test04_findByNameAndAgeWithMgxsql() throws Exception {
        insertTestUser(4L, "dave", 22);

        // mgxsql 语法：where #[user_name = :userName and age = :age]
        List<MgxsqlTestUser> users = dao.findByNameAndAge("dave", 22);
        Assert.assertNotNull("查询结果不应为 null", users);
        Assert.assertFalse("应能查到记录", users.isEmpty());
    }

    @Test
    public void test05_updateNameWithMgxsql() throws Exception {
        insertTestUser(5L, "eve", 27);

        // mgxsql 语法：set + where
        int rows = dao.updateName(5L, "eve_updated");
        Assert.assertEquals("应更新1行", 1, rows);

        MgxsqlTestUser found = dao.findById(5L);
        Assert.assertEquals("eve_updated", found.getUserName());
    }
}
