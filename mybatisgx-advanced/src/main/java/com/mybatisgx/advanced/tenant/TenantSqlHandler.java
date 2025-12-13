package com.mybatisgx.advanced.tenant;

import com.mybatisgx.advanced.interceptor.MappedStatementHelper;
import com.mybatisgx.advanced.interceptor.MybatisgxExecutorInfo;
import com.mybatisgx.advanced.interceptor.SqlHandler;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public class TenantSqlHandler implements SqlHandler {

    /**
     * mappedStatement参数不能直接修改，这个参数都是从【MybatisxConfiguration.getMappedStatement();】中获取的。直接修改等于把元数据进行了修改
     *
     * @param mybatisgxExecutorInfo
     * @return
     */
    public void process(MybatisgxExecutorInfo mybatisgxExecutorInfo) {
        MappedStatement mappedStatement = mybatisgxExecutorInfo.getMappedStatement();
        Object parameter = mybatisgxExecutorInfo.getParameterObject();
        RowBounds rowBounds = mybatisgxExecutorInfo.getRowBounds();
        ResultHandler resultHandler = mybatisgxExecutorInfo.getResultHandler();
        CacheKey cacheKey = mybatisgxExecutorInfo.getCacheKey();
        try {
            String tenantId = TenantContextHolder.get();
            // 重写SQL：添加租户条件
            Configuration configuration = mappedStatement.getConfiguration();
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
            Object parameterObject = boundSql.getParameterObject();
            String newSql = rewriteSqlWithTenant(boundSql.getSql(), tenantId);
            // 创建新BoundSql
            BoundSql newBoundSql = new BoundSql(configuration, newSql, parameterMappingList, parameterObject);
            // 替换原MappedStatement和parameterObject。
            MappedStatement newMappedStatement = MappedStatementHelper.copy(mappedStatement, newBoundSql, "TenantId");
            mybatisgxExecutorInfo.restArgs(newMappedStatement, parameterObject, rowBounds, resultHandler, cacheKey, boundSql);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
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
}
