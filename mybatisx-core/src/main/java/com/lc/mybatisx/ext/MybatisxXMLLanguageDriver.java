package com.lc.mybatisx.ext;

import com.lc.mybatisx.annotation.handler.GenerateValueChain;
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

    private GenerateValueChain generateValueChain;

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
            Object originalValue = metaObject.getValue(javaColumnName);
            Object value = this.generateValueChain.next(sqlCommandType, generateValueColumnInfo, originalValue);
            metaObject.setValue(javaColumnName, value);
        }
        return metaObject.getOriginalObject();
    }

    public void setIdGenerateValueHandler(GenerateValueChain generateValueChain) {
        this.generateValueChain = generateValueChain;
    }

}
