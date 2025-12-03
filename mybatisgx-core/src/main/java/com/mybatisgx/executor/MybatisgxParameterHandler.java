package com.mybatisgx.executor;

import com.mybatisgx.annotation.GeneratedValue;
import com.mybatisgx.api.GeneratedValueHandler;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.context.MethodInfoContextHolder;
import com.mybatisgx.model.*;
import com.mybatisgx.utils.TypeUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/8/17 17:49
 */
public class MybatisgxParameterHandler {

    private GeneratedValueHandler<?> idGenerateValueHandler;

    public MybatisgxParameterHandler(GeneratedValueHandler<?> idGenerateValueHandler) {
        this.idGenerateValueHandler = idGenerateValueHandler;
    }

    public Object fillParameterObject(MappedStatement mappedStatement, Object parameterObject) {
        if (parameterObject == null) {
            return null;
        }

        MapperMethod.ParamMap<Object> mapperMethodParameterObject = (MapperMethod.ParamMap<Object>) parameterObject;
        MethodInfo methodInfo = MethodInfoContextHolder.get(mappedStatement.getId());
        MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
        String paramWrapperKey = methodInfo.getBatch() ? entityParamInfo.getBatchItemName() : entityParamInfo.getArgName();
        Object parameterObjectNew = mapperMethodParameterObject.get(paramWrapperKey);

        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        EntityInfo entityInfo = EntityInfoContextHolder.get(parameterObjectNew.getClass());
        boolean isFill = (SqlCommandType.INSERT == sqlCommandType || SqlCommandType.UPDATE == sqlCommandType) && entityInfo != null;
        if (!isFill) {
            return parameterObject;
        }

        MetaObject metaObject = SystemMetaObject.forObject(parameterObjectNew);
        List<ColumnInfo> generateValueColumnInfoList = entityInfo.getGenerateValueColumnInfoList();
        for (ColumnInfo generateValueColumnInfo : generateValueColumnInfoList) {
            String javaColumnName = generateValueColumnInfo.getJavaColumnName();
            Class<?> javaColumnType = metaObject.getSetterType(javaColumnName);
            Object originalValue = metaObject.getValue(javaColumnName);

            Object value = this.next(sqlCommandType, generateValueColumnInfo, javaColumnType, originalValue);
            metaObject.setValue(javaColumnName, value);
        }
        return metaObject.getOriginalObject();
    }

    private Object next(SqlCommandType sqlCommandType, ColumnInfo columnInfo, Class<?> javaColumnType, Object originalValue) {
        if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class)) {
            GeneratedValue generatedValue = columnInfo.getGenerateValue();
            if (generatedValue != null) {
                GeneratedValueHandler generatedValueHandler = columnInfo.getGenerateValueHandler();
                if (generatedValue.insert() && sqlCommandType == SqlCommandType.INSERT) {
                    return generatedValueHandler.insert(columnInfo, originalValue);
                }
            }
        }
        if (TypeUtils.typeEquals(columnInfo, ColumnInfo.class)) {
            GeneratedValue generatedValue = columnInfo.getGenerateValue();
            if (generatedValue != null) {
                GeneratedValueHandler generatedValueHandler = columnInfo.getGenerateValueHandler();
                if (generatedValue.insert() && sqlCommandType == SqlCommandType.INSERT) {
                    return generatedValueHandler.insert(columnInfo, originalValue);
                }
                if (generatedValue.update() && sqlCommandType == SqlCommandType.UPDATE) {
                    return generatedValueHandler.update(columnInfo, originalValue);
                }
            }
        }
        return originalValue;
    }
}
