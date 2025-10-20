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
            if (batchResultLoader.getLazy()) {
                continue;
            }
            Map<String, List<Object>> nestedQueryParamMap = this.getNestedQueryParamMap(batchResultLoader);
            if (ObjectUtils.isNotEmpty(nestedQueryParamMap)) {
                Object nestedQuery = this.execute(nestedQueryId, nestedQueryParamMap);
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
                    BatchResultLoader batchResultLoader = this.batchResultLoaderMap.get(nestedQueryId);
                    if (batchResultLoader == null) {
                        batchResultLoader = new BatchResultLoader(propertyResultMapping.isLazy());
                        this.batchResultLoaderMap.put(nestedQueryId, batchResultLoader);
                    }
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
        Map<String, MetaObject> leftMap = batchResultLoader.getMap();
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

    private Object execute(String nestedQueryId, Map<String, List<Object>> nestedQueryParamMap) throws SQLException {
        MappedStatement nestedQuery = configuration.getMappedStatement(nestedQueryId);
        BoundSql nestedBoundSql = nestedQuery.getBoundSql(nestedQueryParamMap);
        CacheKey cacheKey = executor.createCacheKey(nestedQuery, nestedQueryParamMap, RowBounds.DEFAULT, nestedBoundSql);
        ResultLoader resultLoader = new ResultLoader(configuration, executor, nestedQuery, nestedQueryParamMap, List.class, cacheKey, nestedBoundSql);
        return resultLoader.loadResult();
    }

    @Override
    public Object getNestedQueryMappingValue(ResultSet rs, MetaObject metaResultObject, ResultMapping propertyMapping, ResultLoaderMap lazyLoader, String columnPrefix) throws SQLException {
        if (propertyMapping instanceof BatchSelectResultMapping) {
            String nestedQueryId = propertyMapping.getNestedQueryId();
            MappedStatement nestedQuery = configuration.getMappedStatement(nestedQueryId);
            ResultMap resultMap = nestedQuery.getResultMaps().get(0);

            BatchResultLoader batchResultLoader = batchResultLoaderMap.get(nestedQueryId);
            if (batchResultLoader == null) {
                batchResultLoader = new BatchResultLoader(propertyMapping, resultMap);
                batchResultLoaderMap.put(nestedQueryId, batchResultLoader);
            }

            // 对象如果获取到的id值不为空，则将当前对象添加到batchResultSetContext中
            String objectKey = this.getObjectKey(resultMap.getIdResultMappings(), metaResultObject);
            if (StringUtils.isNotBlank(objectKey)) {
                batchResultLoader.addMap(objectKey, metaResultObject);
            }

            // 把需要懒加载的数据放在第一个对象中，这样在后续遍历对象的时候就不会触发N+1问题
            if (nestedQueryId != null && propertyMapping.isLazy()) {
                final ResultLoader resultLoader = new ResultLoader(configuration, executor, nestedQuery, null, propertyMapping.getJavaType(), null, null);
                lazyLoader.addLoader(propertyMapping.getProperty(), metaResultObject, resultLoader);
                return DEFERRED;
            }
            return null;
        }
        return super.getNestedQueryMappingValue(rs, metaResultObject, propertyMapping, lazyLoader, columnPrefix);
    }

    public static class BatchResultLoader extends ResultLoader {

        private Boolean isLazy;

        private ResultMapping propertyMapping;

        private ResultMap resultMap;

        private Map<String, MetaObject> map = new ConcurrentHashMap();

        private List<Object> list = new ArrayList();

        public BatchResultLoader(Boolean isLazy) {
            super(null, null, null, null, null, null, null);
            this.isLazy = isLazy;
        }

        public BatchResultLoader(ResultMapping propertyMapping, ResultMap resultMap) {
            super(null, null, null, null, null, null, null);
            this.propertyMapping = propertyMapping;
            this.resultMap = resultMap;
        }

        public Boolean getLazy() {
            return isLazy;
        }

        public ResultMapping getPropertyMapping() {
            return propertyMapping;
        }

        public ResultMap getResultMap() {
            return resultMap;
        }

        public void setResultMap(ResultMap resultMap) {
            this.resultMap = resultMap;
        }

        public void addMap(String key, MetaObject metaObject) {
            if (metaObject.getOriginalObject() == null) {
                return;
            }
            this.map.put(key, metaObject);
            this.list.add(metaObject.getOriginalObject());
        }

        public Map<String, MetaObject> getMap() {
            return map;
        }

        public List<Object> getList() {
            return list;
        }
    }
}