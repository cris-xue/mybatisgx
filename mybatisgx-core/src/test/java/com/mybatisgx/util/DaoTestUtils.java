package com.mybatisgx.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageInterceptor;
import com.google.common.collect.Sets;
import com.mybatisgx.context.MybatisgxContextLoader;
import com.mybatisgx.ext.builder.xml.MybatisgxXMLConfigBuilder;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.ext.session.defaults.MybatisgxDefaultSqlSessionFactory;
import com.mybatisgx.template.StatementTemplateHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
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

    protected static DataSource dataSource() {
        return dataSource(properties.getProperty("dataSourceUrl"));
    }

    protected static DataSource dataSource(String url) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setDefaultAutoCommit(false);
        return dataSource;
    }

    protected static MybatisgxConfiguration context(String[] entityBasePackages, String[] daoBasePackages) {
        DataSource dataSource = dataSource();
        flywayInit(dataSource);

        Configuration configuration;
        try {
            ClassPathResource classPathResource = new ClassPathResource("mybatis-config.xml");
            MybatisgxXMLConfigBuilder xmlConfigBuilder = new MybatisgxXMLConfigBuilder(classPathResource.getInputStream());
            configuration = xmlConfigBuilder.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Environment environment = new Environment.Builder("test_env")
                .transactionFactory(new JdbcTransactionFactory())
                .dataSource(dataSource)
                .build();
        MybatisgxConfiguration mybatisgxConfiguration = new MybatisgxConfiguration(environment);
        mybatisgxConfiguration.setLogImpl(StdOutImpl.class);
        mybatisgxConfiguration.setLazyLoadingEnabled(true);
        mybatisgxConfiguration.setAggressiveLazyLoading(false);
        mybatisgxConfiguration.setLazyLoadTriggerMethods(Sets.newHashSet("get"));
        mybatisgxConfiguration.addInterceptor(new PageInterceptor());
        mybatisgxConfiguration.setDatabaseId(DB_TYPE);

        MybatisgxContextLoader mybatisgxContextLoader = new MybatisgxContextLoader(entityBasePackages, daoBasePackages, null);
        mybatisgxContextLoader.load();

        StatementTemplateHandler statementTemplateHandler = new StatementTemplateHandler((MybatisgxConfiguration) configuration);
        statementTemplateHandler.curdMethod(mybatisgxConfiguration);
        return mybatisgxConfiguration;
    }

    public static SqlSession getSqlSession(String[] entityBasePackages, String[] daoBasePackages) {
        MybatisgxConfiguration mybatisgxConfiguration = context(entityBasePackages, daoBasePackages);
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(mybatisgxConfiguration);
        MybatisgxDefaultSqlSessionFactory mybatisgxDefaultSqlSessionFactory = new MybatisgxDefaultSqlSessionFactory(sqlSessionFactory);
        return mybatisgxDefaultSqlSessionFactory.openSession();
    }
}
