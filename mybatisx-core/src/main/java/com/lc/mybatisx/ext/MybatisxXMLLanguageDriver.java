package com.lc.mybatisx.ext;

import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.annotation.handler.GenerateValueHandler;
import com.lc.mybatisx.annotation.handler.IdGenerateValueHandler;
import com.lc.mybatisx.annotation.handler.JavaColumnInfo;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.EntityInfo;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/8/17 17:48
 */
public class MybatisxXMLLanguageDriver extends XMLLanguageDriver {

    private IdGenerateValueHandler<?> idGenerateValueHandler;

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        parameterObject = this.fillParameterObject(mappedStatement, parameterObject);
        return new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    }

    protected Object fillParameterObject(MappedStatement mappedStatement, Object parameterObject) {
        if (parameterObject == null) {
            return null;
        }

        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        EntityInfo entityInfo = EntityInfoContextHolder.get(parameterObject.getClass());
        boolean isFill = (SqlCommandType.INSERT == sqlCommandType || SqlCommandType.UPDATE == sqlCommandType) && entityInfo != null;
        if (!isFill) {
            return parameterObject;
        }

        Configuration configuration = mappedStatement.getConfiguration();
        MetaObject metaObject = configuration.newMetaObject(parameterObject);

        List<ColumnInfo> generateValueColumnInfoList = entityInfo.getGenerateValueColumnInfoList();
        for (ColumnInfo generateValueColumnInfo : generateValueColumnInfoList) {
            String javaColumnName = generateValueColumnInfo.getJavaColumnName();
            Class<?> javaColumnType = metaObject.getSetterType(javaColumnName);
            Object originalValue = metaObject.getValue(javaColumnName);

            Object value = this.next(sqlCommandType, generateValueColumnInfo, javaColumnType, originalValue);
            // Object castValue = javaColumnType.cast(value);
            metaObject.setValue(javaColumnName, value);
        }
        return metaObject.getOriginalObject();
    }

    private Object next(SqlCommandType sqlCommandType, ColumnInfo columnInfo, Class<?> javaColumnType, Object originalValue) {
        JavaColumnInfo javaColumnInfo = new JavaColumnInfo();
        javaColumnInfo.setType(javaColumnType);
        javaColumnInfo.setColumnName(columnInfo.getJavaColumnName());
        javaColumnInfo.setId(columnInfo.getId());
        javaColumnInfo.setLock(columnInfo.getLock());
        javaColumnInfo.setLogicDelete(columnInfo.getLogicDelete());

        GenerateValueHandler<?> generateValueHandler = columnInfo.getGenerateValueHandler();
        if (generateValueHandler != null) {
            return generateValueHandler.next(sqlCommandType, javaColumnInfo, originalValue);
        }
        Id id = columnInfo.getId();
        if (id != null) {
            return this.idGenerateValueHandler.next(sqlCommandType, javaColumnInfo, originalValue);
        }
        return originalValue;
    }

    public void setIdGenerateValueHandler(IdGenerateValueHandler<?> idGenerateValueHandler) {
        this.idGenerateValueHandler = idGenerateValueHandler;
    }

}
