package com.lc.mybatisx;

import com.lc.mybatisx.spring.MybatisxSqlSessionFactoryBean;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/7 14:21
 */
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MybatisxRegistrar extends GenericApplicationContext implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(Mybatisx.class.getName()));

        if (attrs != null) {
            registerSqlSessionFactoryBean(attrs, registry);
        }
    }

    private void registerSqlSessionFactoryBean(AnnotationAttributes attrs, BeanDefinitionRegistry registry) {
        String[] basePackages = (String[]) attrs.get("basePackages");

        AutoConfigurationPackages.
        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        constructorArgumentValues.addIndexedArgumentValue(0, basePackages);

        // 注册bean
        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(MybatisxSqlSessionFactoryBean.class);
        genericBeanDefinition.setConstructorArgumentValues(constructorArgumentValues);
        genericBeanDefinition.set
        registry.registerBeanDefinition("sqlSessionFactoryBean", genericBeanDefinition);
    }

}
