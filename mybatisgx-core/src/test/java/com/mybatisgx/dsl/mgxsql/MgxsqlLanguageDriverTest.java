package com.mybatisgx.dsl.mgxsql;

import com.mybatisgx.ext.scripting.xmltags.MgxsqlLanguageDriver;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * MgxsqlLanguageDriver 测试
 *
 * @author 薛承城
 * @date 2026/7/9
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MgxsqlLanguageDriverTest {

    private MgxsqlLanguageDriver driver;
    private Configuration configuration;

    @Before
    public void setUp() {
        this.driver = new MgxsqlLanguageDriver();
        this.configuration = new Configuration();
    }

    // ==================== String 重载：含 mgxsql 语法 ====================

    @Test
    public void test01_stringWithMgxsql() {
        String script = "select * from t_user where #name = :name";
        SqlSource sqlSource = this.driver.createSqlSource(this.configuration, script, null);
        Assert.assertNotNull("SqlSource 不应为 null", sqlSource);
        // mgxsql 转换后应生成 DynamicSqlSource（含 <if> <where> 动态标签）
        String sqlSourceClass = sqlSource.getClass().getSimpleName();
        Assert.assertTrue("应生成 DynamicSqlSource，实际为: " + sqlSourceClass,
                sqlSourceClass.contains("Dynamic"));
    }

    @Test
    public void test02_stringWithMgxsqlWhereSet() {
        String script = "update t_user set name = :name where id = :id";
        SqlSource sqlSource = this.driver.createSqlSource(this.configuration, script, null);
        Assert.assertNotNull("SqlSource 不应为 null", sqlSource);
    }

    @Test
    public void test03_stringWithMgxsqlConditionNode() {
        String script = "select * from t_user where #[name = :name and age = :age]";
        SqlSource sqlSource = this.driver.createSqlSource(this.configuration, script, null);
        Assert.assertNotNull("SqlSource 不应为 null", sqlSource);
    }

    // ==================== String 重载：不含 mgxsql 语法（透传） ====================

    @Test
    public void test04_stringWithoutMgxsql() {
        String script = "select * from t_user where id = #{id}";
        SqlSource sqlSource = this.driver.createSqlSource(this.configuration, script, null);
        Assert.assertNotNull("SqlSource 不应为 null", sqlSource);
    }

    @Test
    public void test05_stringPureStaticSql() {
        String script = "select * from t_user";
        SqlSource sqlSource = this.driver.createSqlSource(this.configuration, script, null);
        Assert.assertNotNull("SqlSource 不应为 null", sqlSource);
    }

    // ==================== XNode 重载：含 mgxsql 语法 ====================

    @Test
    public void test06_xnodeWithMgxsql() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">"
                + "<mapper namespace=\"test\">"
                + "<select id=\"findByName\">select * from t_user where #name = :name</select>"
                + "</mapper>";
        XPathParser parser = new XPathParser(xml);
        XNode node = parser.evalNode("/mapper/select");
        SqlSource sqlSource = this.driver.createSqlSource(this.configuration, node, null);
        Assert.assertNotNull("SqlSource 不应为 null", sqlSource);
    }

    // ==================== XNode 重载：不含 mgxsql 语法（透传） ====================

    @Test
    public void test07_xnodeWithoutMgxsql() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">"
                + "<mapper namespace=\"test\">"
                + "<select id=\"findById\">select * from t_user where id = #{id}</select>"
                + "</mapper>";
        XPathParser parser = new XPathParser(xml);
        XNode node = parser.evalNode("/mapper/select");
        SqlSource sqlSource = this.driver.createSqlSource(this.configuration, node, null);
        Assert.assertNotNull("SqlSource 不应为 null", sqlSource);
    }

    // ==================== 行为一致性验证 ====================

    @Test
    public void test08_mgxsqlEquivalentToMybatisXml() {
        // mgxsql 写法
        String mgxsqlScript = "select * from t_user where #name = :name";
        SqlSource mgxsqlSource = this.driver.createSqlSource(this.configuration, mgxsqlScript, null);

        // 等价的 MyBatis XML 写法
        String mybatisScript = "<where><if test=\"name != null\"> name = #{name}</if></where>";
        SqlSource mybatisSource = this.driver.createSqlSource(this.configuration, mybatisScript, null);

        // 两者都应生成 DynamicSqlSource
        Assert.assertEquals("两种写法应生成相同类型的 SqlSource",
                mgxsqlSource.getClass(), mybatisSource.getClass());
    }
}
