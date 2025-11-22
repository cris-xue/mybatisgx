package com.mybatisgx.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageInterceptor;
import com.google.common.collect.Sets;
import com.mybatisgx.context.MybatisxContextLoader;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.ext.session.defaults.MybatisgxDefaultSqlSessionFactory;
import com.mybatisgx.template.StatementTemplateHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DaoTestUtils {

    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(DaoTestUtils.class.getClassLoader().getResourceAsStream("application_mysql.properties"));
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

    protected static MybatisgxConfiguration context(List<Class<?>> entityClassList, List<Class<?>> daoClassList) {
        DataSource dataSource = dataSource();
        flywayInit(dataSource);

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
        mybatisgxConfiguration.setDatabaseId("pgsql");

        MybatisxContextLoader mybatisxContextLoader = new MybatisxContextLoader(mybatisgxConfiguration);
        mybatisxContextLoader.processEntityClass(entityClassList);
        mybatisxContextLoader.processDaoClass(daoClassList);
        mybatisxContextLoader.processTemplate();

        StatementTemplateHandler statementTemplateHandler = new StatementTemplateHandler(mybatisgxConfiguration);
        statementTemplateHandler.curdMethod(mybatisgxConfiguration);
        return mybatisgxConfiguration;
    }

    public static <T> T getDao(Class<?> entityClass, Class<T> daoclass) {
        return getDao(Arrays.asList(entityClass), Arrays.asList(daoclass), daoclass);
    }

    public static <T> T getDao(List<Class<?>> entityClassList, List<Class<?>> daoClassList, Class<T> targetDaoclass) {
        SqlSession sqlSession = getSqlSession(entityClassList, daoClassList);
        return sqlSession.getMapper(targetDaoclass);
    }

    public static SqlSession getSqlSession(List<Class<?>> entityClassList, List<Class<?>> daoClassList) {
        MybatisgxConfiguration mybatisgxConfiguration = context(entityClassList, daoClassList);
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(mybatisgxConfiguration);
        MybatisgxDefaultSqlSessionFactory mybatisgxDefaultSqlSessionFactory = new MybatisgxDefaultSqlSessionFactory(sqlSessionFactory);
        return mybatisgxDefaultSqlSessionFactory.openSession();
    }
}
