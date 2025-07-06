package com.lc.mybatisx.sql;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
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

@Intercepts(
        {
                @Signature(
                        type = Executor.class,
                        method = "update",
                        args = {MappedStatement.class, Object.class}
                ),
                @Signature(
                        type = Executor.class,
                        method = "query",
                        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
                )
        }
)
public class ExecutorInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
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

        return invocation.proceed();
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
