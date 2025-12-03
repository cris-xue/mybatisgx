package com.mybatisgx.util;

import com.mybatisgx.api.GeneratedValueHandler;
import com.mybatisgx.context.MybatisgxContextLoader;
import com.mybatisgx.ext.builder.xml.MybatisgxXMLConfigBuilder;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.ext.session.defaults.MybatisgxDefaultSqlSessionFactory;
import com.mybatisgx.template.StatementTemplateHandler;
import org.apache.commons.lang3.StringUtils;
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

    static {
        try {
            properties.load(DaoTestUtils.class.getClassLoader().getResourceAsStream(String.format("application_flyway.properties")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void flywayInit(DataSource dataSource, String dbType) {
        // 初始化Flyway
        String flywayLocations = properties.getProperty(dbType);
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(StringUtils.split(flywayLocations, ","))
                .load();
        flyway.migrate();
    }

    protected static MybatisgxConfiguration context(String[] entityBasePackages, String[] daoBasePackages) {
        MybatisgxConfiguration configuration;
        try {
            ClassPathResource classPathResource = new ClassPathResource("mybatis-config.xml");
            MybatisgxXMLConfigBuilder xmlConfigBuilder = new MybatisgxXMLConfigBuilder(classPathResource.getInputStream());
            configuration = (MybatisgxConfiguration) xmlConfigBuilder.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DataSource dataSource = configuration.getEnvironment().getDataSource();
        flywayInit(dataSource, configuration.getEnvironment().getId());

        MybatisgxContextLoader mybatisgxContextLoader = new MybatisgxContextLoader(entityBasePackages, daoBasePackages, null, configuration);
        mybatisgxContextLoader.load();

        StatementTemplateHandler statementTemplateHandler = new StatementTemplateHandler(configuration);
        statementTemplateHandler.curdMethod(configuration);
        return configuration;
    }

    public static SqlSession getSqlSession(String[] entityBasePackages, String[] daoBasePackages) {
        MybatisgxConfiguration mybatisgxConfiguration = context(entityBasePackages, daoBasePackages);
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(mybatisgxConfiguration);
        MybatisgxDefaultSqlSessionFactory mybatisgxDefaultSqlSessionFactory = new MybatisgxDefaultSqlSessionFactory(sqlSessionFactory);
        return mybatisgxDefaultSqlSessionFactory.openSession();
    }
}
