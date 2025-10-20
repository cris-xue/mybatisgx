package com.lc.mybatisx.ext;

import com.lc.mybatisx.ext.mapping.BatchSelectResultMapping;
import com.lc.mybatisx.utils.TypeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.loader.ResultLoader;
import org.apache.ibatis.executor.loader.ResultLoaderMap;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetWrapper;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
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
            if (!batchResultLoader.getLazy()) {
                Object nestedQuery = batchResultLoader.loadResult();
                if (nestedQuery instanceof List) {
                    List<Object> rightList = (List<Object>) nestedQuery;
                    this.leftJoin(batchResultLoader, rightList);
                }
            }
        }
        return leftList;
    }

    @Override
    public Object createResultObject(ResultSetWrapper rsw, ResultMap resultMap, ResultLoaderMap lazyLoader, String columnPrefix) throws SQLException {
        Object resultObject = super.createResultObject(rsw, resultMap, lazyLoader, columnPrefix);
        List<ResultMapping> propertyResultMappings = resultMap.getPropertyResultMappings();
        for (ResultMapping propertyResultMapping : propertyResultMappings) {
            if (TypeUtils.typeEquals(propertyResultMapping, BatchSelectResultMapping.class)) {
                String nestedQueryId = propertyResultMapping.getNestedQueryId();
                if (nestedQueryId != null && propertyResultMapping.isLazy()) {
                    /*BatchResultLoader batchResultLoader = this.batchResultLoaderMap.get(nestedQueryId);
                    if (batchResultLoader == null) {
                        batchResultLoader = new BatchResultLoader();
                        this.batchResultLoaderMap.put(nestedQueryId, batchResultLoader);
                    }*/
                }
            }
        }
        return resultObject;
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

    private Map<String, List<Object>> getNestedQueryParamMap(BatchResultLoader batchResultLoader) {
        List<Object> list = batchResultLoader.getList();
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }
        Map<String, List<Object>> nestedQueryParamMap = new HashMap();
        nestedQueryParamMap.put("nested_select_collection", list);
        return nestedQueryParamMap;
    }

    @Override
    public Object getNestedQueryMappingValue(ResultSet rs, MetaObject metaResultObject, ResultMapping propertyMapping, ResultLoaderMap lazyLoader, String columnPrefix) throws SQLException {
        if (propertyMapping instanceof BatchSelectResultMapping) {
            String nestedQueryId = propertyMapping.getNestedQueryId();
            MappedStatement nestedQuery = configuration.getMappedStatement(nestedQueryId);
            ResultMap resultMap = nestedQuery.getResultMaps().get(0);

            /*BatchResultLoader batchResultLoader = batchResultLoaderMap.get(nestedQueryId);
            if (batchResultLoader == null) {
                batchResultLoader = new BatchResultLoader(propertyMapping, resultMap);
                batchResultLoaderMap.put(nestedQueryId, batchResultLoader);
            }

            // 对象如果获取到的id值不为空，则将当前对象添加到batchResultSetContext中
            String objectKey = this.getObjectKey(resultMap.getIdResultMappings(), metaResultObject);
            if (StringUtils.isNotBlank(objectKey)) {
                batchResultLoader.addParameterObject(objectKey, metaResultObject);
            }

            // 把需要懒加载的数据放在第一个对象中，这样在后续遍历对象的时候就不会触发N+1问题
            if (nestedQueryId != null && propertyMapping.isLazy()) {
                this.buildBatchResultLoader();

                final ResultLoader resultLoader = new ResultLoader(configuration, executor, nestedQuery, null, propertyMapping.getJavaType(), null, null);
                lazyLoader.addLoader(propertyMapping.getProperty(), metaResultObject, resultLoader);
                return DEFERRED;
            }*/

            ResultLoader resultLoader = this.buildBatchResultLoader(resultMap, propertyMapping, nestedQueryId, metaResultObject);
            lazyLoader.addLoader(propertyMapping.getProperty(), metaResultObject, resultLoader);
            return DEFERRED;
        }
        return super.getNestedQueryMappingValue(rs, metaResultObject, propertyMapping, lazyLoader, columnPrefix);
    }

    private ResultLoader buildBatchResultLoader(ResultMap resultMap, ResultMapping propertyMapping, String nestedQueryId, MetaObject metaResultObject) {
        BatchResultLoader batchResultLoader = batchResultLoaderMap.get(nestedQueryId);
        if (batchResultLoader == null) {
            MappedStatement nestedQuery = configuration.getMappedStatement(nestedQueryId);
            batchResultLoader = new BatchResultLoader(configuration, executor, nestedQuery, metaResultObject, List.class, null, null);
            batchResultLoader.setPropertyMapping(propertyMapping);
            batchResultLoaderMap.put(nestedQueryId, batchResultLoader);
        }

        String objectKey = this.getObjectKey(resultMap.getIdResultMappings(), metaResultObject);
        if (StringUtils.isNotBlank(objectKey)) {
            batchResultLoader.addParameterObject(objectKey, metaResultObject);
        }
        return batchResultLoader;
    }

    public static class BatchResultLoader extends ResultLoader {

        private ResultMapping propertyMapping;

        private ResultMap resultMap;

        private Map<String, MetaObject> rightObjectMap = new ConcurrentHashMap();

        public BatchResultLoader() {
            super(null, null, null, null, null, null, null);
        }

        public BatchResultLoader(Configuration configuration, Executor executor, MappedStatement mappedStatement, Object parameterObject1111, Class<?> targetType, CacheKey cacheKey, BoundSql boundSql) {
            super(configuration, executor, mappedStatement, new ConcurrentHashMap(), targetType, new BatchCacheKey(), new BatchBoundSql(configuration));
            this.resultMap = mappedStatement.getResultMaps().get(0);
            Map<String, List<Object>> parameterObjectMap = (Map<String, List<Object>>) this.parameterObject;
            parameterObjectMap.put("nested_select_collection", new ArrayList());
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
            parameterObjectMap.get("nested_select_collection").add(metaObject.getOriginalObject());
        }

        public Map<String, List<Object>> getParameterObject() {
            return (Map<String, List<Object>>) this.parameterObject;
        }

        public Map<String, MetaObject> getRightObjectMap() {
            return rightObjectMap;
        }

        public List<Object> getList() {
            return null;
        }

        @Override
        public Object loadResult() throws SQLException {
            Map<String, List<Object>> parameterObject = this.getParameterObject();
            if (ObjectUtils.isEmpty(parameterObject.get("nested_select_collection"))) {
                return null;
            }

            BatchBoundSql batchBoundSql = (BatchBoundSql) this.boundSql;
            batchBoundSql.setBoundSql(mappedStatement.getBoundSql(this.parameterObject));

            BatchCacheKey batchCacheKey = (BatchCacheKey) this.cacheKey;
            batchCacheKey.setCacheKey(executor.createCacheKey(mappedStatement, this.parameterObject, RowBounds.DEFAULT, batchBoundSql));

            return super.loadResult();
        }
    }

    private static class BatchCacheKey extends CacheKey {

        private CacheKey cacheKey;

        public CacheKey getCacheKey() {
            return cacheKey;
        }

        public void setCacheKey(CacheKey cacheKey) {
            this.cacheKey = cacheKey;
        }

        @Override
        public int getUpdateCount() {
            return cacheKey.getUpdateCount();
        }

        @Override
        public void update(Object object) {
            cacheKey.update(object);
        }

        @Override
        public void updateAll(Object[] objects) {
            cacheKey.updateAll(objects);
        }

        @Override
        public boolean equals(Object object) {
            return cacheKey.equals(object);
        }

        @Override
        public int hashCode() {
            return cacheKey.hashCode();
        }

        @Override
        public String toString() {
            return cacheKey.toString();
        }

        @Override
        public CacheKey clone() throws CloneNotSupportedException {
            return cacheKey.clone();
        }
    }

    private static class BatchBoundSql extends BoundSql {

        private BoundSql boundSql;

        public BatchBoundSql(Configuration configuration) {
            super(configuration, null, null, null);
        }

        public BoundSql getBoundSql() {
            return boundSql;
        }

        public void setBoundSql(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public String getSql() {
            return boundSql.getSql();
        }

        @Override
        public List<ParameterMapping> getParameterMappings() {
            return boundSql.getParameterMappings();
        }

        @Override
        public Object getParameterObject() {
            return boundSql.getParameterObject();
        }

        @Override
        public boolean hasAdditionalParameter(String name) {
            return boundSql.hasAdditionalParameter(name);
        }

        @Override
        public void setAdditionalParameter(String name, Object value) {
            boundSql.setAdditionalParameter(name, value);
        }

        @Override
        public Object getAdditionalParameter(String name) {
            return boundSql.getAdditionalParameter(name);
        }
    }
}