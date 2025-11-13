package com.mybatisgx.handler.page;

import com.mybatisgx.dao.Page;
import com.mybatisgx.dao.Pageable;
import com.mybatisgx.handler.MappedStatementHelper;
import com.mybatisgx.handler.MybatisgxExecutorInfo;
import com.mybatisgx.handler.SqlHandler;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2025/11/13 18:31
 */
public class PageSqlHandler implements SqlHandler {

    @Override
    public void process(MybatisgxExecutorInfo mybatisgxExecutorInfo) {
        Invocation invocation = mybatisgxExecutorInfo.getInvocation();
        Executor executor = (Executor) invocation.getTarget();
        MappedStatement mappedStatement = mybatisgxExecutorInfo.getMappedStatement();
        Object parameterObject = mybatisgxExecutorInfo.getParameterObject();
        RowBounds rowBounds = mybatisgxExecutorInfo.getRowBounds();
        ResultHandler resultHandler = mybatisgxExecutorInfo.getResultHandler();
        CacheKey cacheKey = mybatisgxExecutorInfo.getCacheKey();
        BoundSql boundSql = mybatisgxExecutorInfo.getBoundSql();

        Method method = invocation.getMethod();
        String methodName = method.getName();
        if (MybatisxSqlCommandType.QUERY.equalsIgnoreCase(methodName)) {
            PageHandler pageHandler = new PageHandler();
            Pageable pageable = pageHandler.getPageable(parameterObject);
            if (pageable != null) {
                if (boundSql == null) {
                    boundSql = mappedStatement.getBoundSql(parameterObject);
                }
                try {
                    this.processPage(executor, boundSql, pageHandler, invocation, mybatisgxExecutorInfo);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private List<Page> processPage(Executor executor, BoundSql newBoundSql, PageHandler pageHandler, Invocation invocation, MybatisgxExecutorInfo mybatisgxExecutorInfo) throws InvocationTargetException, IllegalAccessException {
        MappedStatement mappedStatement = mybatisgxExecutorInfo.getMappedStatement();
        Object parameterObject = mybatisgxExecutorInfo.getParameterObject();
        RowBounds rowBounds = mybatisgxExecutorInfo.getRowBounds();
        ResultHandler resultHandler = mybatisgxExecutorInfo.getResultHandler();
        CacheKey cacheKey = mybatisgxExecutorInfo.getCacheKey();
        BoundSql boundSql = mybatisgxExecutorInfo.getBoundSql();
        MybatisgxPageInfo mybatisgxPageInfo = pageHandler.execute(executor, mappedStatement, parameterObject, rowBounds, resultHandler, newBoundSql);
        MappedStatement newMappedStatement = MappedStatementHelper.copy(mappedStatement, mybatisgxPageInfo.getBoundSql(), "Page");
        mybatisgxExecutorInfo.restArgs(newMappedStatement, mybatisgxPageInfo.getParameterObject(), mybatisgxPageInfo.getRowBounds(), mybatisgxPageInfo.getResultHandler(), mybatisgxPageInfo.getCacheKey(), mybatisgxPageInfo.getBoundSql());
        Object result = invocation.proceed();
        Page page = new Page(mybatisgxPageInfo.getCount(), (List) result);
        List<Page> pageList = new ArrayList(1);
        pageList.add(page);
        return pageList;
    }
}
