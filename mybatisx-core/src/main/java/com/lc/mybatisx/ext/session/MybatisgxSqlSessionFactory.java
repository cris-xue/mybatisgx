package com.lc.mybatisx.ext.session;

import org.apache.ibatis.session.*;

import java.sql.Connection;

public class MybatisgxSqlSessionFactory implements SqlSessionFactory {

    private SqlSessionFactory delegate;

    public MybatisgxSqlSessionFactory(SqlSessionFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public SqlSession openSession() {
        SqlSession sqlSession = this.delegate.openSession();
        return new MybatisgxSqlSession(sqlSession);
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        SqlSession sqlSession = this.delegate.openSession(autoCommit);
        return new MybatisgxSqlSession(sqlSession);
    }

    @Override
    public SqlSession openSession(Connection connection) {
        SqlSession sqlSession = this.delegate.openSession(connection);
        return new MybatisgxSqlSession(sqlSession);
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel level) {
        SqlSession sqlSession = this.delegate.openSession(level);
        return new MybatisgxSqlSession(sqlSession);
    }

    @Override
    public SqlSession openSession(ExecutorType execType) {
        SqlSession sqlSession = this.delegate.openSession(execType);
        return new MybatisgxSqlSession(sqlSession);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
        SqlSession sqlSession = this.delegate.openSession(execType, autoCommit);
        return new MybatisgxSqlSession(sqlSession);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
        SqlSession sqlSession = this.delegate.openSession(execType, level);
        return new MybatisgxSqlSession(sqlSession);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, Connection connection) {
        SqlSession sqlSession = this.delegate.openSession(execType, connection);
        return new MybatisgxSqlSession(sqlSession);
    }

    @Override
    public Configuration getConfiguration() {
        return this.delegate.getConfiguration();
    }
}
