package com.lc.mybatisx.handler;

import com.github.pagehelper.BoundSqlInterceptor;
import com.github.pagehelper.Dialect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.cache.Cache;
import com.github.pagehelper.util.ExecutorUtil;
import com.github.pagehelper.util.MSUtils;
import com.lc.mybatisx.dao.Page;
import com.lc.mybatisx.dao.Pageable;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.*;

public class PageHandler {

    private volatile Dialect dialect = new PageHelper();
    private String countSuffix = "_COUNT";
    protected Cache<String, MappedStatement> msCountMap = null;
    private String default_dialect_class = "com.github.pagehelper.PageHelper";
    private Pageable pageable;

    public PageHandler() {
        dialect.setProperties(new Properties());
    }

    public Pageable getPageable(Object parameterObject) {
        if (parameterObject instanceof Map) {
            Map<String, Object> parameterObjectMap = (Map<String, Object>) parameterObject;
            Collection<Object> parameterObjectCollection = parameterObjectMap.values();
            for (Object object : parameterObjectCollection) {
                if (object instanceof Pageable) {
                    this.pageable = (Pageable) object;
                    break;
                }
            }
        }
        return this.pageable;
    }

    public Object execute(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        try {
            CacheKey cacheKey = executor.createCacheKey(mappedStatement, parameterObject, rowBounds, boundSql);
            PageHelper.startPage(pageable.getPageNo(), pageable.getPageSize());
            Object result = execute(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql, cacheKey);
            Page page = new Page(10, (List) result);
            List<Page> pageList = new ArrayList(1);
            pageList.add(page);
            return pageList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Object execute(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql, CacheKey cacheKey) throws Throwable {
        try {
            //对 boundSql 的拦截处理
            if (dialect instanceof BoundSqlInterceptor.Chain) {
                boundSql = ((BoundSqlInterceptor.Chain) dialect).doBoundSql(BoundSqlInterceptor.Type.ORIGINAL, boundSql, cacheKey);
            }
            List resultList;
            //调用方法判断是否需要进行分页，如果不需要，直接返回结果
            if (!dialect.skip(mappedStatement, parameterObject, rowBounds)) {
                //判断是否需要进行 count 查询
                if (dialect.beforeCount(mappedStatement, parameterObject, rowBounds)) {
                    //查询总数
                    Long count = count(executor, mappedStatement, parameterObject, rowBounds, null, boundSql);
                    //处理查询总数，返回 true 时继续分页查询，false 时直接返回
                    if (!dialect.afterCount(count, parameterObject, rowBounds)) {
                        //当查询总数为 0 时，直接返回空的结果
                        return dialect.afterPage(new ArrayList(), parameterObject, rowBounds);
                    }
                }
                resultList = ExecutorUtil.pageQuery(dialect, executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql, cacheKey);
            } else {
                //rowBounds用参数值，不使用分页插件处理时，仍然支持默认的内存分页
                resultList = executor.query(mappedStatement, parameterObject, rowBounds, resultHandler, cacheKey, boundSql);
            }
            return dialect.afterPage(resultList, parameterObject, rowBounds);
        } finally {
            if (dialect != null) {
                dialect.afterAll();
            }
        }
    }

    private Long count(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        String countMsId = ms.getId() + countSuffix;
        Long count;
        //先判断是否存在手写的 count 查询
        MappedStatement countMs = ExecutorUtil.getExistedMappedStatement(ms.getConfiguration(), countMsId);
        if (countMs != null) {
            count = ExecutorUtil.executeManualCount(executor, countMs, parameter, boundSql, resultHandler);
        } else {
            if (msCountMap != null) {
                countMs = msCountMap.get(countMsId);
            }
            //自动创建
            if (countMs == null) {
                //根据当前的 ms 创建一个返回值为 Long 类型的 ms
                countMs = MSUtils.newCountMappedStatement(ms, countMsId);
                if (msCountMap != null) {
                    msCountMap.put(countMsId, countMs);
                }
            }
            count = ExecutorUtil.executeAutoCount(this.dialect, executor, countMs, parameter, boundSql, rowBounds, resultHandler);
        }
        return count;
    }
}
