package com.lc.mybatisx.ext;

import com.lc.mybatisx.annotation.Entity;
import com.lc.mybatisx.scripting.MetaObjectHandler;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

public class MybatisxConfiguration extends Configuration {

    private MetaObjectHandler metaObjectHandler;

    public MetaObjectHandler getMetaObjectHandler() {
        return metaObjectHandler;
    }

    public void setMetaObjectHandler(MetaObjectHandler metaObjectHandler) {
        this.metaObjectHandler = metaObjectHandler;
    }

    @Override
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        parameterObject = this.fillParameterObject(mappedStatement, parameterObject);
        return super.newStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
    }

    private Object fillParameterObject(MappedStatement mappedStatement, Object parameterObject) {
        if (parameterObject == null) {
            return null;
        }

        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Entity entity = parameterObject.getClass().getAnnotation(Entity.class);
        boolean isFill = (SqlCommandType.INSERT == sqlCommandType || SqlCommandType.UPDATE == sqlCommandType) && entity != null;

        if (isFill) {
            MetaObject metaObject = this.newMetaObject(parameterObject);
            metaObjectHandler.fillParameterObject(sqlCommandType, metaObject);
            return metaObject.getOriginalObject();
        }

        return parameterObject;
    }

}
