package com.lc.mybatisx.ext;

import com.lc.mybatisx.annotation.handler.IdGenerateValueHandler;
import com.lc.mybatisx.ext.executor.MybatisgxBatchExecutor;
import com.lc.mybatisx.ext.executor.MybatisgxMixExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

public class MybatisxConfiguration extends Configuration {

    public MybatisxConfiguration() {
        super();
    }

    public MybatisxConfiguration(Environment environment) {
        super(environment);
    }

    public void setIdGenerateValueHandler(IdGenerateValueHandler<?> idGenerateValueHandler) {
    }

    @Override
    public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
        Executor defaultExecutor = super.newExecutor(transaction, executorType);
        Executor batchExecutor = super.newExecutor(transaction, ExecutorType.BATCH);
        return new MybatisgxMixExecutor(defaultExecutor, new MybatisgxBatchExecutor(batchExecutor));
    }

    @Override
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        return super.newStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
    }

    @Override
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql) {
        ResultSetHandler resultSetHandler;
        /*if (boundSql instanceof BatchSelectBoundSql) {
            resultSetHandler = new MybatisgxResultSetHandler(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
            resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
        } else {
            executor = (Executor) interceptorChain.pluginAll(executor);
            resultSetHandler = super.newResultSetHandler(executor, mappedStatement, rowBounds, parameterHandler, resultHandler, boundSql);
        }*/
        resultSetHandler = new MybatisgxResultSetHandler(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
        return resultSetHandler;
    }
}