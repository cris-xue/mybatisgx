package com.lc.mybatisx;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.*;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Properties;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/5 14:21
 */
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MybatisxRegistrar extends GenericApplicationContext implements EnvironmentAware, ImportBeanDefinitionRegistrar {

    private MutablePropertySources mutablePropertySources;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(Mybatisx.class.getName()));

        if (attrs != null) {
            registerSqlSessionFactoryBean(attrs);
        }
    }

    private void registerSqlSessionFactoryBean(AnnotationAttributes attrs) {
        String[] basePackages = (String[]) attrs.get("basePackages");

        Properties properties = new Properties();
        properties.put("mybatis.daoPackages", basePackages);
        PropertySource<?> propertySource = new PropertiesPropertySource("mybatisxProperties", properties);
        mutablePropertySources.addLast(propertySource);
    }

    @Override
    public void setEnvironment(Environment environment) {
        if (environment instanceof StandardEnvironment) {
            StandardEnvironment sse = (StandardEnvironment) environment;
            this.mutablePropertySources = sse.getPropertySources();
        }

        // 抛出异常
    }
}
