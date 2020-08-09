package com.lc.mybatisx.test.commons;

import com.lc.mybatisx.converter.MetaObjectHandler;
import com.lc.mybatisx.converter.MybatisxObjectWrapperFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

@org.springframework.context.annotation.Configuration
public class MybatisxConfig {

    public Configuration configuration(SqlSessionFactory sqlSessionFactory) {
        Configuration configuration = sqlSessionFactory.getConfiguration();
        configuration.setObjectWrapperFactory(new MybatisxObjectWrapperFactory(new MetaObjectHandler() {
            @Override
            public void set(MetaObject metaObject, Object object) {
                // System.out.println(object.toString());
            }
        }));

        // TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        // typeHandlerRegistry.register(String.class, JdbcType.VARCHAR, BizNStringTypeHandler.class);
        // typeHandlerRegistry.register(String.class, JdbcType.CHAR, BizNStringTypeHandler.class);
        return configuration;
    }

}
