package com.mybatisgx.ext.executor;

import com.github.pagehelper.PageHelper;
import com.mybatisgx.context.MethodInfoContextHolder;
import com.mybatisgx.executor.page.Page;
import com.mybatisgx.executor.page.Pageable;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.MethodParamInfo;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * mybatisgx路由执行器，根据分页、批量处理调用不用的执行器
 *
 * @author ccxuef
 * @date 2025/7/24 8:33
 */
public class MybatisgxRoutingExecutor implements Executor {

    private final Executor defaultExecutor;
    private final Executor batchExecutor;

    public MybatisgxRoutingExecutor(Executor defaultExecutor, Executor batchExecutor) {
        this.defaultExecutor = defaultExecutor;
        this.batchExecutor = batchExecutor;
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        Executor delegate = this.resolveExecutor(ms);
        return delegate.update(ms, parameter);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException {
        return this.executeQuery(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        return this.executeQuery(ms, parameter, rowBounds, resultHandler, null, null);
    }

    private <E> List<E> executeQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException {
        MethodInfo methodInfo = MethodInfoContextHolder.get(ms.getId());
        Executor delegate = this.resolveExecutor(ms, methodInfo);
        MethodParamInfo pageParamInfo = methodInfo.getPageParamInfo();

        // 非分页
        if (pageParamInfo == null) {
            return cacheKey == null
                    ? delegate.query(ms, parameter, rowBounds, resultHandler)
                    : delegate.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
        }

        Pageable pageable = this.getPageable(parameter, pageParamInfo);
        com.github.pagehelper.Page pagehelperPage = PageHelper.startPage(pageable.getPageNo(), pageable.getPageSize());

        List<Object> list = cacheKey == null && boundSql == null
                ? delegate.query(ms, parameter, rowBounds, resultHandler)
                : delegate.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);

        Page<Object> page = new Page(pagehelperPage.getTotal(), list);
        return (List<E>) Collections.singletonList(page);
    }

    private Pageable getPageable(Object parameter, MethodParamInfo pageParamInfo) {
        return (Pageable) (pageParamInfo.getWrapper()
                ? ((Map<String, Object>) parameter).get(pageParamInfo.getArgName())
                : parameter);
    }

    @Override
    public <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException {
        Executor delegate = this.resolveExecutor(ms);
        return delegate.queryCursor(ms, parameter, rowBounds);
    }

    @Override
    public List<BatchResult> flushStatements() throws SQLException {
        Executor delegate = this.resolveExecutor();
        return delegate.flushStatements();
    }

    @Override
    public void commit(boolean required) throws SQLException {
        Executor delegate = this.resolveExecutor();
        delegate.commit(required);
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        Executor delegate = this.resolveExecutor();
        delegate.rollback(required);
    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        Executor delegate = this.resolveExecutor(ms);
        return delegate.createCacheKey(ms, parameterObject, rowBounds, boundSql);
    }

    @Override
    public boolean isCached(MappedStatement ms, CacheKey key) {
        Executor delegate = this.resolveExecutor(ms);
        return delegate.isCached(ms, key);
    }

    @Override
    public void clearLocalCache() {
        Executor delegate = this.resolveExecutor();
        delegate.clearLocalCache();
    }

    @Override
    public void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
        Executor delegate = this.resolveExecutor(ms);
        delegate.deferLoad(ms, resultObject, property, key, targetType);
    }

    @Override
    public Transaction getTransaction() {
        Executor delegate = this.resolveExecutor();
        return delegate.getTransaction();
    }

    @Override
    public void close(boolean forceRollback) {
        Executor delegate = this.resolveExecutor();
        delegate.close(forceRollback);
    }

    @Override
    public boolean isClosed() {
        Executor delegate = this.resolveExecutor();
        return delegate.isClosed();
    }

    @Override
    public void setExecutorWrapper(Executor executor) {
        Executor delegate = this.resolveExecutor();
        delegate.setExecutorWrapper(executor);
    }

    private Executor resolveExecutor() {
        return this.resolveExecutor(null);
    }

    private Executor resolveExecutor(MappedStatement mappedStatement) {
        MethodInfo methodInfo = this.getMethodInfo(mappedStatement, null);
        return this.resolveExecutor(mappedStatement, methodInfo);
    }

    private Executor resolveExecutor(MappedStatement mappedStatement, MethodInfo methodInfo) {
        methodInfo = this.getMethodInfo(mappedStatement, methodInfo);
        return methodInfo != null && methodInfo.getBatch() ? batchExecutor : defaultExecutor;
    }

    private MethodInfo getMethodInfo(MappedStatement mappedStatement, MethodInfo methodInfo) {
        return mappedStatement != null && methodInfo == null ? MethodInfoContextHolder.get(mappedStatement.getId()) : methodInfo;
    }
}
