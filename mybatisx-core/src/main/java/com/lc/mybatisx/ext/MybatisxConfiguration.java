package com.lc.mybatisx.ext;

import com.lc.mybatisx.annotation.GenerateValue;
import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.annotation.TenantId;
import com.lc.mybatisx.annotation.handler.GenerateValueHandler;
import com.lc.mybatisx.annotation.handler.IdGenerateValueHandler;
import com.lc.mybatisx.annotation.handler.JavaColumnInfo;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.EntityInfo;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public class MybatisxConfiguration extends Configuration {

    private IdGenerateValueHandler<?> idGenerateValueHandler;

    public void setIdGenerateValueHandler(IdGenerateValueHandler<?> idGenerateValueHandler) {
        this.idGenerateValueHandler = idGenerateValueHandler;
    }

    @Override
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        parameterObject = this.fillParameterObject(mappedStatement, parameterObject);
        return super.newStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
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

        MetaObject metaObject = super.newMetaObject(parameterObject);
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
        JavaColumnInfo javaColumnInfo = new JavaColumnInfo();
        javaColumnInfo.setType(javaColumnType);
        javaColumnInfo.setColumnName(columnInfo.getJavaColumnName());
        javaColumnInfo.setId(columnInfo.getId());
        javaColumnInfo.setLock(columnInfo.getLock());
        javaColumnInfo.setLogicDelete(columnInfo.getLogicDelete());
        javaColumnInfo.setGenerateValue(columnInfo.getGenerateValue());

        GenerateValue generateValue = columnInfo.getGenerateValue();
        if (generateValue != null) {
            GenerateValueHandler generateValueHandler = columnInfo.getGenerateValueHandler();
            Boolean insert = generateValue.insert();
            if (insert && sqlCommandType == SqlCommandType.INSERT) {
                return generateValueHandler.insert(javaColumnInfo, originalValue);
            }
            Boolean update = generateValue.update();
            if (update && sqlCommandType == SqlCommandType.UPDATE) {
                return generateValueHandler.update(javaColumnInfo, originalValue);
            }
        }
        Id id = columnInfo.getId();
        if (id != null && sqlCommandType == SqlCommandType.INSERT) {
            return this.idGenerateValueHandler.insert(javaColumnInfo, originalValue);
        }
        TenantId tenantId = columnInfo.getTenantId();
        if (tenantId != null) {
            return this.idGenerateValueHandler.insert(javaColumnInfo, originalValue);
        }
        return originalValue;
    }

}
