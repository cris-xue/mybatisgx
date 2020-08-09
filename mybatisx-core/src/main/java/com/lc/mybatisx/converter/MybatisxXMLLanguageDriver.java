package com.lc.mybatisx.converter;

import com.lc.mybatisx.handler.MybatisxParameterHandler;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

public class MybatisxXMLLanguageDriver extends XMLLanguageDriver {

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        switch (mappedStatement.getSqlCommandType()) {
            case INSERT:
                break;
            case UPDATE:
                break;
            default:
                break;
        }

        return new MybatisxParameterHandler(mappedStatement, parameterObject, boundSql);
    }

}
