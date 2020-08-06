package com.lc.mybatisx.session;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import java.lang.reflect.Field;

public class MybatisxSqlSessionTemplate extends SqlSessionTemplate {

    public static final String GENERIC_NAME_PREFIX = "param";

    public MybatisxSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    public MybatisxSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
        super(sqlSessionFactory, executorType);
    }

    public MybatisxSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType, PersistenceExceptionTranslator exceptionTranslator) {
        super(sqlSessionFactory, executorType, exceptionTranslator);
    }

    @Override
    public int update(String statement) {
        return super.update(statement);
    }

    @Override
    public int update(String statement, Object parameter) {
        return super.update(statement, parameter);
    }

    @Override
    public int delete(String statement) {
        return super.delete(statement);
    }

    @Override
    public int delete(String statement, Object parameter) {
        Configuration configuration = getConfiguration();
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        SqlSource sqlSource = mappedStatement.getSqlSource();
        try {
            Field field = DynamicSqlSource.class.getDeclaredField("rootSqlNode");
            SqlNode sqlNode = (SqlNode) field.get(sqlSource);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return super.delete(statement, parameter);
    }

    public Object convertArgsToSqlCommandParam(Object[] args) {
        return getNamedParams(args);
    }

    public Object getNamedParams(Object[] args) {
        System.out.println(args);

        return args;
        /*final int paramCount = names.size();
        if (args == null || paramCount == 0) {
            return null;
        } else if (!hasParamAnnotation && paramCount == 1) {
            return args[names.firstKey()];
        } else {
            final Map<String, Object> param = new MapperMethod.ParamMap<>();
            int i = 0;
            for (Map.Entry<Integer, String> entry : names.entrySet()) {
                param.put(entry.getValue(), args[entry.getKey()]);
                // add generic param names (param1, param2, ...)
                final String genericParamName = GENERIC_NAME_PREFIX + (i + 1);
                // ensure not to overwrite parameter named with @Param
                if (!names.containsValue(genericParamName)) {
                    param.put(genericParamName, args[entry.getKey()]);
                }
                i++;
            }
            return param;
        }*/
    }

}
