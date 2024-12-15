package com.lc.mybatisx.scripting;

import com.lc.mybatisx.annotation.Entity;
import com.lc.mybatisx.annotation.handler.GenerateValueHandler;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.EntityInfo;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/8/17 17:49
 */
public class MybatisxParameterHandler extends DefaultParameterHandler {

    private Object parameterObject;

    public MybatisxParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        super(mappedStatement, parameterObject, boundSql);
        // Configuration configuration = mappedStatement.getConfiguration();
        // this.parameterObject = this.fillParameterObject(configuration, mappedStatement, parameterObject);
    }

    @Override
    public Object getParameterObject() {
        return super.getParameterObject();
    }

    @Override
    public void setParameters(PreparedStatement ps) {
        super.setParameters(ps);
    }

    private Object fillParameterObject(Configuration configuration, MappedStatement mappedStatement, Object parameterObject) {
        if (parameterObject == null) {
            return null;
        }
        MetaObject metaObject = configuration.newMetaObject(parameterObject);

        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Entity entity = parameterObject.getClass().getAnnotation(Entity.class);
        boolean isFill = (SqlCommandType.INSERT == sqlCommandType || SqlCommandType.UPDATE == sqlCommandType) && entity != null;

        EntityInfo entityInfo = EntityInfoContextHolder.get(parameterObject.getClass());
        List<ColumnInfo> generateValueColumnInfoList = entityInfo.getGenerateValueColumnInfoList();
        for (ColumnInfo generateValueColumnInfo : generateValueColumnInfoList) {
            GenerateValueHandler generateValueHandler = generateValueColumnInfo.getGenerateValueHandler();
            Object value = generateValueHandler.next();
            metaObject.setValue(generateValueColumnInfo.getJavaColumnName(), value);
        }
        return metaObject.getOriginalObject();
    }

}
