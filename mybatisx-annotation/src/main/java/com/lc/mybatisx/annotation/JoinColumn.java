package com.lc.mybatisx.annotation;

import java.lang.annotation.*;

/**
 * 实体关联关系中的外键列
 * @author ccxuef
 * @date 2025/8/8 17:40
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JoinColumn {

    /**
     * 外键列的名称（当前实体表中的列名）
     * @return
     */
    String name() default "";

    /**
     * 被引用实体的主键列名（目标表的主键列）
     * @return
     */
    String referencedColumnName() default "id";
}
