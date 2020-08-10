package com.lc.mybatisx.scripting;

import com.lc.mybatisx.session.MybatisxConfiguration;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

public final class MybatisxXMLLanguageDriver extends XMLLanguageDriver {

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        switch (mappedStatement.getSqlCommandType()) {
            case INSERT:
            case UPDATE:
                return new MybatisxParameterHandler(mappedStatement, parameterObject, boundSql, mappedStatement.getSqlCommandType());
            default:
                break;
        }

        return new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    }

}
