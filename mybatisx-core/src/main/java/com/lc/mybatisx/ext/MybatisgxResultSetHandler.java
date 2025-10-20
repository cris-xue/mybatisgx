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
    private Map<String, BatchResultSetContext> batchNestedQueryMap = new ConcurrentHashMap();

    public MybatisgxResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        this.executor = executor;
        this.configuration = mappedStatement.getConfiguration();
    }

    @Override
    public List<Object> handleResultSets(Statement stmt) throws SQLException {
        List<Object> leftList = super.handleResultSets(stmt);
        for (String nestedQueryId : this.batchNestedQueryMap.keySet()) {
            BatchResultSetContext batchResultSetContext = this.batchNestedQueryMap.get(nestedQueryId);
            Map<String, List<Object>> nestedQueryParamMap = this.getNestedQueryParamMap(batchResultSetContext);
            if (ObjectUtils.isNotEmpty(nestedQueryParamMap)) {
                Object nestedQuery = this.execute(nestedQueryId, nestedQueryParamMap);
                if (nestedQuery instanceof List) {
                    List<Object> rightList = (List<Object>) nestedQuery;
                    this.leftJoin(batchResultSetContext, rightList);
                }
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
            String objectKey = this.getObjectKey(idResultMappings, rightValue);

            Object leftValue = leftMap.get(objectKey);
            this.configuration.newMetaObject(leftValue).setValue(property, linkRightValue);
        }
    }

    private String getObjectKey(List<ResultMapping> idResultMappings, Object object) {
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
        List<Object> list = batchResultSetContext.getList();
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }
        Map<String, List<Object>> nestedQueryParamMap = new HashMap();
        nestedQueryParamMap.put("nested_select_collection", list);
        return nestedQueryParamMap;
    }

    private Object execute(String nestedQueryId, Map<String, List<Object>> nestedQueryParamMap) throws SQLException {
        MappedStatement nestedQuery = configuration.getMappedStatement(nestedQueryId);
        ResultMap resultMap = nestedQuery.getResultMaps().get(0);
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

            BatchResultSetContext batchResultSetContext = batchNestedQueryMap.get(nestedQueryId);
            if (batchResultSetContext == null) {
                batchResultSetContext = new BatchResultSetContext(propertyMapping, resultMap);
                batchNestedQueryMap.put(nestedQueryId, batchResultSetContext);
            }

            // 对象如果获取到的id值不为空，则将当前对象添加到batchResultSetContext中
            String objectKey = this.getObjectKey(resultMap.getIdResultMappings(), metaResultObject.getOriginalObject());
            if (StringUtils.isNotBlank(objectKey)) {
                batchResultSetContext.addMap(objectKey, metaResultObject.getOriginalObject());
            }
            return null;
        }
        return super.getNestedQueryMappingValue(rs, metaResultObject, propertyMapping, lazyLoader, columnPrefix);
    }

    public static class BatchResultSetContext {

        private ResultMapping propertyMapping;

        private ResultMap resultMap;

        private Map<String, Object> map = new ConcurrentHashMap();

        private List<Object> list = new ArrayList();

        public BatchResultSetContext(ResultMapping propertyMapping, ResultMap resultMap) {
            this.propertyMapping = propertyMapping;
            this.resultMap = resultMap;
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

        public Map<String, Object> getMap() {
            return map;
        }

        public void setMap(Map<String, Object> map) {
            this.map = map;
        }

        public void addMap(String key, Object object) {
            if (object == null) {
                return;
            }
            this.map.put(key, object);
            this.list.add(object);
        }

        public List<Object> getList() {
            return list;
        }
    }
}