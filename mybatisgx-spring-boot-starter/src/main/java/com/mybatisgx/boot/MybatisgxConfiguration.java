package com.mybatisgx.boot;

import com.mybatisgx.annotation.handler.IdGenerateValueHandler;
import com.mybatisgx.annotation.handler.JavaColumnInfo;
import com.mybatisgx.handler.MybatisgxInterceptor;
import org.springframework.context.annotation.Bean;

public class MybatisgxConfiguration {

    @Bean
    public MybatisgxInterceptor mybatisgxInterceptor() {
        return new MybatisgxInterceptor(new IdGenerateValueHandler() {
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
