package com.lc.mybatisx.ext;

import com.lc.mybatisx.ext.mapping.BatchSelectResultMapping;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.loader.ResultLoader;
import org.apache.ibatis.executor.loader.ResultLoaderMap;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一句话描述
 * @author ccxuef
 * @date 2025/10/20 10:20
 */
public class MybatisgxResultSetHandler extends MybatisDefaultResultSetHandler {

    private Executor executor;
    private Configuration configuration;
    private BatchResultLoaderContext batchResultLoaderContext = new BatchResultLoaderContext();
    private Map<String, BatchResultLoader> batchResultLoaderMap = new ConcurrentHashMap();

    public MybatisgxResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        this.executor = executor;
        this.configuration = mappedStatement.getConfiguration();
    }

    @Override
    public List<Object> handleResultSets(Statement stmt) throws SQLException {
        List<Object> leftList = super.handleResultSets(stmt);
        for (String nestedQueryId : this.batchResultLoaderMap.keySet()) {
            BatchResultLoader batchResultLoader = this.batchResultLoaderMap.get(nestedQueryId);
            Object nestedQuery = batchResultLoader.loadResultEager();
            if (nestedQuery instanceof List) {
                List<Object> rightList = (List<Object>) nestedQuery;
                this.leftJoin(batchResultLoader, rightList);
            }
        }
        return leftList;
    }

    private void leftJoin(BatchResultLoader batchResultLoader, List<Object> rightValueList) {
        // 如果不存在连接值，不需要处理关联数据
        if (ObjectUtils.isEmpty(rightValueList)) {
            return;
        }

        List<ResultMapping> idResultMappings = batchResultLoader.getResultMap().getIdResultMappings();
        Map<String, MetaObject> leftMap = batchResultLoader.getRightObjectMap();
        BatchSelectResultMapping batchSelectResultMapping = (BatchSelectResultMapping) batchResultLoader.getPropertyMapping();
        String property = batchSelectResultMapping.getProperty();

        // 处理主键和对象的映射关系
        for (Object rightValue : rightValueList) {
            MetaObject rightMetaObject = this.configuration.newMetaObject(rightValue);
            String linkObjectKey = this.getObjectKey(idResultMappings, rightMetaObject);

            MetaObject leftMetaObject = leftMap.get(linkObjectKey);
            Object linkRightValue = rightMetaObject.getValue(property);

            this.linkObjects(leftMetaObject, batchSelectResultMapping, linkRightValue);
        }
    }

    private String getObjectKey(List<ResultMapping> idResultMappings, MetaObject metaObject) {
        if (metaObject.getOriginalObject() == null) {
            return "";
        }
        List<String> idValueList = new ArrayList<>();
        for (ResultMapping idResultMapping : idResultMappings) {
            Object idValue = metaObject.getValue(idResultMapping.getProperty());
            idValueList.add(idValue instanceof Long ? idValue.toString() : (String) idValue);
        }
        return StringUtils.join(idValueList, "");
    }

    @Override
    public Object getNestedQueryMappingValue(ResultSet rs, MetaObject metaResultObject, ResultMapping propertyMapping, ResultLoaderMap lazyLoader, String columnPrefix) throws SQLException {
        if (propertyMapping instanceof BatchSelectResultMapping) {
            String nestedQueryId = propertyMapping.getNestedQueryId();
            MappedStatement nestedQuery = configuration.getMappedStatement(nestedQueryId);
            ResultMap resultMap = nestedQuery.getResultMaps().get(0);

            ResultLoader resultLoader = this.buildBatchResultLoader(resultMap, propertyMapping, nestedQueryId, metaResultObject);
            lazyLoader.addLoader(propertyMapping.getProperty(), metaResultObject, resultLoader);
            return DEFERRED;
        }
        return super.getNestedQueryMappingValue(rs, metaResultObject, propertyMapping, lazyLoader, columnPrefix);
    }

    private ResultLoader buildBatchResultLoader(ResultMap resultMap, ResultMapping propertyMapping, String nestedQueryId, MetaObject metaResultObject) {
        MappedStatement nestedQuery = configuration.getMappedStatement(nestedQueryId);

        BatchResultLoader batchResultLoader = new BatchResultLoader(configuration, executor, nestedQuery, metaResultObject.getOriginalObject(), List.class, this.batchResultLoaderContext);
        batchResultLoader.setPropertyMapping(propertyMapping);
        batchResultLoaderMap.put(nestedQueryId, batchResultLoader);
        this.batchResultLoaderContext.addParameterObject(metaResultObject);
        /*if (batchResultLoader == null) {
            MappedStatement nestedQuery = configuration.getMappedStatement(nestedQueryId);
            batchResultLoader = new BatchResultLoader(configuration, executor, nestedQuery, List.class);
            batchResultLoader.setPropertyMapping(propertyMapping);
            batchResultLoaderMap.put(nestedQueryId, batchResultLoader);
        }*/

        /*String objectKey = this.getObjectKey(resultMap.getIdResultMappings(), metaResultObject);
        if (StringUtils.isNotBlank(objectKey)) {
            batchResultLoader.addParameterObject(objectKey, metaResultObject);
        }*/
        return batchResultLoader;
    }

    public static class BatchResultLoaderContext {

        private volatile boolean isLoader = false;

        private List<Object> parameterObjectList = new ArrayList();

        private Map<String, Object> resultObjectMap = new ConcurrentHashMap();

        public Map<String, List<Object>> getParameterObject() {
            Map<String, List<Object>> parameterObjectMap = new HashMap();
            parameterObjectMap.put(BatchResultLoader.NESTED_SELECT_PARAM_COLLECTION, this.parameterObjectList);
            return parameterObjectMap;
        }

        public void addParameterObject(MetaObject parameterObject) {
            this.parameterObjectList.add(parameterObject.getOriginalObject());
        }

        public BatchResultLoaderContext addResultObject(Object resultObject, List<ResultMapping> idResultMappings, ResultMapping propertyMapping) {
            if (resultObject instanceof List) {
                List<Object> rightValueList = (List<Object>) resultObject;
                for (Object rightValue : rightValueList) {
                    MetaObject rightValueMetaObject = SystemMetaObject.forObject(rightValue);
                    List<String> idValueList = new ArrayList();
                    for (ResultMapping idResultMapping : idResultMappings) {
                        Object idValue = rightValueMetaObject.getValue(idResultMapping.getProperty());
                        idValueList.add(idValue instanceof Long ? idValue.toString() : (String) idValue);
                    }
                    String objectKey = StringUtils.join(idValueList, "");
                    Object linkRightValue = rightValueMetaObject.getValue(propertyMapping.getProperty());
                    this.resultObjectMap.put(objectKey, linkRightValue);
                }
            }
            return this;
        }

        public Map<String, Object> getResultObject() {
            return this.resultObjectMap;
        }
    }

    public static class BatchResultLoader extends ResultLoader {

        public static final String NESTED_SELECT_PARAM_COLLECTION = "nested_select_param_collection";

        private ResultMapping propertyMapping;

        private ResultMap resultMap;

        private Map<String, MetaObject> rightObjectMap = new ConcurrentHashMap();

        private ResultLoader resultLoader;

        private BatchResultLoaderContext batchResultLoaderContext;

        public BatchResultLoader(Configuration configuration, Executor executor, MappedStatement mappedStatement, Object parameterObject, Class<?> targetType, BatchResultLoaderContext batchResultLoaderContext) {
            super(configuration, executor, mappedStatement, parameterObject, targetType, null, null);
            this.resultMap = mappedStatement.getResultMaps().get(0);
            this.batchResultLoaderContext = batchResultLoaderContext;
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

        public void addParameterObject(String key, MetaObject metaObject) {
            if (metaObject.getOriginalObject() == null) {
                return;
            }
            this.rightObjectMap.put(key, metaObject);
            Map<String, List<Object>> parameterObjectMap = (Map<String, List<Object>>) this.parameterObject;
            parameterObjectMap.get(NESTED_SELECT_PARAM_COLLECTION).add(metaObject.getOriginalObject());
        }

        private List<Object> getParameterObject() {
            return ((Map<String, List<Object>>) this.parameterObject).get(NESTED_SELECT_PARAM_COLLECTION);
        }

        public Map<String, MetaObject> getRightObjectMap() {
            return rightObjectMap;
        }

        @Override
        public Object loadResult() throws SQLException {
            if (this.propertyMapping.isLazy()) {
                Map<String, List<Object>> parameterObject = this.batchResultLoaderContext.getParameterObject();
                if (parameterObject == null) {
                    return null;
                }

                Map<String, Object> resultObject = this.batchResultLoaderContext.getResultObject();
                if (ObjectUtils.isEmpty(resultObject)) {
                    BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
                    CacheKey cacheKey = executor.createCacheKey(mappedStatement, this.parameterObject, RowBounds.DEFAULT, boundSql);
                    resultLoader = new ResultLoader(configuration, executor, mappedStatement, this.parameterObject, List.class, cacheKey, boundSql);
                    Object result = this.resultLoader.loadResult();
                    List<ResultMapping> idResultMappings = resultMap.getIdResultMappings();
                    resultObject = this.batchResultLoaderContext.addResultObject(result, idResultMappings, propertyMapping).getResultObject();
                }
                List<ResultMapping> idResultMappings = resultMap.getIdResultMappings();

                MetaObject parameterObjectMetaObject = SystemMetaObject.forObject(this.parameterObject);
                List<String> idValueList = new ArrayList();
                for (ResultMapping idResultMapping : idResultMappings) {
                    Object parameterObjectKey = parameterObjectMetaObject.getValue(idResultMapping.getProperty());
                    idValueList.add(parameterObjectKey instanceof Long ? parameterObjectKey.toString() : (String) parameterObjectKey);
                }
                String objectKey = StringUtils.join(idValueList, "");
                return resultObject.get(objectKey);
            }
            throw new RuntimeException("eager load not support");
        }

        public Object loadResultEager() throws SQLException {
            if (!this.propertyMapping.isLazy()) {
                Map<String, List<Object>> parameterObject = this.batchResultLoaderContext.getParameterObject();
                if (parameterObject == null) {
                    return null;
                }

                Map<String, Object> resultObject = this.batchResultLoaderContext.getResultObject();
                if (ObjectUtils.isEmpty(resultObject)) {
                    BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
                    CacheKey cacheKey = executor.createCacheKey(mappedStatement, this.parameterObject, RowBounds.DEFAULT, boundSql);
                    resultLoader = new ResultLoader(configuration, executor, mappedStatement, this.parameterObject, List.class, cacheKey, boundSql);
                    Object result = this.resultLoader.loadResult();
                    List<ResultMapping> idResultMappings = resultMap.getIdResultMappings();
                    resultObject = this.batchResultLoaderContext.addResultObject(result, idResultMappings, propertyMapping).getResultObject();
                }
                return resultObject.get("");
            }
            return null;
        }

        @Override
        public boolean wasNull() {
            return this.resultLoader.wasNull();
        }
    }
}