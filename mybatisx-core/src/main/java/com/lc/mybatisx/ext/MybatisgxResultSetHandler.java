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
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
        BatchResultLoader batchResultLoader = batchResultLoaderMap.get(nestedQueryId);
        if (batchResultLoader == null) {
            MappedStatement nestedQuery = configuration.getMappedStatement(nestedQueryId);
            batchResultLoader = new BatchResultLoader(configuration, executor, nestedQuery, List.class);
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

        private ResultLoader resultLoader;

        public BatchResultLoader(Configuration configuration, Executor executor, MappedStatement mappedStatement, Class<?> targetType) {
            super(configuration, executor, mappedStatement, new ConcurrentHashMap(), targetType, null, null);
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

        private List<Object> getParameterObject() {
            return ((Map<String, List<Object>>) this.parameterObject).get("nested_select_collection");
        }

        public Map<String, MetaObject> getRightObjectMap() {
            return rightObjectMap;
        }

        @Override
        public Object loadResult() throws SQLException {
            if (ObjectUtils.isEmpty(this.getParameterObject())) {
                return null;
            }

            BoundSql boundSql = mappedStatement.getBoundSql(this.parameterObject);
            CacheKey cacheKey = executor.createCacheKey(mappedStatement, this.parameterObject, RowBounds.DEFAULT, boundSql);
            resultLoader = new ResultLoader(configuration, executor, mappedStatement, this.parameterObject, propertyMapping.getJavaType(), cacheKey, boundSql);
            return this.resultLoader.loadResult();
        }

        @Override
        public boolean wasNull() {
            return this.resultLoader.wasNull();
        }
    }
}