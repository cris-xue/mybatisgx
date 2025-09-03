package com.mybatisgx.dao.test;

import com.alibaba.druid.pool.DruidDataSource;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public abstract class DatabaseTestBase {

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
}
