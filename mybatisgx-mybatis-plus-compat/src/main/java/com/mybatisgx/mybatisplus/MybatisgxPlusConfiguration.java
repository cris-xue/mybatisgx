package com.mybatisgx.mybatisplus;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.mybatisgx.executor.MybatisgxValueProcessor;
import com.mybatisgx.ext.executor.MybatisgxBatchExecutor;
import com.mybatisgx.ext.executor.MybatisgxRoutingExecutor;
import com.mybatisgx.ext.executor.resultset.MybatisgxResultSetHandler;
import com.mybatisgx.ext.session.MybatisgxConfigurationAware;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MapperInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.utils.MethodInfoUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MyBatisGX 与 MyBatis-Plus 共存的 Configuration
 *
 * <p>继承 MP 的 {@link MybatisConfiguration}（获得 MybatisMapperRegistry、addMappedStatement 去重等能力），
 * 同时实现 MyBatisGX 的 {@link MybatisgxConfigurationAware}（entityInfo/methodInfo 存取），
 * 并将 MyBatisGX 的 newExecutor / newParameterHandler / newResultSetHandler 增强叠加到 MP 之上。</p>
 *
 * <p>MP 3.5.10 不覆盖 newParameterHandler / newResultSetHandler，newExecutor 为透传，
 * 故合并时不存在逻辑冲突；对 MP 的 MappedStatement 运行时按 MethodInfo 有无自动分流，无副作用。</p>
 *
 * @author ccxuef
 * @description MyBatisGX 与 MyBatis-Plus 共存 Configuration
 * @date 2026/8/15
 */
public class MybatisgxPlusConfiguration extends MybatisConfiguration implements MybatisgxConfigurationAware {

    private static final MybatisgxValueProcessor mybatisgxValueProcessor = new MybatisgxValueProcessor();

    protected final Map<Class<?>, EntityInfo> entityInfoMap = new ConcurrentHashMap();

    protected final Map<String, MethodInfo> methodInfoMap = new StrictMap("methodInfo collection");

    public MybatisgxPlusConfiguration() {
        this(null);
    }

    public MybatisgxPlusConfiguration(Environment environment) {
        super(environment);
    }

    @Override
    public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
        Executor defaultExecutor = super.newExecutor(transaction, executorType);
        Executor batchExecutor = super.newExecutor(transaction, ExecutorType.BATCH);
        return new MybatisgxRoutingExecutor(defaultExecutor, new MybatisgxBatchExecutor(batchExecutor));
    }

    @Override
    public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        this.mybatisgxValueProcessor.process(mappedStatement, parameterObject, boundSql);
        return super.newParameterHandler(mappedStatement, parameterObject, boundSql);
    }

    @Override
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql) {
        ResultSetHandler resultSetHandler = new MybatisgxResultSetHandler(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
        return resultSetHandler;
    }

    @Override
    public EntityInfo getEntityInfo(Class<?> clazz) {
        return this.entityInfoMap.containsKey(clazz) ? this.entityInfoMap.get(clazz) : null;
    }

    @Override
    public List<Class<?>> getEntityClassList() {
        return new ArrayList(this.entityInfoMap.keySet());
    }

    @Override
    public void addEntityInfo(EntityInfo entityInfo) {
        this.entityInfoMap.put(entityInfo.getClazz(), entityInfo);
    }

    @Override
    public MethodInfo getMethodInfo(MappedStatement ms) {
        return this.getMethodInfo(ms.getId());
    }

    @Override
    public MethodInfo getMethodInfo(String msId) {
        return this.methodInfoMap.containsKey(msId) ? this.methodInfoMap.get(msId) : null;
    }

    @Override
    public void addMethodInfo(MethodInfo methodInfo) {
        MapperInfo mapperInfo = methodInfo.getMapperInfo();
        String namespaceMethodName = MethodInfoUtils.getNamespaceMethodName(mapperInfo.getNamespace(), methodInfo.getMethodName());
        this.methodInfoMap.put(namespaceMethodName, methodInfo);
    }
}
