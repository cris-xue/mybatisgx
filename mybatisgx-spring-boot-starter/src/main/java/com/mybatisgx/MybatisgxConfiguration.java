package com.mybatisgx;

import com.mybatisgx.annotation.handler.IdGenerateValueHandler;
import com.mybatisgx.annotation.handler.JavaColumnInfo;
import com.mybatisgx.sql.MybatisxExecutorInterceptor;
import org.springframework.context.annotation.Bean;

public class MybatisgxConfiguration {

    @Bean
    public MybatisxExecutorInterceptor mybatisxExecutorInterceptor() {
        return new MybatisxExecutorInterceptor(new IdGenerateValueHandler() {
            @Override
            public Object insert(JavaColumnInfo javaColumnInfo, Object originalValue) {
                return null;
            }

            @Override
            public Object update(JavaColumnInfo javaColumnInfo, Object originalValue) {
                return null;
            }
        });
    }
}
