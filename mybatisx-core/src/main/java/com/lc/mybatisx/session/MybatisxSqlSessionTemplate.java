package com.lc.mybatisx.session;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.support.PersistenceExceptionTranslator;

public class MybatisxSqlSessionTemplate extends SqlSessionTemplate {

    public static final String GENERIC_NAME_PREFIX = "param";

    public MybatisxSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    public MybatisxSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
        super(sqlSessionFactory, executorType);
    }

    public MybatisxSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType, PersistenceExceptionTranslator exceptionTranslator) {
        super(sqlSessionFactory, executorType, exceptionTranslator);
    }

    @Override
    public int update(String statement) {
        return super.update(statement);
    }

    @Override
    public int update(String statement, Object parameter) {
        return super.update(statement, parameter);
    }

    @Override
    public int delete(String statement) {
        return super.delete(statement);
    }

    @Override
    public int delete(String statement, Object parameter) {
        return super.delete(statement, parameter);
    }

}
