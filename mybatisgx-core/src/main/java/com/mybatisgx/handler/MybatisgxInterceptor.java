package com.mybatisgx.handler;

import com.mybatisgx.context.MybatisgxObjectFactory;
import com.mybatisgx.executor.MybatisgxParameterHandler;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

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
public class MybatisgxInterceptor implements Interceptor {

    private List<SqlHandler> sqlHandlerList = new ArrayList();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Executor executor = (Executor) invocation.getTarget();
        Object[] args = invocation.getArgs();
        MybatisgxExecutorInfo mybatisgxExecutorInfo = new MybatisgxExecutorInfo(args);
        MappedStatement mappedStatement = mybatisgxExecutorInfo.getMappedStatement();
        Object parameterObject = mybatisgxExecutorInfo.getParameterObject();

        MybatisgxParameterHandler mybatisgxParameterHandler = MybatisgxObjectFactory.get(MybatisgxParameterHandler.class);
        parameterObject = mybatisgxParameterHandler.fillParameterObject(mappedStatement, parameterObject);

        for (SqlHandler sqlHandler : sqlHandlerList) {
            sqlHandler.process(mybatisgxExecutorInfo);
        }
        return invocation.proceed();
    }

    @Override
    public void setProperties(Properties properties) {
        Collection<Object> values = properties.values();
        for (Object value : values) {
            if (value instanceof String) {
                try {
                    Class<?> sqlHandlerClass = Class.forName((String) value);
                    sqlHandlerList.add((SqlHandler) sqlHandlerClass.newInstance());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
