package com.mybatisgx.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author ：薛承城
 * @description：开启枚举包装类的端点
 * @date ：2020/1/7 16:52
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MapperScan
@Import({MybatisxRegistrar.class})
public @interface MybatisxScan {

    String[] entityBasePackages();

    @AliasFor(annotation = MapperScan.class, attribute = "basePackages")
    String[] daoBasePackages();

    @AliasFor(annotation = MapperScan.class, attribute = "value")
    String[] value() default {};

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

    // @AliasFor(annotation = MapperScan.class, attribute = "lazyInitialization")
    // String lazyInitialization() default "";
}
