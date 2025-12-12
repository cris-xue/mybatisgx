package com.mybatisgx.executor;

import com.mybatisgx.annotation.GeneratedValue;
import com.mybatisgx.api.GeneratedValueHandler;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.context.MethodInfoContextHolder;
import com.mybatisgx.model.*;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.ObjectUtils;
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

    private GeneratedValueHandler idGeneratedValueHandler;

    public MybatisgxParameterHandler(GeneratedValueHandler idGeneratedValueHandler) {
        this.idGeneratedValueHandler = idGeneratedValueHandler;
    }

    public Object fillParameterObject(MappedStatement mappedStatement, Object parameterObject) {
        if (parameterObject == null) {
            return null;
        }

        MethodInfo methodInfo = MethodInfoContextHolder.get(mappedStatement.getId());
        if (mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE && methodInfo.getSqlCommandType() == SqlCommandType.DELETE) {
            // mapper为更新操作，但是方法为删除删除，表示当前方法为逻辑删除
        }
        if (mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE && methodInfo.getSqlCommandType() == SqlCommandType.UPDATE) {
            // 更新操作
            return this.processFillParameterObject(methodInfo, parameterObject);
        }
        if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT && methodInfo.getSqlCommandType() == SqlCommandType.INSERT) {
            // 新增操作
            return this.processFillParameterObject(methodInfo, parameterObject);
        }
        return parameterObject;
    }

    private Object processFillParameterObject(MethodInfo methodInfo, Object parameterObject) {
        Object parameterObjectNew;
        if (parameterObject instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap<Object> mapperMethodParameterObject = (MapperMethod.ParamMap<Object>) parameterObject;
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            String paramWrapperKey = methodInfo.getBatch() ? entityParamInfo.getBatchItemName() : entityParamInfo.getArgName();
            parameterObjectNew = mapperMethodParameterObject.get(paramWrapperKey);
        } else {
            parameterObjectNew = parameterObject;
        }

        EntityInfo entityInfo = EntityInfoContextHolder.get(parameterObjectNew.getClass());
        if (entityInfo == null) {
            return parameterObject;
        }

        MetaObject metaObject = SystemMetaObject.forObject(parameterObjectNew);
        List<ColumnInfo> generateValueColumnInfoList = entityInfo.getGenerateValueColumnInfoList();
        for (ColumnInfo generateValueColumnInfo : generateValueColumnInfoList) {
            String javaColumnName = generateValueColumnInfo.getJavaColumnName();
            Class<?> javaColumnType = metaObject.getSetterType(javaColumnName);
            Object originalValue = metaObject.getValue(javaColumnName);

            Object value = this.processGeneratedValue(methodInfo.getSqlCommandType(), generateValueColumnInfo, originalValue);
            metaObject.setValue(javaColumnName, value);
        }
        return metaObject.getOriginalObject();
    }

    private Object processGeneratedValue(SqlCommandType sqlCommandType, ColumnInfo columnInfo, Object originalValue) {
        if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class)) {
            List<ColumnInfo> columnInfoComposites = columnInfo.getComposites();
            if (ObjectUtils.isEmpty(columnInfoComposites)) {
                GeneratedValueHandler idGeneratedValueHandler = this.getIdGeneratedValueHandler(columnInfo);
                if (idGeneratedValueHandler != null) {
                    if (sqlCommandType == SqlCommandType.INSERT) {
                        return idGeneratedValueHandler.insert(columnInfo, originalValue);
                    }
                }
            } else {
                MetaObject metaObject = SystemMetaObject.forObject(originalValue);
                for (ColumnInfo columnInfoComposite : columnInfoComposites) {
                    GeneratedValueHandler idGeneratedValueHandler = this.getIdGeneratedValueHandler(columnInfoComposite);
                    if (idGeneratedValueHandler != null) {
                        if (sqlCommandType == SqlCommandType.INSERT) {
                            String javaColumnName = columnInfoComposite.getJavaColumnName();
                            Object columnInfoCompositeOriginalValue = metaObject.getValue(javaColumnName);
                            Object value = idGeneratedValueHandler.insert(columnInfoComposite, columnInfoCompositeOriginalValue);
                            metaObject.setValue(javaColumnName, value);
                        }
                    }
                }
                return metaObject.getOriginalObject();
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

    private GeneratedValueHandler getIdGeneratedValueHandler(ColumnInfo columnInfo) {
        // 默认使用全局id生成器
        GeneratedValueHandler idGeneratedValueHandler = columnInfo.getGenerateValueHandler();
        if (idGeneratedValueHandler == null) {
            idGeneratedValueHandler = this.idGeneratedValueHandler;
        }
        return idGeneratedValueHandler;
    }
}
