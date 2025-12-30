package com.mybatisgx.executor;

import com.mybatisgx.annotation.LogicDeleteId;
import com.mybatisgx.api.ValueProcessContext;
import com.mybatisgx.api.ValueProcessPhase;
import com.mybatisgx.api.ValueProcessor;
import com.mybatisgx.context.DaoMethodManager;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.context.MethodInfoContextHolder;
import com.mybatisgx.model.*;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 值处理
 * @author 薛承城
 * @date 2025/12/14 19:56
 */
public class MybatisgxValueProcessor {

    private static final Map<Class, AbstractFieldValueHandler> VALUE_HANDLER_MAP = new ConcurrentHashMap();

    static {
        VALUE_HANDLER_MAP.put(ColumnInfo.class, new CommonFieldValueHandler());
        VALUE_HANDLER_MAP.put(IdColumnInfo.class, new CommonFieldValueHandler());
        VALUE_HANDLER_MAP.put(LogicDeleteIdColumnInfo.class, new LogicDeleteIdFieldValueHandler());
    }

    public Object process(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        if (parameterObject == null) {
            return null;
        }
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if (sqlCommandType == SqlCommandType.SELECT || sqlCommandType == SqlCommandType.DELETE) {
            return parameterObject;
        }
        MethodInfo methodInfo = MethodInfoContextHolder.get(mappedStatement.getId());
        if (methodInfo == null) {
            return parameterObject;
        }

        Object useParameterObject = this.getParameterObject(methodInfo, parameterObject);
        ValueProcessPhase phase = this.getValueProcessPhase(mappedStatement, methodInfo);
        for (ColumnInfo columnInfo : methodInfo.getEntityInfo().getGenerateValueColumnInfoList()) {
            AbstractFieldValueHandler fieldValueHandler = this.VALUE_HANDLER_MAP.get(columnInfo.getClass());
            fieldValueHandler.handle(methodInfo, phase, columnInfo, useParameterObject, boundSql);
        }
        return useParameterObject;
    }

    private ValueProcessPhase getValueProcessPhase(MappedStatement mappedStatement, MethodInfo methodInfo) {
        ValueProcessPhase valueProcessPhase = null;
        if (mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE && methodInfo.getSqlCommandType() == SqlCommandType.DELETE) {
            // mapper为更新操作，但是方法为删除删除，表示当前方法为逻辑删除
            valueProcessPhase = ValueProcessPhase.LOGIC_DELETE;
        }
        if (mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE && methodInfo.getSqlCommandType() == SqlCommandType.UPDATE) {
            valueProcessPhase = ValueProcessPhase.UPDATE;
        }
        if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT && methodInfo.getSqlCommandType() == SqlCommandType.INSERT) {
            valueProcessPhase = ValueProcessPhase.INSERT;
        }
        return valueProcessPhase;
    }

    private Object getParameterObject(MethodInfo methodInfo, Object parameterObject) {
        MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
        if (parameterObject instanceof MapperMethod.ParamMap && entityParamInfo != null) {
            MapperMethod.ParamMap<Object> mapperMethodParameterObject = (MapperMethod.ParamMap<Object>) parameterObject;
            String paramWrapperKey = methodInfo.getBatch() ? entityParamInfo.getBatchItemName() : entityParamInfo.getArgName();
            return mapperMethodParameterObject.get(paramWrapperKey);
        }
        return parameterObject;
    }

    private static abstract class AbstractFieldValueHandler {

        public abstract void handle(MethodInfo methodInfo, ValueProcessPhase phase, ColumnInfo columnInfo, Object parameterObject, BoundSql boundSql);

        protected Object valueHandle(ValueProcessPhase phase, ColumnInfo columnInfo, Object originalValue, MetaObject entityMetaObject) {
            ValueProcessContext context = new DefaultValueProcessContext(phase, columnInfo, originalValue, entityMetaObject);
            List<ValueProcessor> valueProcessors = DaoMethodManager.get(columnInfo.getGenerateValue().value());
            for (ValueProcessor valueProcessor : valueProcessors) {
                if (valueProcessor.supports(columnInfo)) {
                    if (valueProcessor.phases().contains(phase)) {
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
        public void handle(MethodInfo methodInfo, ValueProcessPhase phase, ColumnInfo columnInfo, Object parameterObject, BoundSql boundSql) {
            EntityInfo entityInfo = methodInfo.getEntityInfo();
            LogicDeleteIdColumnInfo logicDeleteIdColumnInfo = (LogicDeleteIdColumnInfo) entityInfo.getLogicDeleteIdColumnInfo();
            if (logicDeleteIdColumnInfo != null) {
                Object value = this.valueHandle(phase, columnInfo, null, null);
                LogicDeleteId logicDeleteId = logicDeleteIdColumnInfo.getLogicDeleteId();
                boundSql.setAdditionalParameter(logicDeleteId.value(), value);
            }
        }
    }

    private static class CommonFieldValueHandler extends AbstractFieldValueHandler {

        @Override
        public void handle(MethodInfo methodInfo, ValueProcessPhase phase, ColumnInfo columnInfo, Object parameterObject, BoundSql boundSql) {
            EntityInfo parameterEntityInfo = EntityInfoContextHolder.get(parameterObject.getClass());
            if (parameterEntityInfo == null) {
                return;
            }
            MetaObject metaObject = SystemMetaObject.forObject(parameterObject);
            String javaColumnNamePath = columnInfo.getJavaColumnNamePath();
            Object fieldValue = metaObject.getValue(javaColumnNamePath);
            Object value = this.valueHandle(phase, columnInfo, fieldValue, metaObject);
            metaObject.setValue(javaColumnNamePath, value);
        }
    }
}
