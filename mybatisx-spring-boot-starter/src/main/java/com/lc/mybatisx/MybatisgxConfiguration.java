package com.lc.mybatisx;

import com.lc.mybatisx.annotation.handler.IdGenerateValueHandler;
import com.lc.mybatisx.annotation.handler.JavaColumnInfo;
import com.lc.mybatisx.sql.MybatisxExecutorInterceptor;
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
