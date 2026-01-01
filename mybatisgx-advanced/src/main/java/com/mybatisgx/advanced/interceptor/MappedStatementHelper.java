package com.mybatisgx.advanced.interceptor;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

public class MappedStatementHelper {

    public static MappedStatement copy(MappedStatement mappedStatement, BoundSql boundSql, String methodSuffixName) {
        BoundSqlSource boundSqlSource = new BoundSqlSource(boundSql);
        // 1. 获取原始MappedStatement的构建器
        MappedStatement.Builder builder = new MappedStatement.Builder(
                mappedStatement.getConfiguration(),
                mappedStatement.getId() + methodSuffixName,  // 新ID，避免与原ID冲突
                boundSqlSource,                    // 注入新SQLSource
                mappedStatement.getSqlCommandType()           // 保留原SQL命令类型
        );

        // 2. 复制关键属性
        builder.resource(mappedStatement.getResource())
                .fetchSize(mappedStatement.getFetchSize())
                .timeout(mappedStatement.getTimeout())
                .statementType(mappedStatement.getStatementType())
                .keyGenerator(mappedStatement.getKeyGenerator())
                .keyProperty(mappedStatement.getKeyProperties() == null ? null : String.join(",", mappedStatement.getKeyProperties()))
                .keyColumn(mappedStatement.getKeyColumns() == null ? null : String.join(",", mappedStatement.getKeyColumns()))
                .databaseId(mappedStatement.getDatabaseId())
                .lang(mappedStatement.getLang())
                .resultOrdered(mappedStatement.isResultOrdered())
                .resultSets(mappedStatement.getResultSets() == null ? null : String.join(",", mappedStatement.getResultSets()))
                .resultMaps(mappedStatement.getResultMaps())              // 保留结果集映射
                .flushCacheRequired(mappedStatement.isFlushCacheRequired())
                .useCache(mappedStatement.isUseCache())
                .cache(mappedStatement.getCache());                       // 保留二级缓存配置

        // 3. 处理参数映射（防止因SQL改写导致参数绑定失效）
        if (mappedStatement.getParameterMap() != null) {
            builder.parameterMap(mappedStatement.getParameterMap());
        }

        // 4. 构建新MappedStatement
        return builder.build();
    }
}
