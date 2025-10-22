package com.lc.mybatisx.ext;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.loader.ResultLoader;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;

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

    private ResultMap resultMap;

    private Map<String, MetaObject> rightObjectMap = new ConcurrentHashMap();

    private BatchResultLoaderContext batchResultLoaderContext;

    public BatchResultLoader(Configuration configuration, Executor executor, MappedStatement mappedStatement, MetaObject parameterObject, Class<?> targetType, BatchResultLoaderContext batchResultLoaderContext) {
        super(configuration, executor, mappedStatement, parameterObject.getOriginalObject(), targetType, null, null);
        this.resultMap = mappedStatement.getResultMaps().get(0);
        this.parameterObjectMetaObject = parameterObject;
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

    public void setPropertyMapping(ResultMapping propertyMapping) {
        this.propertyMapping = propertyMapping;
    }

    public ResultMap getResultMap() {
        return resultMap;
    }

    public void setResultMap(ResultMap resultMap) {
        this.resultMap = resultMap;
    }

    public Object getParameterObject() {
        return this.parameterObject;
    }

    public Map<String, MetaObject> getRightObjectMap() {
        return rightObjectMap;
    }

    @Override
    public Object loadResult() throws SQLException {
        String objectKey = LinkObjectHelper.getObjectKey(resultMap.getIdResultMappings(), this.parameterObjectMetaObject);
        Map<String, Object> resultObjectMap = this.loadResultBatch();
        return resultObject = resultObjectMap.get(objectKey);
    }

    private Map<String, Object> loadResultBatch() throws SQLException {
        Map<String, Object> resultObjectMap = this.batchResultLoaderContext.getResultObject();
        if (ObjectUtils.isEmpty(resultObjectMap)) {
            List<Object> parameterObjectList = this.batchResultLoaderContext.getParameterObjectList();
            if (ObjectUtils.isNotEmpty(parameterObjectList)) {
                Map<String, List<Object>> parameterObjectMap = new HashMap();
                parameterObjectMap.put(NESTED_SELECT_PARAM_COLLECTION, parameterObjectList);

                BoundSql boundSql = mappedStatement.getBoundSql(parameterObjectMap);
                CacheKey cacheKey = executor.createCacheKey(mappedStatement, parameterObjectMap, RowBounds.DEFAULT, boundSql);
                ResultLoader resultLoader = new ResultLoader(configuration, executor, mappedStatement, parameterObjectMap, List.class, cacheKey, boundSql);
                Object result = resultLoader.loadResult();
                List<ResultMapping> idResultMappings = resultMap.getIdResultMappings();
                resultObjectMap = this.batchResultLoaderContext.addResultObject(result, idResultMappings, propertyMapping).getResultObject();
            }
        }
        return resultObjectMap;
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
                    this.rightResultObjectMap.put(objectKey, linkRightValue);
                }
            }
            return this;
        }

        public Map<String, Object> getResultObject() {
            return this.rightResultObjectMap;
        }
    }
}
