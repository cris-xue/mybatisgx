package com.lc.mybatisx.sql;

import com.lc.mybatisx.annotation.handler.IdGenerateValueHandler;
import com.lc.mybatisx.dao.Page;
import com.lc.mybatisx.dao.Pageable;
import com.lc.mybatisx.scripting.MybatisxParameterHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Intercepts({
        @Signature(
                type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}
        ),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
        ),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
        )
})
public class MybatisxExecutorInterceptor implements Interceptor {

    private IdGenerateValueHandler idGenerateValueHandler;
    private String tenantId = "";

    public MybatisxExecutorInterceptor(IdGenerateValueHandler idGenerateValueHandler) {
        this.idGenerateValueHandler = idGenerateValueHandler;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Executor executor = (Executor) invocation.getTarget();
        Object[] args = invocation.getArgs();
        MybatisgxExecutorInfo mybatisgxExecutorInfo = new MybatisgxExecutorInfo(args);
        MappedStatement mappedStatement = mybatisgxExecutorInfo.getMappedStatement();
        Object parameterObject = mybatisgxExecutorInfo.getParameterObject();
        RowBounds rowBounds = mybatisgxExecutorInfo.getRowBounds();
        ResultHandler resultHandler = mybatisgxExecutorInfo.getResultHandler();
        CacheKey cacheKey = mybatisgxExecutorInfo.getCacheKey();
        BoundSql boundSql = mybatisgxExecutorInfo.getBoundSql();

        MybatisxParameterHandler mybatisxParameterHandler = new MybatisxParameterHandler(this.idGenerateValueHandler);
        parameterObject = mybatisxParameterHandler.fillParameterObject(mappedStatement, parameterObject);

        // 租户功能会改变sql
        if (StringUtils.isNotBlank(tenantId)) {
            SqlHandler sqlHandler = new SqlHandler();
            boundSql = sqlHandler.process(mappedStatement, parameterObject);
            // 替换原MappedStatement和parameterObject。
            MappedStatement newMappedStatement = MappedStatementHelper.copy(mappedStatement, boundSql, "TenantId");
            mybatisgxExecutorInfo.restArgs(newMappedStatement, parameterObject, rowBounds, resultHandler, cacheKey, boundSql);
        }

        Method method = invocation.getMethod();
        String methodName = method.getName();
        if (MybatisxSqlCommandType.QUERY.equalsIgnoreCase(methodName)) {
            PageHandler pageHandler = new PageHandler();
            Pageable pageable = pageHandler.getPageable(parameterObject);
            if (pageable != null) {
                return this.processPage(executor, boundSql, pageHandler, invocation, mybatisgxExecutorInfo);
            }
        }
        return invocation.proceed();
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
