package com.lc.mybatisx.annotation;

import javax.persistence.FetchType;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToOne {

    /**
     * 抓取策略
     *
     * @return
     */
    FetchType fetch() default FetchType.LAZY;

    /**
     * 抓取数量
     *
     * @return
     */
    int fetchSize() default 0;

    /**
     * 关系维护方
     *
     * @return
     */
    String mappedBy() default "";

    /**
     * 加载策略
     *
     * @return
     */
    LoadStrategy loadStrategy() default LoadStrategy.SUB;
}
