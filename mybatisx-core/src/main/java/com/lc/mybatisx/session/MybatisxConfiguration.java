package com.lc.mybatisx.session;

import com.lc.mybatisx.scripting.MetaObjectHandler;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import javax.persistence.Entity;

public class MybatisxConfiguration extends Configuration {

    private MetaObjectHandler metaObjectHandler;

    public MetaObjectHandler getMetaObjectHandler() {
        return metaObjectHandler;
    }

    public void setMetaObjectHandler(MetaObjectHandler metaObjectHandler) {
        this.metaObjectHandler = metaObjectHandler;
    }

    @Override
    public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        parameterObject = this.fillParameterObject(mappedStatement, parameterObject);
        return super.newParameterHandler(mappedStatement, parameterObject, boundSql);
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
            metaObjectHandler.fillParameterObject(metaObject);
            return metaObject.getOriginalObject();
        }

        return parameterObject;
    }

}
