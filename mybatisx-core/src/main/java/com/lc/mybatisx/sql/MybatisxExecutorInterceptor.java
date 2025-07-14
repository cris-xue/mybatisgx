package com.lc.mybatisx.sql;

import com.lc.mybatisx.annotation.handler.IdGenerateValueHandler;
import com.lc.mybatisx.dao.Page;
import com.lc.mybatisx.dao.Pageable;
import com.lc.mybatisx.scripting.MybatisxParameterHandler;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

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

        SqlHandler sqlHandler = new SqlHandler();
        BoundSql newBoundSql = sqlHandler.process(mappedStatement, parameterObject);

        Method method = invocation.getMethod();
        String methodName = method.getName();
        if (MybatisxSqlCommandType.QUERY.equalsIgnoreCase(methodName)) {
            PageHandler pageHandler = new PageHandler();
            Pageable pageable = pageHandler.getPageable(parameterObject);
            if (pageable != null) {
                MybatisgxPageInfo mybatisgxPageInfo = pageHandler.execute(executor, mappedStatement, parameterObject, rowBounds, resultHandler, newBoundSql);
                if (mybatisgxExecutorInfo.getCache()) {
                    args[5] = mybatisgxPageInfo.getBoundSql();
                } else {
                    MappedStatement newMappedStatement = this.copyMappedStatement(mappedStatement, new BoundSqlSqlSource(mybatisgxPageInfo.getBoundSql()));
                    args[0] = newMappedStatement;
                    args[1] = mybatisgxPageInfo.getParameterObject();
                }
                Object result = invocation.proceed();
                Page page = new Page(mybatisgxPageInfo.getCount(), (List) result);
                List<Page> pageList = new ArrayList(1);
                pageList.add(page);
                return pageList;
            }
        }

        MappedStatement newMappedStatement = copyMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
        // 替换原MappedStatement和parameterObject。
        args[0] = newMappedStatement;
        args[1] = parameterObject;
        if (args.length == 6) {
            args[5] = newBoundSql;
        }
        return invocation.proceed();
    }

    private MappedStatement copyMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        // 1. 获取原始MappedStatement的构建器
        MappedStatement.Builder builder = new MappedStatement.Builder(
                ms.getConfiguration(),
                ms.getId() + "_TenantModified",  // 新ID，避免与原ID冲突
                newSqlSource,                    // 注入新SQLSource
                ms.getSqlCommandType()           // 保留原SQL命令类型
        );

        // 2. 复制关键属性
        builder.resource(ms.getResource())
                .fetchSize(ms.getFetchSize())
                .timeout(ms.getTimeout())
                .statementType(ms.getStatementType())
                .keyGenerator(ms.getKeyGenerator())
                .keyProperty(ms.getKeyProperties() == null ? null : String.join(",", ms.getKeyProperties()))
                .keyColumn(ms.getKeyColumns() == null ? null : String.join(",", ms.getKeyColumns()))
                .databaseId(ms.getDatabaseId())
                .lang(ms.getLang())
                .resultOrdered(ms.isResultOrdered())
                .resultSets(ms.getResultSets() == null ? null : String.join(",", ms.getResultSets()))
                .resultMaps(ms.getResultMaps())              // 保留结果集映射
                .flushCacheRequired(ms.isFlushCacheRequired())
                .useCache(ms.isUseCache())
                .cache(ms.getCache());                       // 保留二级缓存配置

        // 3. 处理参数映射（防止因SQL改写导致参数绑定失效）
        if (ms.getParameterMap() != null) {
            builder.parameterMap(ms.getParameterMap());
        }

        // 4. 构建新MappedStatement
        return builder.build();
    }
}
