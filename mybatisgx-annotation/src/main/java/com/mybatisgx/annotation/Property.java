package com.mybatisgx.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 指定查询字段映射的实际实体属性名称。
 *
 * <p>当查询字段名称因操作符拼接、关键字冲突或语义歧义，
 * 无法被框架准确解析时，可通过该注解显式指定对应的实体字段。</p>
 *
 * <p>例如：</p>
 * <pre>
 * {@code
 * @Property(name = "nameLike")
 * private String nameLikeLike;
 * }
 * </pre>
 *
 * <p>上述配置可避免框架将 {@code nameLikeLike} 误解析为实体字段，
 * 而是明确表示：</p>
 * <ul>
 *     <li>实体字段：nameLike</li>
 *     <li>查询操作：Like</li>
 * </ul>
 *
 * <p>适用于：</p>
 * <ul>
 *     <li>字段名与查询关键字冲突</li>
 *     <li>复杂字段命名存在歧义</li>
 *     <li>自动解析无法唯一确定字段语义</li>
 * </ul>
 *
 * @author 薛承城
 * @date 2026/4/28 19:37
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface Property {

    /**
     * 指定实际映射的 Java 实体字段名称。
     *
     * @return 实体字段名称
     */
    String name();
}
