package com.lc.mybatisx.ext.executor;

import com.lc.mybatisx.context.MethodInfoContextHolder;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.model.MethodParamInfo;
import org.apache.ibatis.binding.MapperMethod;
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
import java.util.Collection;
import java.util.List;

public class MybatisgxBatchExecutor implements Executor {

    private Executor delegate;

    public MybatisgxBatchExecutor(Executor delegate) {
        this.delegate = delegate;
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        MethodInfo methodInfo = MethodInfoContextHolder.get(ms.getId());
        if (methodInfo.getBatch()) {
            List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
            MethodParamInfo dataMethodParamInfo = null;
            MethodParamInfo batchSizeMethodParamInfo = null;
            for (int i = 0; i < methodParamInfoList.size(); i++) {
                MethodParamInfo methodParamInfo = methodParamInfoList.get(i);
                Boolean isBatchSize = methodParamInfo.getBatchSize();
                if (isBatchSize) {
                    batchSizeMethodParamInfo = methodParamInfo;
                } else {
                    dataMethodParamInfo = methodParamInfo;
                }
            }

            MapperMethod.ParamMap<Object> mapperMethodParam = (MapperMethod.ParamMap<Object>) parameter;
            Collection collection = (Collection) mapperMethodParam.get(dataMethodParamInfo.getParamName());
            int batchSize = (int) mapperMethodParam.get(batchSizeMethodParamInfo.getParamName());
            List<Object> list = (List<Object>) collection;
            for (int i = 0; i < list.size(); i++) {
                Object item = list.get(i);
                mapperMethodParam.put(dataMethodParamInfo.getBatchItemName(), item);
                this.delegate.update(ms, mapperMethodParam);
                if ((i + 1) % batchSize == 0 || (i + 1) == list.size()) {
                    this.flushStatements();
                }
            }
            return list.size();
        } else {
            return this.delegate.update(ms, parameter);
        }
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException {
        return this.delegate.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        return this.delegate.query(ms, parameter, rowBounds, resultHandler);
    }

    @Override
    public <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException {
        return this.delegate.queryCursor(ms, parameter, rowBounds);
    }

    @Override
    public List<BatchResult> flushStatements() throws SQLException {
        return this.delegate.flushStatements();
    }

    @Override
    public void commit(boolean required) throws SQLException {
        this.delegate.commit(required);
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        this.delegate.rollback(required);
    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        return this.delegate.createCacheKey(ms, parameterObject, rowBounds, boundSql);
    }

    @Override
    public boolean isCached(MappedStatement ms, CacheKey key) {
        return this.delegate.isCached(ms, key);
    }

    @Override
    public void clearLocalCache() {
        this.delegate.clearLocalCache();
    }

    @Override
    public void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
        this.delegate.deferLoad(ms, resultObject, property, key, targetType);
    }

    @Override
    public Transaction getTransaction() {
        return this.delegate.getTransaction();
    }

    @Override
    public void close(boolean forceRollback) {
        this.delegate.close(forceRollback);
    }

    @Override
    public boolean isClosed() {
        return this.delegate.isClosed();
    }

    @Override
    public void setExecutorWrapper(Executor executor) {
        this.delegate.setExecutorWrapper(executor);
    }
}
