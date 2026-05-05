package com.mybatisgx.executor;

import com.mybatisgx.annotation.LogicDeleteId;
import com.mybatisgx.api.MethodCommandType;
import com.mybatisgx.context.DaoMethodManager;
import com.mybatisgx.model.*;
import com.mybatisgx.spi.ValueProcessContext;
import com.mybatisgx.spi.ValueProcessor;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 值生成处理器
 *
 * @author：薛承城
 */
public class MybatisgxValueProcessor {

    private static final Map<Class, AbstractFieldValueHandler> VALUE_HANDLER_MAP = new ConcurrentHashMap();

    static {
        VALUE_HANDLER_MAP.put(ColumnInfo.class, new CommonFieldValueHandler());
        VALUE_HANDLER_MAP.put(IdColumnInfo.class, new CommonFieldValueHandler());
        VALUE_HANDLER_MAP.put(LogicDeleteIdColumnInfo.class, new LogicDeleteIdFieldValueHandler());
    }

    public ValueProcessPrepareContext prepare(MappedStatement ms, Object parameterObject) {
        MethodInfo methodInfo = DaoMethodManager.getMethodInfo(ms);
        boolean isProcess = this.isProcess(methodInfo, parameterObject);
        return new ValueProcessPrepareContext(isProcess, methodInfo);
    }

    public Object process(ValueProcessPrepareContext context, Object parameterObject, BoundSql boundSql) {
        MethodInfo methodInfo = context.getMethodInfo();
        Object unwrapParameterObject = this.unwrapParameterObject(methodInfo, parameterObject);
        context.setParameterObject(unwrapParameterObject);

        for (ColumnInfo columnInfo : methodInfo.getMapperInfo().getEntityInfo().getGenerateValueColumnInfoList()) {
            AbstractFieldValueHandler fieldValueHandler = this.VALUE_HANDLER_MAP.get(columnInfo.getClass());
            fieldValueHandler.handle(context, columnInfo, unwrapParameterObject, boundSql);
        }
        return unwrapParameterObject;
    }

    private boolean isProcess(MethodInfo methodInfo, Object parameterObject) {
        if (parameterObject == null || methodInfo == null) {
            return false;
        }
        MethodCommandType methodCommandType = methodInfo.getMethodCommandType();
        if (methodCommandType == MethodCommandType.SELECT || methodCommandType == MethodCommandType.DELETE) {
            return false;
        }
        // 逻辑删除可能没有实体参数，但是新增和修改必须有实体参数
        if (methodCommandType != MethodCommandType.LOGIC_DELETE) {
            if (methodInfo.getEntityParamInfo() == null) {
                return false;
            }
        }
        return true;
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

        public abstract void handle(ValueProcessPrepareContext context, ColumnInfo columnInfo, Object parameterObject, BoundSql boundSql);

        protected Object valueHandle(MethodCommandType commandType, ColumnInfo columnInfo, Object originalValue, MetaObject entityMetaObject) {
            FieldInfo fieldInfo = new FieldInfo(columnInfo);
            ValueProcessContext context = new DefaultValueProcessContext(commandType, fieldInfo, originalValue, entityMetaObject);
            List<ValueProcessor> valueProcessors = DaoMethodManager.get(columnInfo.getGenerateValue().value());
            for (ValueProcessor valueProcessor : valueProcessors) {
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
        public void handle(ValueProcessPrepareContext context, ColumnInfo columnInfo, Object parameterObject, BoundSql boundSql) {
            MethodInfo methodInfo = context.getMethodInfo();
            MethodCommandType commandType = context.getCommandType();

            EntityInfo entityInfo = methodInfo.getMapperInfo().getEntityInfo();
            LogicDeleteIdColumnInfo logicDeleteIdColumnInfo = (LogicDeleteIdColumnInfo) entityInfo.getLogicDeleteIdColumnInfo();
            if (logicDeleteIdColumnInfo != null) {
                Object value = this.valueHandle(commandType, columnInfo, null, null);
                LogicDeleteId logicDeleteId = logicDeleteIdColumnInfo.getLogicDeleteId();
                boundSql.setAdditionalParameter(logicDeleteId.value(), value);
            }
        }
    }

    private static class CommonFieldValueHandler extends AbstractFieldValueHandler {

        @Override
        public void handle(ValueProcessPrepareContext context, ColumnInfo columnInfo, Object parameterObject, BoundSql boundSql) {
            MethodInfo methodInfo = context.getMethodInfo();
            MethodCommandType commandType = context.getCommandType();
            MetaObject metaObject = context.getMetaObject();

            Class<?> entityClass = methodInfo.getMapperInfo().getEntityClass();
            if (!entityClass.isAssignableFrom(parameterObject.getClass())) {
                return;
            }

            String javaColumnNamePath = columnInfo.getJavaColumnNamePath();
            Object fieldValue = metaObject.getValue(javaColumnNamePath);
            Object value = this.valueHandle(commandType, columnInfo, fieldValue, metaObject);
            metaObject.setValue(javaColumnNamePath, value);
        }
    }
}
