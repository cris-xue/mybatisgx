package com.lc.mybatisx.ext;

import com.lc.mybatisx.ext.mapping.BatchSelectBoundSql;
import com.lc.mybatisx.ext.mapping.BatchSelectResultMapping;
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
import java.util.*;

public class MybatisgxResultSetHandler extends MybatisDefaultResultSetHandler {

    private ResultSetHandler delegate;
    private Executor executor;
    private Configuration configuration;
    private MappedStatement mappedStatement;

    public MybatisgxResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        this.executor = executor;
        this.configuration = mappedStatement.getConfiguration();
        this.mappedStatement = mappedStatement;
    }

    public MybatisgxResultSetHandler(ResultSetHandler delegate, Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        this.delegate = delegate;
        this.executor = executor;
        this.configuration = mappedStatement.getConfiguration();
        this.mappedStatement = mappedStatement;
    }

    @Override
    public List<Object> handleResultSets(Statement stmt) throws SQLException {
        List<Object> list = super.handleResultSets(stmt);

        List<ResultMap> resultMaps = mappedStatement.getResultMaps();
        ResultMap resultMap = resultMaps.get(0);

        List<ResultMapping> propertyResultMappings = resultMap.getPropertyResultMappings();
        Map<String, ResultMapping> columnPropertyMap = new LinkedHashMap();
        Map<String, List<ResultMapping>> nestedQueryMap = new LinkedHashMap();
        for (int i = 0; i < propertyResultMappings.size(); i++) {
            ResultMapping propertyMapping = propertyResultMappings.get(i);
            if (propertyMapping.getNestedQueryId() == null) {
                if (propertyMapping.getProperty() != null) {
                    columnPropertyMap.put(propertyMapping.getColumn(), propertyMapping);
                }
            } else {
                String nestedQueryId = propertyMapping.getNestedQueryId();
                List<ResultMapping> composites = propertyMapping.getComposites();
                nestedQueryMap.put(nestedQueryId, composites);
            }
        }

        Map<String, Object> nestedQueryResultMap = new HashMap();
        for (String nestedQueryId : nestedQueryMap.keySet()) {
            List<ResultMapping> resultMappingList = nestedQueryMap.get(nestedQueryId);
            Map<String, List<Object>> nestedQueryParamMap = this.getNestedQueryParamList(nestedQueryId, list, resultMappingList, columnPropertyMap);
            Object nestedQueryList = this.execute(nestedQueryId, nestedQueryParamMap);
            nestedQueryResultMap.put(nestedQueryId, nestedQueryList);
        }
        return list;
    }

    private Map<String, List<Object>> getNestedQueryParamList(String nestedQueryId, List<Object> list, List<ResultMapping> resultMappingList, Map<String, ResultMapping> columnPropertyMap) {
        List<Map<String, Object>> nestedQueryParamList = new ArrayList();
        for (Object item : list) {
            MetaObject metaObject = configuration.newMetaObject(item);

            Map<String, Object> nestedQueryParam = new HashMap();
            for (ResultMapping nestedQueryResultMapping : resultMappingList) {
                String property = nestedQueryResultMapping.getProperty();
                String column = nestedQueryResultMapping.getColumn();

                ResultMapping columnResultMapping = columnPropertyMap.get(column);
                Object propertyValue = metaObject.getValue(columnResultMapping.getProperty());

                nestedQueryParam.put(property, propertyValue);
            }

            nestedQueryParamList.add(nestedQueryParam);
        }
        Map<String, List<Map<String, Object>>> nestedQueryParamMap = new HashMap();
        nestedQueryParamMap.put("nested_select_collection", nestedQueryParamList);

        Map<String, List<Object>> nestedQueryParamMapNew = new HashMap();
        nestedQueryParamMapNew.put("nested_select_collection", list);

        return nestedQueryParamMapNew;
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

    private BatchSelectBoundSql getBatchSelectBoundSql(MappedStatement nestedQuery, Map<String, List<Object>> nestedQueryParamMap) {
        BoundSql nestedBoundSql = nestedQuery.getBoundSql(nestedQueryParamMap);
        return new BatchSelectBoundSql(configuration, nestedBoundSql.getSql(), nestedBoundSql.getParameterMappings(), nestedBoundSql.getParameterObject());
    }

    @Override
    public Object getNestedQueryMappingValue(ResultSet rs, MetaObject metaResultObject, ResultMapping propertyMapping, ResultLoaderMap lazyLoader, String columnPrefix) throws SQLException {
        if (propertyMapping instanceof BatchSelectResultMapping) {
            BatchSelectResultMapping batchSelectResultMapping = (BatchSelectResultMapping) propertyMapping;
            String nestedQueryId = propertyMapping.getNestedQueryId();
            MappedStatement nestedQuery = configuration.getMappedStatement(nestedQueryId);
            // Class<?> nestedQueryParameterType = nestedQuery.getParameterMap().getType();
            ResultMap resultMap = nestedQuery.getResultMaps().get(0);
            Class<?> nestedQueryParameterType = resultMap.getType();
            // Object nestedQueryParameterObject = prepareParameterForNestedQuery(rs, batchSelectResultMapping, nestedQueryParameterType, columnPrefix);
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
}