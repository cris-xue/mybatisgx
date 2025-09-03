package com.mybatisgx.dao.test.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.lc.mybatisx.context.MybatisxContextLoader;
import com.lc.mybatisx.ext.MybatisxConfiguration;
import com.lc.mybatisx.template.CurdTemplateHandler;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.util.Arrays;

public class DaoTestUtils {

    protected static void init(DataSource dataSource) {
        // 初始化Flyway
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/mysql")
                .load();
        flyway.migrate();
    }

    protected static DataSource dataSource() {
        return dataSource("jdbc:h2:mem:test_db;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE");
    }

    protected static DataSource dataSource(String url) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setDriverClassName("org.h2.Driver");
        return dataSource;
    }

    public static <T> T getDao(Class<?> entityClass, Class<T> daoclass) {
        DataSource dataSource = dataSource();
        init(dataSource);

        Environment environment = new Environment.Builder("test001")
                .transactionFactory(new JdbcTransactionFactory())
                .dataSource(dataSource)
                .build();
        MybatisxConfiguration mybatisxConfiguration = new MybatisxConfiguration(environment);
        // mybatisxConfiguration.setLogImpl(StdOutImpl.class);

        MybatisxContextLoader mybatisxContextLoader = new MybatisxContextLoader();
        mybatisxContextLoader.processEntityClass(Arrays.asList(entityClass));
        mybatisxContextLoader.processDaoClass(Arrays.asList(daoclass));
        mybatisxContextLoader.processTemplate();

        CurdTemplateHandler curdTemplateHandler = new CurdTemplateHandler();
        curdTemplateHandler.curdMethod(mybatisxConfiguration);

        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(mybatisxConfiguration);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession.getMapper(daoclass);
    }
}
