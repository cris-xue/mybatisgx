package com.mybatisgx.annotation;

import java.lang.annotation.*;

/**
 * 实体关联关系中的外键列定义
 * <p>
 * 可用于：
 * <ul>
 *   <li>当前实体表中的外键列（配合 {@link JoinColumns} 使用）</li>
 *   <li>中间表中的外键列（配合 {@link JoinTable} 使用）</li>
 * </ul>
 * @author 薛承城
 * @date 2025/8/8 17:40
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JoinColumn {

    /**
     * 外键列名。
     * <p>
     * 根据使用场景不同，表示：
     * <ul>
     *   <li>当前实体表中的外键列名</li>
     *   <li>中间表中的外键列名</li>
     * </ul>
     * @return
     */
    String name() default "";

    /**
     * 外键所引用的目标表列名，通常为主键列。
     * @return
     */
    String referencedColumnName() default "id";
}
