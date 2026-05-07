package com.mybatisgx.ext.session;

import com.mybatisgx.executor.MybatisgxValueProcessor;
import com.mybatisgx.executor.ValueProcessPrepareContext;
import com.mybatisgx.ext.executor.MybatisgxBatchExecutor;
import com.mybatisgx.ext.executor.MybatisgxRoutingExecutor;
import com.mybatisgx.ext.executor.resultset.MybatisgxResultSetHandler;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.utils.MethodInfoUtils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MybatisgxConfiguration extends Configuration {

    private static final MybatisgxValueProcessor mybatisgxValueProcessor = new MybatisgxValueProcessor();

    protected final Map<Class<?>, EntityInfo> entityInfoMap = new ConcurrentHashMap();

    protected final Map<String, MethodInfo> methodInfoMap = new StrictMap("methodInfo collection");

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
        return new MybatisgxRoutingExecutor(defaultExecutor, new MybatisgxBatchExecutor(batchExecutor));
    }

    @Override
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        StatementHandler statementHandler = super.newStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
        ValueProcessPrepareContext context = this.mybatisgxValueProcessor.prepare(mappedStatement, parameterObject);
        if (context.getProcess()) {
            this.mybatisgxValueProcessor.process(context, parameterObject, statementHandler.getBoundSql());
        }
        return statementHandler;
    }

    @Override
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql) {
        ResultSetHandler resultSetHandler = new MybatisgxResultSetHandler(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
        return resultSetHandler;
    }

    public EntityInfo getEntityInfo(Class<?> clazz) {
        return this.entityInfoMap.containsKey(clazz) ? this.entityInfoMap.get(clazz) : null;
    }

    public List<Class<?>> getEntityClassList() {
        return new ArrayList(this.entityInfoMap.keySet());
    }

    public void addEntityInfo(EntityInfo entityInfo) {
        this.entityInfoMap.put(entityInfo.getClazz(), entityInfo);
    }

    public MethodInfo getMethodInfo(MappedStatement ms) {
        return this.getMethodInfo(ms.getId());
    }

    public MethodInfo getMethodInfo(String msId) {
        return this.methodInfoMap.containsKey(msId) ? this.methodInfoMap.get(msId) : null;
    }

    public void addMethodInfo(MethodInfo methodInfo) {
        MapperInfo mapperInfo = methodInfo.getMapperInfo();
        String namespaceMethodName = MethodInfoUtils.getNamespaceMethodName(mapperInfo.getNamespace(), methodInfo.getMethodName());
        this.methodInfoMap.put(namespaceMethodName, methodInfo);
    }
}