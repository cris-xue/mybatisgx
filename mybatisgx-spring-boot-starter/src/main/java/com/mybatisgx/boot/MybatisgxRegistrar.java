package com.mybatisgx.boot;

import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/1/8 18:06
 */
public class MybatisgxRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(MybatisgxScan.class.getName()));

        if (annotationAttributes != null) {
            String[] entityBasePackages = (String[]) annotationAttributes.get("entityBasePackages");
            String[] daoBasePackages = (String[]) annotationAttributes.get("daoBasePackages");

            ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
            constructorArgumentValues.addIndexedArgumentValue(0, entityBasePackages);
            constructorArgumentValues.addIndexedArgumentValue(1, daoBasePackages);
            GenericBeanDefinition sqlSessionFactoryBeanPostProcessorBeanDefinition = new GenericBeanDefinition();
            sqlSessionFactoryBeanPostProcessorBeanDefinition.setBeanClass(SqlSessionFactoryBeanPostProcessor.class);
            sqlSessionFactoryBeanPostProcessorBeanDefinition.setConstructorArgumentValues(constructorArgumentValues);
            registry.registerBeanDefinition("sqlSessionFactoryBeanPostProcessorBeanDefinition", sqlSessionFactoryBeanPostProcessorBeanDefinition);
        }
    }
}
