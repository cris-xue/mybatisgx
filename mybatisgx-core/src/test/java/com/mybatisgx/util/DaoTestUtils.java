package com.mybatisgx.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.mybatisgx.context.MybatisgxContextLoader;
import com.mybatisgx.context.MybatisgxObjectFactory;
import com.mybatisgx.ext.builder.xml.MybatisgxXMLConfigBuilder;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.ext.session.defaults.MybatisgxDefaultSqlSessionFactory;
import com.mybatisgx.template.StatementTemplateHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.flywaydb.core.Flyway;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

public class DaoTestUtils {

    private static final Properties properties = new Properties();

    private static final String DB_TYPE = "oracle";

    static {
        try {
            properties.load(DaoTestUtils.class.getClassLoader().getResourceAsStream(String.format("application_%s.properties", DB_TYPE)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void flywayInit(DataSource dataSource) {
        // 初始化Flyway
        String flywayLocations = properties.getProperty("flywayLocations");
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(StringUtils.split(flywayLocations, ","))
                .load();
        flyway.migrate();
    }

    protected static MybatisgxConfiguration context(String[] entityBasePackages, String[] daoBasePackages) {
        Configuration configuration;
        try {
            ClassPathResource classPathResource = new ClassPathResource("mybatis-config.xml");
            MybatisgxXMLConfigBuilder xmlConfigBuilder = new MybatisgxXMLConfigBuilder(classPathResource.getInputStream());
            configuration = xmlConfigBuilder.parse();
            MybatisgxObjectFactory.register((MybatisgxConfiguration) configuration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DataSource dataSource = configuration.getEnvironment().getDataSource();
        flywayInit(dataSource);

        MybatisgxContextLoader mybatisgxContextLoader = new MybatisgxContextLoader(entityBasePackages, daoBasePackages, null);
        mybatisgxContextLoader.load();

        StatementTemplateHandler statementTemplateHandler = new StatementTemplateHandler((MybatisgxConfiguration) configuration);
        statementTemplateHandler.curdMethod(configuration);
        return (MybatisgxConfiguration) configuration;
    }

    public static SqlSession getSqlSession(String[] entityBasePackages, String[] daoBasePackages) {
        MybatisgxConfiguration mybatisgxConfiguration = context(entityBasePackages, daoBasePackages);
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(mybatisgxConfiguration);
        MybatisgxDefaultSqlSessionFactory mybatisgxDefaultSqlSessionFactory = new MybatisgxDefaultSqlSessionFactory(sqlSessionFactory);
        return mybatisgxDefaultSqlSessionFactory.openSession();
    }
}
