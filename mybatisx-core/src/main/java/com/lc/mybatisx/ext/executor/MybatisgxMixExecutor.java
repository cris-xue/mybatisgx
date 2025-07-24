package com.lc.mybatisx.ext.executor;

import com.lc.mybatisx.context.MethodInfoContextHolder;
import com.lc.mybatisx.ext.MybatisxConfiguration;
import com.lc.mybatisx.model.MethodInfo;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * mybatisgx混合执行器
 *
 * @author ccxuef
 * @date 2025/7/24 8:33
 */
public class MybatisgxMixExecutor implements Executor {

    private MybatisxConfiguration configuration;
    private Transaction transaction;
    private ExecutorType executorType;
    private Executor delegate;

    public MybatisgxMixExecutor(MybatisxConfiguration configuration, Transaction transaction, ExecutorType executorType) {
        this.configuration = configuration;
        this.transaction = transaction;
        this.executorType = executorType;
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        this.newExecutor(ms);
        return this.delegate.update(ms, parameter);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException {
        this.newExecutor(ms);
        return this.delegate.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        this.newExecutor(ms);
        return this.delegate.query(ms, parameter, rowBounds, resultHandler);
    }

    @Override
    public <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException {
        this.newExecutor(ms);
        return this.delegate.queryCursor(ms, parameter, rowBounds);
    }

    @Override
    public List<BatchResult> flushStatements() throws SQLException {
        this.newExecutor();
        return this.delegate.flushStatements();
    }

    @Override
    public void commit(boolean required) throws SQLException {
        this.newExecutor();
        this.delegate.commit(required);
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        this.newExecutor();
        this.delegate.rollback(required);
    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        this.newExecutor(ms);
        return this.delegate.createCacheKey(ms, parameterObject, rowBounds, boundSql);
    }

    @Override
    public boolean isCached(MappedStatement ms, CacheKey key) {
        this.newExecutor(ms);
        return this.delegate.isCached(ms, key);
    }

    @Override
    public void clearLocalCache() {
        this.newExecutor();
        this.delegate.clearLocalCache();
    }

    @Override
    public void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
        this.newExecutor(ms);
        this.delegate.deferLoad(ms, resultObject, property, key, targetType);
    }

    @Override
    public Transaction getTransaction() {
        this.newExecutor();
        return this.delegate.getTransaction();
    }

    @Override
    public void close(boolean forceRollback) {
        this.newExecutor();
        this.delegate.close(forceRollback);
    }

    @Override
    public boolean isClosed() {
        this.newExecutor();
        return this.delegate.isClosed();
    }

    @Override
    public void setExecutorWrapper(Executor executor) {
        this.delegate.setExecutorWrapper(executor);
    }

    void newExecutor() {
        this.newExecutor(null);
    }

    void newExecutor(MappedStatement mappedStatement) {
        if (this.delegate != null) {
            return;
        }
        MethodInfo methodInfo = MethodInfoContextHolder.get(mappedStatement.getId());
        ExecutorType executorType = methodInfo != null && methodInfo.getBatch() ? ExecutorType.BATCH : this.executorType;
        this.delegate = this.configuration.getExecutor(this.transaction, executorType);
    }
}
