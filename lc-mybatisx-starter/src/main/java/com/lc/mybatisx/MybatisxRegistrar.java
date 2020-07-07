package com.lc.mybatisx;

import com.lc.mybatisx.spring.MybatisxSqlSessionFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/7 14:21
 */
public class MybatisxRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(Mybatisx.class.getName()));

        if (attrs != null) {
            String[] basePackages = (String[]) attrs.get("basePackages");
            MybatisxSqlSessionFactoryBean.setDaoPackages(basePackages);
        }
    }

}
