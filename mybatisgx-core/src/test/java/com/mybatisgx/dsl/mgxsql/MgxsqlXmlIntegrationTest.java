package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.dsl.mgxsql.dao.MgxsqlXmlTestDao;
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
 * MgxsqlLanguageDriver 集成测试：配置 default-scripting-language 后 XML 中写 mgxsql 语法
 * <p>
 * 验证全局配置 default-scripting-language=MgxsqlLanguageDriver 后，
 * XML mapper 中的 mgxsql 语法被自动检测并转换。
 *
 * @author 薛承城
 * @date 2026/7/9
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxsqlXmlIntegrationTest {

    private static SqlSessionFactory sqlSessionFactory;
    private SqlSession sqlSession;
    private MgxsqlXmlTestDao dao;

    @BeforeClass
    public static void beforeClass() throws Exception {
        // 初始化 H2 内存数据库
        UnpooledDataSource dataSource = new UnpooledDataSource(
                "org.h2.Driver",
                "jdbc:h2:mem:mgxsql_xml_test;MODE=MySQL;DB_CLOSE_DELAY=-1",
                "sa", ""
        );
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("test", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.setMapUnderscoreToCamelCase(true);

        // 全局配置 default-scripting-language 为 MgxsqlLanguageDriver
        configuration.setDefaultScriptingLanguage(MgxsqlLanguageDriver.class);

        sqlSessionFactory = new DefaultSqlSessionFactory(configuration);

        // 建表 + 注册 XML mapper
        try (SqlSession session = sqlSessionFactory.openSession()) {
            Connection conn = session.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS mgxsql_test_user ("
                    + "id BIGINT NOT NULL, "
                    + "user_name VARCHAR(256), "
                    + "age INT, "
                    + "PRIMARY KEY (id))");
            stmt.close();

            // 解析 XML mapper
            session.getConfiguration().addMapper(MgxsqlXmlTestDao.class);
            conn.commit();
        }
    }

    @AfterClass
    public static void afterClass() {
    }

    @Before
    public void setUp() {
        sqlSession = sqlSessionFactory.openSession();
        dao = sqlSession.getMapper(MgxsqlXmlTestDao.class);
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
    public void test01_xmlFindByIdNoMgxsql() throws Exception {
        // XML 中不含 mgxsql 语法的标准查询（透传测试）
        insertTestUser(101L, "alice_xml", 30);

        MgxsqlTestUser found = dao.findById(101L);
        Assert.assertNotNull("应能通过 findById 查到记录", found);
        Assert.assertEquals("alice_xml", found.getUserName());
    }

    @Test
    public void test02_xmlFindByNameWithMgxsql() throws Exception {
        insertTestUser(102L, "bob_xml", 25);

        // XML 中 mgxsql 语法：where #user_name = :userName
        List<MgxsqlTestUser> users = dao.findByName("bob_xml");
        Assert.assertNotNull("查询结果不应为 null", users);
        Assert.assertFalse("应能查到记录", users.isEmpty());
        Assert.assertEquals("bob_xml", users.get(0).getUserName());
    }

    @Test
    public void test03_xmlFindByNameWithNullParam() throws Exception {
        insertTestUser(103L, "charlie_xml", 28);

        // mgxsql 语法：参数为 null 时 # 条件不拼接
        List<MgxsqlTestUser> users = dao.findByName(null);
        Assert.assertNotNull("查询结果不应为 null", users);
    }

    @Test
    public void test04_xmlFindByNameAndAgeWithMgxsql() throws Exception {
        insertTestUser(104L, "dave_xml", 22);

        // XML 中 mgxsql 语法：where #[user_name = :userName and age = :age]
        List<MgxsqlTestUser> users = dao.findByNameAndAge("dave_xml", 22);
        Assert.assertNotNull("查询结果不应为 null", users);
        Assert.assertFalse("应能查到记录", users.isEmpty());
    }

    @Test
    public void test05_xmlUpdateNameWithMgxsql() throws Exception {
        insertTestUser(105L, "eve_xml", 27);

        // XML 中 mgxsql 语法：set + where
        int rows = dao.updateName(105L, "eve_updated_xml");
        Assert.assertEquals("应更新1行", 1, rows);

        MgxsqlTestUser found = dao.findById(105L);
        Assert.assertEquals("eve_updated_xml", found.getUserName());
    }
}
