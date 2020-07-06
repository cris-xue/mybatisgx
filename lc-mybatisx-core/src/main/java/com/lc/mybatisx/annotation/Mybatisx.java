package com.lc.mybatisx.annotation;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/6 10:14
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MapperScan
public @interface Mybatisx {

    @AliasFor(annotation = MapperScan.class, attribute = "value")
    String[] value() default {};

    @AliasFor(annotation = MapperScan.class, attribute = "basePackages")
    String[] basePackages() default {};

    @AliasFor(annotation = MapperScan.class, attribute = "basePackageClasses")
    Class<?>[] basePackageClasses() default {};

    @AliasFor(annotation = MapperScan.class, attribute = "nameGenerator")
    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    @AliasFor(annotation = MapperScan.class, attribute = "annotationClass")
    Class<? extends Annotation> annotationClass() default Annotation.class;

    @AliasFor(annotation = MapperScan.class, attribute = "markerInterface")
    Class<?> markerInterface() default Class.class;

    @AliasFor(annotation = MapperScan.class, attribute = "sqlSessionTemplateRef")
    String sqlSessionTemplateRef() default "";

    @AliasFor(annotation = MapperScan.class, attribute = "sqlSessionFactoryRef")
    String sqlSessionFactoryRef() default "";

    @AliasFor(annotation = MapperScan.class, attribute = "factoryBean")
    Class<? extends MapperFactoryBean> factoryBean() default MapperFactoryBean.class;

    @AliasFor(annotation = MapperScan.class, attribute = "lazyInitialization")
    String lazyInitialization() default "";

}
