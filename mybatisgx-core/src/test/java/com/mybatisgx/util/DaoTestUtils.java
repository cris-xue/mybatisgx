package com.mybatisgx.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Sets;
import com.mybatisgx.context.MybatisxContextLoader;
import com.mybatisgx.ext.session.MybatisxConfiguration;
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
            properties.load(DaoTestUtils.class.getClassLoader().getResourceAsStream("application.properties"));
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

    protected static MybatisxConfiguration context(List<Class<?>> entityClassList, List<Class<?>> daoClassList) {
        DataSource dataSource = dataSource();
        flywayInit(dataSource);

        Environment environment = new Environment.Builder("test_env")
                .transactionFactory(new JdbcTransactionFactory())
                .dataSource(dataSource)
                .build();
        MybatisxConfiguration mybatisxConfiguration = new MybatisxConfiguration(environment);
        mybatisxConfiguration.setLogImpl(StdOutImpl.class);
        mybatisxConfiguration.setLazyLoadingEnabled(true);
        mybatisxConfiguration.setAggressiveLazyLoading(false);
        mybatisxConfiguration.setLazyLoadTriggerMethods(Sets.newHashSet("get"));

        MybatisxContextLoader mybatisxContextLoader = new MybatisxContextLoader();
        mybatisxContextLoader.processEntityClass(entityClassList);
        mybatisxContextLoader.processDaoClass(daoClassList);
        mybatisxContextLoader.processTemplate();

        StatementTemplateHandler statementTemplateHandler = new StatementTemplateHandler();
        statementTemplateHandler.curdMethod(mybatisxConfiguration);
        return mybatisxConfiguration;
    }

    public static <T> T getDao(Class<?> entityClass, Class<T> daoclass) {
        return getDao(Arrays.asList(entityClass), Arrays.asList(daoclass), daoclass);
    }

    public static <T> T getDao(List<Class<?>> entityClassList, List<Class<?>> daoClassList, Class<T> targetDaoclass) {
        MybatisxConfiguration mybatisxConfiguration = context(entityClassList, daoClassList);
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(mybatisxConfiguration);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession.getMapper(targetDaoclass);
    }

    public static SqlSession getSqlSession(List<Class<?>> entityClassList, List<Class<?>> daoClassList) {
        MybatisxConfiguration mybatisxConfiguration = context(entityClassList, daoClassList);
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(mybatisxConfiguration);
        return sqlSessionFactory.openSession();
    }
}
