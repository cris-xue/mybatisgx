package com.mybatisgx.ext.session;

import com.mybatisgx.context.MybatisgxObjectFactory;
import com.mybatisgx.executor.MybatisgxParameterHandler;
import com.mybatisgx.ext.executor.MybatisgxBatchExecutor;
import com.mybatisgx.ext.executor.MybatisgxMixExecutor;
import com.mybatisgx.ext.executor.resultset.MybatisgxResultSetHandler;
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

public class MybatisgxConfiguration extends Configuration {

    public MybatisgxConfiguration() {
        super();
    }

    public MybatisgxConfiguration(Environment environment) {
        super(environment);
    }

    @Override
    public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
        Executor defaultExecutor = super.newExecutor(transaction, executorType);
        Executor batchExecutor = super.newExecutor(transaction, ExecutorType.BATCH);
        return new MybatisgxMixExecutor(defaultExecutor, new MybatisgxBatchExecutor(batchExecutor));
    }

    @Override
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        StatementHandler statementHandler = super.newStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
        MybatisgxParameterHandler mybatisgxParameterHandler = MybatisgxObjectFactory.get(MybatisgxParameterHandler.class);
        mybatisgxParameterHandler.fillParameterObject(mappedStatement, parameterObject, statementHandler.getBoundSql());
        return statementHandler;
    }

    @Override
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql) {
        ResultSetHandler resultSetHandler = new MybatisgxResultSetHandler(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
        return resultSetHandler;
    }
}