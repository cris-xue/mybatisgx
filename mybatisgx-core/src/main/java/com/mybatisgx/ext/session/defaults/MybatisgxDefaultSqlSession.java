package com.mybatisgx.ext.session.defaults;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2025/11/13 19:42
 */
public class MybatisgxDefaultSqlSession implements SqlSession {

    private SqlSession sqlSession;

    public MybatisgxDefaultSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public <T> T selectOne(String statement) {
        return this.sqlSession.selectOne(statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        return this.sqlSession.selectOne(statement, parameter);
    }

    @Override
    public <E> List<E> selectList(String statement) {
        return this.sqlSession.selectList(statement);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        return this.sqlSession.selectList(statement, parameter);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
        return this.sqlSession.selectList(statement, parameter, rowBounds);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        return this.sqlSession.selectMap(statement, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
        return this.sqlSession.selectMap(statement, parameter, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
        return this.sqlSession.selectMap(statement, parameter, mapKey, rowBounds);
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement) {
        return this.sqlSession.selectCursor(statement);
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement, Object parameter) {
        return this.sqlSession.selectCursor(statement, parameter);
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds) {
        return this.sqlSession.selectCursor(statement, parameter, rowBounds);
    }

    @Override
    public void select(String statement, Object parameter, ResultHandler handler) {
        this.sqlSession.select(statement, parameter, handler);
    }

    @Override
    public void select(String statement, ResultHandler handler) {
        this.sqlSession.select(statement, handler);
    }

    @Override
    public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
        this.sqlSession.select(statement, parameter, rowBounds, handler);
    }

    @Override
    public int insert(String statement) {
        return this.sqlSession.insert(statement);
    }

    @Override
    public int insert(String statement, Object parameter) {
        return this.sqlSession.insert(statement, parameter);
    }

    @Override
    public int update(String statement) {
        return this.sqlSession.update(statement);
    }

    @Override
    public int update(String statement, Object parameter) {
        return this.sqlSession.update(statement, parameter);
    }

    @Override
    public int delete(String statement) {
        return this.sqlSession.delete(statement);
    }

    @Override
    public int delete(String statement, Object parameter) {
        return this.sqlSession.delete(statement, parameter);
    }

    @Override
    public void commit() {
        this.sqlSession.commit();
    }

    @Override
    public void commit(boolean force) {
        this.sqlSession.commit(force);
    }

    @Override
    public void rollback() {
        this.sqlSession.rollback();
    }

    @Override
    public void rollback(boolean force) {
        this.sqlSession.rollback(force);
    }

    @Override
    public List<BatchResult> flushStatements() {
        return this.sqlSession.flushStatements();
    }

    @Override
    public void close() {
        this.sqlSession.close();
    }

    @Override
    public void clearCache() {
        this.sqlSession.clearCache();
    }

    @Override
    public Configuration getConfiguration() {
        return this.sqlSession.getConfiguration();
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return this.sqlSession.getMapper(type);
    }

    @Override
    public Connection getConnection() {
        return this.sqlSession.getConnection();
    }
}
