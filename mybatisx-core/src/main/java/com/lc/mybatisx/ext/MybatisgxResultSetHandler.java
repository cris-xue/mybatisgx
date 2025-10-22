package com.lc.mybatisx.ext;

import com.lc.mybatisx.ext.mapping.BatchSelectResultMapping;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.loader.ResultLoader;
import org.apache.ibatis.executor.loader.ResultLoaderMap;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
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
    private Map<String, BatchResultLoader.BatchResultLoaderContext> batchResultLoaderContextMap = new ConcurrentHashMap();
    private Map<String, List<BatchResultLoader>> batchResultLoaderMap = new ConcurrentHashMap();

    public MybatisgxResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        this.executor = executor;
        this.configuration = mappedStatement.getConfiguration();
    }

    @Override
    public List<Object> handleResultSets(Statement stmt) throws SQLException {
        List<Object> leftList = super.handleResultSets(stmt);
        for (String nestedQueryId : this.batchResultLoaderMap.keySet()) {
            List<BatchResultLoader> batchResultLoaderList = this.batchResultLoaderMap.get(nestedQueryId);
            for (BatchResultLoader batchResultLoader : batchResultLoaderList) {
                if (!batchResultLoader.getLazy()) {
                    MetaObject parameterObjectMetaObject = batchResultLoader.getParameterObjectMetaObject();
                    Object linkRightValue = batchResultLoader.loadResult();
                    this.linkObjects(parameterObjectMetaObject, batchResultLoader.getPropertyMapping(), linkRightValue);
                }
            }
        }
        return leftList;
    }

    @Override
    public Object getNestedQueryMappingValue(ResultSet rs, MetaObject metaResultObject, ResultMapping propertyMapping, ResultLoaderMap lazyLoader, String columnPrefix) throws SQLException {
        if (propertyMapping instanceof BatchSelectResultMapping) {
            String nestedQueryId = propertyMapping.getNestedQueryId();
            MappedStatement nestedQuery = configuration.getMappedStatement(nestedQueryId);
            List<ResultMapping> idResultMappings = nestedQuery.getResultMaps().get(0).getIdResultMappings();
            String objectKey = LinkObjectHelper.getObjectKey(idResultMappings, metaResultObject);
            if (StringUtils.isNotBlank(objectKey)) {
                ResultLoader resultLoader = this.buildBatchResultLoader(propertyMapping, metaResultObject, nestedQueryId, nestedQuery, idResultMappings);
                lazyLoader.addLoader(propertyMapping.getProperty(), metaResultObject, resultLoader);
            }
            return DEFERRED;
        }
        return super.getNestedQueryMappingValue(rs, metaResultObject, propertyMapping, lazyLoader, columnPrefix);
    }

    private ResultLoader buildBatchResultLoader(ResultMapping propertyMapping, MetaObject metaResultObject, String nestedQueryId, MappedStatement nestedQuery, List<ResultMapping> idResultMappings) {
        BatchResultLoader.BatchResultLoaderContext batchResultLoaderContext = batchResultLoaderContextMap.get(nestedQueryId);
        if (batchResultLoaderContext == null) {
            batchResultLoaderContext = new BatchResultLoader.BatchResultLoaderContext();
            this.batchResultLoaderContextMap.put(nestedQueryId, batchResultLoaderContext);
        }

        BatchResultLoader batchResultLoader = new BatchResultLoader(configuration, executor, nestedQuery, metaResultObject, List.class, idResultMappings, propertyMapping, batchResultLoaderContext);
        List<BatchResultLoader> batchResultLoaderList = this.batchResultLoaderMap.get(nestedQueryId);
        if (ObjectUtils.isEmpty(batchResultLoaderList)) {
            batchResultLoaderList = new ArrayList();
            this.batchResultLoaderMap.put(nestedQueryId, batchResultLoaderList);
        }
        batchResultLoaderList.add(batchResultLoader);
        return batchResultLoader;
    }
}