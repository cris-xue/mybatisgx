package com.lc.mybatisx.sql;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

public class BoundSqlSqlSource implements SqlSource {

    private final BoundSql boundSql;

    public BoundSqlSqlSource(BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return boundSql;
    }
}
