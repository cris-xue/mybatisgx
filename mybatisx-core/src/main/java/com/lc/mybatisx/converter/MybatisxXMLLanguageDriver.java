package com.lc.mybatisx.converter;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

public class MybatisxXMLLanguageDriver extends XMLLanguageDriver {

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        switch (mappedStatement.getSqlCommandType()) {
            case INSERT:
                System.out.println("插入");
                break;
            case UPDATE:
                System.out.println("更新");
                break;
            default:
                break;
        }

        return new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    }

}
