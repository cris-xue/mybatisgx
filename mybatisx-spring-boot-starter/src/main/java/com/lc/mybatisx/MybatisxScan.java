package com.lc.mybatisx;

import org.springframework.context.annotation.Import;

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
@Import({EnumEndpointRegistrar.class})
public @interface MybatisxScan {

    String[] entityBasePackages();

    String[] daoBasePackages();

}
