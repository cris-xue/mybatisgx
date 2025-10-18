package com.lc.mybatisx.ext;

import com.lc.mybatisx.ext.mapping.BatchSelectResultMapping;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.loader.ResultLoader;
import org.apache.ibatis.executor.loader.ResultLoaderMap;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MybatisgxResultSetHandler extends MybatisDefaultResultSetHandler {

    private ResultSetHandler delegate;
    private Executor executor;
    private Configuration configuration;
    private Map<String, Object> batchNestedQueryMap = new ConcurrentHashMap();

    public MybatisgxResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        this.executor = executor;
        this.configuration = mappedStatement.getConfiguration();
    }

    public MybatisgxResultSetHandler(ResultSetHandler delegate, Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        this.delegate = delegate;
        this.executor = executor;
        this.configuration = mappedStatement.getConfiguration();
    }

    @Override
    public List<Object> handleResultSets(Statement stmt) throws SQLException {
        List<Object> leftList = super.handleResultSets(stmt);
        for (String nestedQueryId : this.batchNestedQueryMap.keySet()) {
            BatchResultSetContext batchResultSetContext = (BatchResultSetContext) this.batchNestedQueryMap.get(nestedQueryId);
            Map<String, List<Object>> nestedQueryParamMap = this.getNestedQueryParamMap(batchResultSetContext);
            Object nestedQuery = this.execute(nestedQueryId, nestedQueryParamMap);
            if (nestedQuery instanceof List) {
                List<Object> rightList = (List<Object>) nestedQuery;
                this.leftJoin(batchResultSetContext, rightList);
            }
        }
        return leftList;
    }

    private void leftJoin(BatchResultSetContext batchResultSetContext, List<Object> rightValueList) {
        // 如果不存在连接值，不需要处理关联数据
        if (ObjectUtils.isEmpty(rightValueList)) {
            return;
        }

        List<ResultMapping> idResultMappings = batchResultSetContext.getResultMap().getIdResultMappings();
        Map<String, Object> leftMap = batchResultSetContext.getMap();
        BatchSelectResultMapping batchSelectResultMapping = (BatchSelectResultMapping) batchResultSetContext.getPropertyMapping();
        String property = batchSelectResultMapping.getProperty();

        // 处理主键和对象的映射关系
        for (Object rightValue : rightValueList) {
            Object linkRightValue = this.configuration.newMetaObject(rightValue).getValue(property);
            String objectId = this.getObjectId(idResultMappings, rightValue);

            Object leftValue = leftMap.get(objectId);
            this.configuration.newMetaObject(leftValue).setValue(property, linkRightValue);
        }
    }

    private String getObjectId(List<ResultMapping> idResultMappings, Object object) {
        if (object == null) {
            return "";
        }
        List<String> idValueList = new ArrayList<>();
        for (ResultMapping idResultMapping : idResultMappings) {
            Object idValue = this.configuration.newMetaObject(object).getValue(idResultMapping.getProperty());
            idValueList.add(idValue instanceof Long ? idValue.toString() : (String) idValue);
        }
        return StringUtils.join(idValueList, "");
    }

    private Map<String, List<Object>> getNestedQueryParamMap(BatchResultSetContext batchResultSetContext) {
        Map<String, List<Object>> nestedQueryParamMap = new HashMap();
        nestedQueryParamMap.put("nested_select_collection", batchResultSetContext.getList());
        return nestedQueryParamMap;
    }

    private Object execute(String nestedQueryId, Map<String, List<Object>> nestedQueryParamMap) throws SQLException {
        MappedStatement nestedQuery = configuration.getMappedStatement(nestedQueryId);
        ResultMap resultMap = nestedQuery.getResultMaps().get(0);
        Class<?> targetType = resultMap.getType();
        BoundSql nestedBoundSql = nestedQuery.getBoundSql(nestedQueryParamMap);
        CacheKey cacheKey = executor.createCacheKey(nestedQuery, nestedQueryParamMap, RowBounds.DEFAULT, nestedBoundSql);
        ResultLoader resultLoader = new ResultLoader(configuration, executor, nestedQuery, nestedQueryParamMap, List.class, cacheKey, nestedBoundSql);
        return resultLoader.loadResult();
    }

    @Override
    public Object getNestedQueryMappingValue(ResultSet rs, MetaObject metaResultObject, ResultMapping propertyMapping, ResultLoaderMap lazyLoader, String columnPrefix) throws SQLException {
        if (propertyMapping instanceof BatchSelectResultMapping) {
            BatchSelectResultMapping batchSelectResultMapping = (BatchSelectResultMapping) propertyMapping;
            String nestedQueryId = propertyMapping.getNestedQueryId();
            MappedStatement nestedQuery = configuration.getMappedStatement(propertyMapping.getNestedQueryId());
            ResultMap resultMap = nestedQuery.getResultMaps().get(0);

            Object nestedQueryMetaObject = this.configuration.getObjectFactory().create(batchSelectResultMapping.getJavaType());
            metaResultObject.setValue(batchSelectResultMapping.getProperty(), nestedQueryMetaObject);

            String objectId = this.getObjectId(resultMap.getIdResultMappings(), metaResultObject.getOriginalObject());
            BatchResultSetContext batchResultSetContext = new BatchResultSetContext();
            batchResultSetContext.setPropertyMapping(propertyMapping);
            batchResultSetContext.setResultMap(resultMap);
            batchResultSetContext.addMap(objectId, metaResultObject.getOriginalObject());

            batchNestedQueryMap.put(nestedQueryId, batchResultSetContext);
            return null;
        }
        return super.getNestedQueryMappingValue(rs, metaResultObject, propertyMapping, lazyLoader, columnPrefix);
    }

    @Override
    public <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException {
        Cursor<E> cursor = this.delegate.handleCursorResultSets(stmt);
        return cursor;
    }

    @Override
    public void handleOutputParameters(CallableStatement cs) throws SQLException {
        this.delegate.handleOutputParameters(cs);
    }

    public static class BatchResultSetContext {

        private String nestedQueryId;

        private ResultMapping propertyMapping;

        private ResultMap resultMap;

        private Map<String, Object> map = new ConcurrentHashMap();

        private List<Object> list = new ArrayList();

        public String getNestedQueryId() {
            return nestedQueryId;
        }

        public void setNestedQueryId(String nestedQueryId) {
            this.nestedQueryId = nestedQueryId;
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

        public Map<String, Object> getMap() {
            return map;
        }

        public void setMap(Map<String, Object> map) {
            this.map = map;
        }

        public void addMap(String key, Object object) {
            this.map.put(key, object);
            this.list.add(object);
        }

        public List<Object> getList() {
            return list;
        }
    }
}