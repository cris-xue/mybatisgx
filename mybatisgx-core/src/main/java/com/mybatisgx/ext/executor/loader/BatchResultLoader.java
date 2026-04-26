package com.mybatisgx.ext.executor.loader;

import com.mybatisgx.ext.LinkObjectHelper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.loader.ResultLoader;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BatchResultLoader extends ResultLoader {

    public static final String NESTED_SELECT_PARAM_COLLECTION = "nested_select_param_collection";

    private MetaObject parameterObjectMetaObject;

    private ResultMapping propertyMapping;

    private List<ResultMapping> idResultMappings;

    private BatchResultLoaderContext batchResultLoaderContext;

    public BatchResultLoader(
            Configuration configuration,
            Executor executor,
            MappedStatement mappedStatement,
            MetaObject parameterObject,
            Class<?> targetType,
            List<ResultMapping> idResultMappings,
            ResultMapping propertyMapping,
            BatchResultLoaderContext batchResultLoaderContext) {
        super(configuration, executor, mappedStatement, parameterObject.getOriginalObject(), targetType, null, null);
        this.idResultMappings = idResultMappings;
        this.parameterObjectMetaObject = parameterObject;
        this.propertyMapping = propertyMapping;
        this.batchResultLoaderContext = batchResultLoaderContext;
        this.batchResultLoaderContext.addParameterObject(parameterObject);
    }

    public MetaObject getParameterObjectMetaObject() {
        return parameterObjectMetaObject;
    }

    public Boolean getLazy() {
        return this.propertyMapping.isLazy();
    }

    public ResultMapping getPropertyMapping() {
        return propertyMapping;
    }

    public Object getParameterObject() {
        return this.parameterObject;
    }

    @Override
    public Object loadResult() throws SQLException {
        String objectKey = LinkObjectHelper.getObjectKey(this.idResultMappings, this.parameterObjectMetaObject);
        Map<String, Object> resultObjectMap = this.loadResultBatch();
        return resultObject = resultObjectMap.get(objectKey);
    }

    private Map<String, Object> loadResultBatch() throws SQLException {
        Map<String, Object> resultObjectMap = this.batchResultLoaderContext.getResultObject();
        if (ObjectUtils.isEmpty(resultObjectMap)) {
            List<Object> parameterObjectList = this.batchResultLoaderContext.getParameterObjectList();
            if (ObjectUtils.isNotEmpty(parameterObjectList)) {
                Map<String, List<Object>> parameterObjectMap = this.getParameterObjectMap(parameterObjectList);
                Object result = this.select(parameterObjectMap);
                resultObjectMap = this.batchResultLoaderContext.addResultObject(result, this.idResultMappings, propertyMapping).getResultObject();
            }
        }
        return resultObjectMap;
    }

    private Map<String, List<Object>> getParameterObjectMap(List<Object> parameterObjectList) {
        Map<String, List<Object>> parameterObjectMap = new HashMap();
        parameterObjectMap.put(NESTED_SELECT_PARAM_COLLECTION, parameterObjectList);
        return parameterObjectMap;
    }

    private Object select(Map<String, List<Object>> parameterObjectMap) throws SQLException {
        Executor localExecutor = executor;
        if (Thread.currentThread().getId() != this.creatorThreadId || localExecutor.isClosed()) {
            localExecutor = newExecutor();
        }
        try {
            BoundSql boundSql = mappedStatement.getBoundSql(parameterObjectMap);
            CacheKey cacheKey = localExecutor.createCacheKey(mappedStatement, parameterObjectMap, RowBounds.DEFAULT, boundSql);
            ResultLoader resultLoader = new ResultLoader(configuration, localExecutor, mappedStatement, parameterObjectMap, List.class, cacheKey, boundSql);
            return resultLoader.loadResult();
        } finally {
            if (localExecutor != executor) {
                localExecutor.close(false);
            }
        }
    }

    private Executor newExecutor() {
        final Environment environment = configuration.getEnvironment();
        if (environment == null) {
            throw new ExecutorException("ResultLoader could not load lazily.  Environment was not configured.");
        }
        final DataSource ds = environment.getDataSource();
        if (ds == null) {
            throw new ExecutorException("ResultLoader could not load lazily.  DataSource was not configured.");
        }
        final TransactionFactory transactionFactory = environment.getTransactionFactory();
        final Transaction tx = transactionFactory.newTransaction(ds, null, false);
        return configuration.newExecutor(tx, ExecutorType.SIMPLE);
    }

    public static class BatchResultLoaderContext {

        private List<Object> parameterObjectList = new ArrayList();

        private Map<String, Object> rightResultObjectMap = new ConcurrentHashMap();

        public void addParameterObject(MetaObject parameterObject) {
            this.parameterObjectList.add(parameterObject.getOriginalObject());
        }

        public List<Object> getParameterObjectList() {
            return this.parameterObjectList;
        }

        public BatchResultLoader.BatchResultLoaderContext addResultObject(Object resultObject, List<ResultMapping> idResultMappings, ResultMapping propertyMapping) {
            if (resultObject instanceof List) {
                List<Object> rightValueList = (List<Object>) resultObject;
                for (Object rightValue : rightValueList) {
                    MetaObject rightValueMetaObject = SystemMetaObject.forObject(rightValue);
                    String objectKey = LinkObjectHelper.getObjectKey(idResultMappings, rightValueMetaObject);
                    Object linkRightValue = rightValueMetaObject.getValue(propertyMapping.getProperty());
                    if (ObjectUtils.isNotEmpty(linkRightValue)) {
                        this.rightResultObjectMap.put(objectKey, linkRightValue);
                    }
                }
            }
            return this;
        }

        public Map<String, Object> getResultObject() {
            return this.rightResultObjectMap;
        }
    }
}
