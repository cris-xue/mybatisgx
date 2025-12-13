package com.mybatisgx.ext.executor;

import com.github.pagehelper.PageHelper;
import com.mybatisgx.context.MethodInfoContextHolder;
import com.mybatisgx.executor.page.Page;
import com.mybatisgx.executor.page.Pageable;
import com.mybatisgx.model.MethodInfo;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * mybatisgx混合执行器
 *
 * @author ccxuef
 * @date 2025/7/24 8:33
 */
public class MybatisgxMixExecutor implements Executor {

    private Executor delegate;
    private Executor defaultExecutor;
    private Executor batchExecutor;

    public MybatisgxMixExecutor(Executor defaultExecutor, Executor batchExecutor) {
        this.defaultExecutor = defaultExecutor;
        this.batchExecutor = batchExecutor;
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        this.newExecutor(ms);
        return this.delegate.update(ms, parameter);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException {
        this.newExecutor(ms);
        Pageable pageable = this.getPageable(parameter);
        if (pageable != null) {
            return this.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql, pageable);
        } else {
            return this.delegate.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
        }
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        this.newExecutor(ms);
        Pageable pageable = this.getPageable(parameter);
        if (pageable != null) {
            return this.query(ms, parameter, rowBounds, resultHandler, null, null, pageable);
        } else {
            return this.delegate.query(ms, parameter, rowBounds, resultHandler);
        }
    }

    private <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql, Pageable pageable) throws SQLException {
        com.github.pagehelper.Page pagehelperPage = PageHelper.startPage(pageable.getPageNo(), pageable.getPageSize());
        List<Object> list;
        if (cacheKey == null && boundSql == null) {
            list = this.delegate.query(ms, parameter, rowBounds, resultHandler);
        } else {
            list = this.delegate.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
        }
        Page<Object> page = new Page(pagehelperPage.getTotal(), list);
        return (List<E>) Arrays.asList(page);
    }

    private Pageable getPageable(Object parameterObject) {
        if (parameterObject instanceof Map) {
            Map<String, Object> parameterObjectMap = (Map<String, Object>) parameterObject;
            for (Object object : parameterObjectMap.values()) {
                if (object instanceof Pageable) {
                    return (Pageable) object;
                }
            }
        }
        return null;
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
        MethodInfo methodInfo = null;
        if (mappedStatement != null) {
            methodInfo = MethodInfoContextHolder.get(mappedStatement.getId());
        }
        this.delegate = methodInfo != null && methodInfo.getBatch() ? batchExecutor : defaultExecutor;
    }
}
