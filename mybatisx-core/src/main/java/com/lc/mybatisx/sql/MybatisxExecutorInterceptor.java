package com.lc.mybatisx.sql;

import com.lc.mybatisx.annotation.handler.IdGenerateValueHandler;
import com.lc.mybatisx.dao.Pageable;
import com.lc.mybatisx.handler.PageHandler;
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
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameterObject = args[1];
        RowBounds rowBounds = null;
        ResultHandler resultHandler = null;
        CacheKey cacheKey;
        BoundSql boundSql = null;
        if (args.length == 2) {

        } else if (args.length == 4) {
            rowBounds = (RowBounds) args[2];
            resultHandler = (ResultHandler) args[3];
            // cacheKey = executor.createCacheKey(mappedStatement, parameterObject, rowBounds, boundSql);
            // boundSql = mappedStatement.getBoundSql(parameterObject);
        } else if (args.length == 6) {
            rowBounds = (RowBounds) args[2];
            resultHandler = (ResultHandler) args[3];
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        } else {
            throw new RuntimeException("不支持的方法");
        }

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
                return pageHandler.execute(executor, mappedStatement, parameterObject, rowBounds, resultHandler, newBoundSql);
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

    /*public void processExecutor(Invocation invocation) throws JSQLParserException {
        String tenantId = TenantContextHolder.get();
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];

        // 重写SQL：添加租户条件
        BoundSql boundSql = ms.getBoundSql(parameter);
        String newSql = rewriteSqlWithTenant(boundSql.getSql(), tenantId);

        // 创建新BoundSql
        BoundSql newBoundSql = new BoundSql(
                ms.getConfiguration(),
                newSql,
                boundSql.getParameterMappings(),
                boundSql.getParameterObject()
        );

        // 替换原MappedStatement
        MappedStatement newMs = copyMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
        args[0] = newMs;
    }

    public BoundSql processExecutor(MappedStatement mappedStatement, Object parameter) throws JSQLParserException {
        String tenantId = TenantContextHolder.get();

        // 重写SQL：添加租户条件
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String newSql = rewriteSqlWithTenant(boundSql.getSql(), tenantId);

        // 创建新BoundSql
        BoundSql newBoundSql = new BoundSql(
                mappedStatement.getConfiguration(),
                newSql,
                boundSql.getParameterMappings(),
                boundSql.getParameterObject()
        );

        // 替换原MappedStatement
        // mappedStatement = copyMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
        return newBoundSql;
    }

    private void processResultSet(Invocation invocation) throws JSQLParserException {
        String tenantId = TenantContextHolder.get();
        // 获取ResultSetHandler
        ResultSetHandler resultSetHandler = (ResultSetHandler) invocation.getTarget();

        // 通过反射获取嵌套查询的MappedStatement
        MetaObject metaObject = SystemMetaObject.forObject(resultSetHandler);
        MappedStatement ms = (MappedStatement) metaObject.getValue("mappedStatement");

        // 增强嵌套查询SQL
        BoundSql boundSql = ms.getBoundSql(null);
        String enhancedSql = rewriteSqlWithTenant(boundSql.getSql(), tenantId);

        // 创建新MappedStatement
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), enhancedSql,
                boundSql.getParameterMappings(),
                boundSql.getParameterObject());
        MappedStatement newMs = copyMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));

        // 替换当前MappedStatement
        metaObject.setValue("mappedStatement", newMs);
    }

    private String rewriteSqlWithTenant(String sql, String tenantId) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        // 根据SQL类型分发处理
        if (statement instanceof Select) {
            SelectHandler selectHandler = new SelectHandler();
            return selectHandler.processSelect(statement, tenantId);
        } else if (statement instanceof Insert) {
            InsertHandler insertHandler = new InsertHandler();
            return insertHandler.processInsert((Insert) statement, tenantId);
        } else if (statement instanceof Update) {
            UpdateHandler updateHandler = new UpdateHandler();
            return updateHandler.processUpdate((Update) statement, tenantId);
        } else if (statement instanceof Delete) {
            DeleteHandler deleteHandler = new DeleteHandler();
            return deleteHandler.processDelete((Delete) statement, tenantId);
        }
        return sql;
    }*/

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
