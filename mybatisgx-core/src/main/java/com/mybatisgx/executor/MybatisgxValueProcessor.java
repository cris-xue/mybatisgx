package com.mybatisgx.executor;

import com.mybatisgx.annotation.LogicDeleteId;
import com.mybatisgx.api.MethodCommandType;
import com.mybatisgx.context.DaoMethodManager;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.*;
import com.mybatisgx.spi.ValueProcessContext;
import com.mybatisgx.spi.ValueProcessor;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 值生成处理器
 *
 * @author：薛承城
 */
public class MybatisgxValueProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisgxValueProcessor.class);
    private static final Map<Class, AbstractFieldValueHandler> VALUE_HANDLER_MAP = new ConcurrentHashMap();

    static {
        VALUE_HANDLER_MAP.put(ColumnInfo.class, new CommonFieldValueHandler());
        VALUE_HANDLER_MAP.put(IdColumnInfo.class, new CommonFieldValueHandler());
        VALUE_HANDLER_MAP.put(LogicDeleteIdColumnInfo.class, new LogicDeleteIdFieldValueHandler());
    }

    public ValueProcessPrepareContext prepare(MappedStatement ms, Object parameterObject) {
        MethodInfo methodInfo = DaoMethodManager.getMethodInfo(ms);
        boolean isValueProcessor = this.isValueProcessor(methodInfo, parameterObject);
        return new ValueProcessPrepareContext(isValueProcessor, methodInfo);
    }

    public void process(MappedStatement ms, Object parameterObject, BoundSql boundSql) {
        MethodInfo methodInfo = DaoMethodManager.getMethodInfo(ms);
        if (!this.isValueProcessor(methodInfo, parameterObject)) {
            return;
        }
        Object unwrapParameterObject = this.unwrapParameterObject(methodInfo, parameterObject);
        for (ColumnInfo columnInfo : methodInfo.getMapperInfo().getEntityInfo().getGenerateValueColumnInfoList()) {
            AbstractFieldValueHandler fieldValueHandler = this.VALUE_HANDLER_MAP.get(columnInfo.getClass());
            fieldValueHandler.handle(methodInfo, columnInfo, unwrapParameterObject, boundSql);
        }
    }

    private boolean isValueProcessor(MethodInfo methodInfo, Object parameterObject) {
        if (methodInfo == null || parameterObject == null) {
            return false;
        }
        return methodInfo.isValueProcessor();
    }

    private Object unwrapParameterObject(MethodInfo methodInfo, Object parameterObject) {
        MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
        if (entityParamInfo != null && entityParamInfo.getWrapper()) {
            MapperMethod.ParamMap<Object> mapperMethodParamMap = (MapperMethod.ParamMap<Object>) parameterObject;
            String paramWrapperKey = methodInfo.getBatch() ? entityParamInfo.getBatchItemName() : entityParamInfo.getArgName();
            return mapperMethodParamMap.get(paramWrapperKey);
        }
        return parameterObject;
    }

    private static abstract class AbstractFieldValueHandler {

        public abstract void handle(MethodInfo methodInfo, ColumnInfo columnInfo, Object parameterObject, BoundSql boundSql);

        protected Object valueHandle(MethodCommandType commandType, ColumnInfo columnInfo, Object originalValue, Object parameterObject) {
            FieldInfo fieldInfo = columnInfo.getFieldInfo();
            ValueProcessContext context = new DefaultValueProcessContext(commandType, fieldInfo, originalValue, parameterObject);
            for (ValueProcessor valueProcessor : columnInfo.getValueProcessors()) {
                if (valueProcessor.supports(fieldInfo)) {
                    if (valueProcessor.commandTypes().contains(commandType)) {
                        Object fieldValue = valueProcessor.process(context);
                        context.setFieldValue(fieldValue);
                    }
                }
            }
            return context.getFieldValue();
        }
    }

    private static class LogicDeleteIdFieldValueHandler extends AbstractFieldValueHandler {

        @Override
        public void handle(MethodInfo methodInfo, ColumnInfo columnInfo, Object parameterObject, BoundSql boundSql) {
            MethodCommandType commandType = methodInfo.getMethodCommandType();
            EntityInfo entityInfo = methodInfo.getMapperInfo().getEntityInfo();
            LogicDeleteIdColumnInfo logicDeleteIdColumnInfo = (LogicDeleteIdColumnInfo) entityInfo.getLogicDeleteIdColumnInfo();
            if (logicDeleteIdColumnInfo != null) {
                Object value = this.valueHandle(commandType, columnInfo, null, parameterObject);
                LogicDeleteId logicDeleteId = logicDeleteIdColumnInfo.getLogicDeleteId();
                boundSql.setAdditionalParameter(logicDeleteId.value(), value);
            }
        }
    }

    private static class CommonFieldValueHandler extends AbstractFieldValueHandler {

        @Override
        public void handle(MethodInfo methodInfo, ColumnInfo columnInfo, Object parameterObject, BoundSql boundSql) {
            MethodCommandType commandType = methodInfo.getMethodCommandType();
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            if (entityParamInfo == null || !entityParamInfo.getType().equals(parameterObject.getClass())) {
                return;
            }
            Object originalValue = this.getValueByChain(parameterObject, columnInfo);
            Object value = this.valueHandle(commandType, columnInfo, originalValue, parameterObject);
            this.setValueByChain(parameterObject, columnInfo, value);
        }

        private Object getValueByChain(Object root, ColumnInfo columnInfo) {
            Object current = root;

            List<ColumnInfo> chain = columnInfo.getJavaColumnChain();
            for (int i = 0; i < chain.size(); i++) {
                if (current == null) {
                    return null;
                }
                current = chain.get(i).getLambdaAccessor().getValue(current);
            }

            return current;
        }

        private void setValueByChain(Object root, ColumnInfo columnInfo, Object value) {
            Object current = root;

            List<ColumnInfo> chain = columnInfo.getJavaColumnChain();
            int lastIndex = chain.size() - 1;

            // 处理中间链路对象
            for (int i = 0; i < lastIndex; i++) {
                ColumnInfo currentColumn = chain.get(i);
                Object next = currentColumn.getLambdaAccessor().getValue(current);

                if (next == null) {
                    ObjectFactory<?> factory = currentColumn.getLambdaAccessor().getObjectFactory();
                    if (factory == null) {
                        throw new MybatisgxException("Cannot instantiate property: " + currentColumn.getField().getName());
                    }

                    next = factory.create();
                    currentColumn.getLambdaAccessor().setValue(current, next);
                }

                current = next;
            }

            // 最终字段
            ColumnInfo targetColumn = chain.get(lastIndex);

            // 已有值则跳过
            Object existingValue = targetColumn.getLambdaAccessor().getValue(current);
            if (existingValue != null) {
                return;
            }

            targetColumn.getLambdaAccessor().setValue(current, value);
        }
    }
}
