package com.mybatisgx.handler;

import com.mybatisgx.handler.page.MybatisxSqlCommandType;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

public class MybatisgxExecutorInfo {

    /**
     * 参数数量
     */
    private Integer count;
    /**
     * 类型   update select/select cache
     */
    private MybatisxSqlCommandType sqlCommandType;

    private Boolean isCache = false;

    private Invocation invocation;
    private Object[] args;
    private MappedStatement mappedStatement;
    private Object parameterObject;
    private RowBounds rowBounds;
    private ResultHandler resultHandler;
    private CacheKey cacheKey;
    private BoundSql boundSql;

    public MybatisgxExecutorInfo(Object[] args) {
        this.count = args.length;
        this.args = args;
        this.mappedStatement = (MappedStatement) args[0];
        this.parameterObject = args[1];
        if (args.length == 4 || args.length == 6) {
            this.isCache = false;
            this.rowBounds = (RowBounds) args[2];
            this.resultHandler = (ResultHandler) args[3];
        } else if (args.length == 6) {
            this.isCache = true;
            this.cacheKey = (CacheKey) args[4];
            this.boundSql = (BoundSql) args[5];
        } else {
            throw new RuntimeException("不支持的方法");
        }
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public MybatisxSqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType(MybatisxSqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }

    public Boolean getCache() {
        return isCache;
    }

    public void setCache(Boolean cache) {
        isCache = cache;
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    public MappedStatement getMappedStatement() {
        return mappedStatement;
    }

    public void setMappedStatement(MappedStatement mappedStatement) {
        this.mappedStatement = mappedStatement;
    }

    public Object getParameterObject() {
        return parameterObject;
    }

    public void setParameterObject(Object parameterObject) {
        this.parameterObject = parameterObject;
    }

    public RowBounds getRowBounds() {
        return rowBounds;
    }

    public void setRowBounds(RowBounds rowBounds) {
        this.rowBounds = rowBounds;
    }

    public ResultHandler getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }

    public CacheKey getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(CacheKey cacheKey) {
        this.cacheKey = cacheKey;
    }

    public BoundSql getBoundSql() {
        return boundSql;
    }

    public void setBoundSql(BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    public Object[] restArgs(MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) {
        this.args[0] = mappedStatement;
        this.args[1] = parameterObject;
        if (count == 4) {
            this.args[2] = rowBounds;
            this.args[3] = resultHandler;
        }
        if (count == 6) {
            this.args[4] = cacheKey;
            this.args[5] = boundSql;
        }
        return this.args;
    }
}
