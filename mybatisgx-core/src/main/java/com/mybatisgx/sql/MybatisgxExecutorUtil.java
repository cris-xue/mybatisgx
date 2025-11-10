package com.mybatisgx.sql;

import com.github.pagehelper.BoundSqlInterceptor;
import com.github.pagehelper.Dialect;
import com.github.pagehelper.util.ExecutorUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.Map;

public class MybatisgxExecutorUtil extends ExecutorUtil {

    public static MybatisgxPageInfo getPageInfo(Dialect dialect, Executor executor, MappedStatement ms, Object parameter,
                                                RowBounds rowBounds, ResultHandler resultHandler,
                                                BoundSql boundSql, CacheKey cacheKey) throws SQLException {
        //判断是否需要进行分页查询
        if (dialect.beforePage(ms, parameter, rowBounds)) {
            //生成分页的缓存 key
            CacheKey pageKey = cacheKey;
            //处理参数对象
            parameter = dialect.processParameterObject(ms, parameter, boundSql, pageKey);
            //调用方言获取分页 sql
            String pageSql = dialect.getPageSql(ms, boundSql, parameter, rowBounds, pageKey);
            BoundSql pageBoundSql = new BoundSql(ms.getConfiguration(), pageSql, boundSql.getParameterMappings(), parameter);

            Map<String, Object> additionalParameters = getAdditionalParameter(boundSql);
            //设置动态参数
            for (String key : additionalParameters.keySet()) {
                pageBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
            }
            //对 boundSql 的拦截处理
            if (dialect instanceof BoundSqlInterceptor.Chain) {
                pageBoundSql = ((BoundSqlInterceptor.Chain) dialect).doBoundSql(BoundSqlInterceptor.Type.PAGE_SQL, pageBoundSql, pageKey);
            }
            //执行分页查询

            MybatisgxPageInfo mybatisgxPageInfo = new MybatisgxPageInfo();
            mybatisgxPageInfo.setMappedStatement(ms);
            mybatisgxPageInfo.setParameterObject(parameter);
            mybatisgxPageInfo.setRowBounds(RowBounds.DEFAULT);
            mybatisgxPageInfo.setResultHandler(resultHandler);
            mybatisgxPageInfo.setBoundSql(pageBoundSql);
            mybatisgxPageInfo.setCacheKey(pageKey);
            return mybatisgxPageInfo;
            // return Arrays.asList(ms, parameter, RowBounds.DEFAULT, resultHandler, pageKey, pageBoundSql); // executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, pageKey, pageBoundSql);
        } else {
            //不执行分页的情况下，也不执行内存分页
            MybatisgxPageInfo mybatisgxPageInfo = new MybatisgxPageInfo();
            mybatisgxPageInfo.setMappedStatement(ms);
            mybatisgxPageInfo.setParameterObject(parameter);
            mybatisgxPageInfo.setRowBounds(RowBounds.DEFAULT);
            mybatisgxPageInfo.setResultHandler(resultHandler);
            mybatisgxPageInfo.setBoundSql(boundSql);
            mybatisgxPageInfo.setCacheKey(cacheKey);

            return mybatisgxPageInfo; // executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, boundSql);
        }
    }
}
