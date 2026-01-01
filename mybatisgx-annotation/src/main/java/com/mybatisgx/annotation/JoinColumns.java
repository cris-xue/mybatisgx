package com.mybatisgx.annotation;

import java.lang.annotation.*;

/**
 * 用于一对一或一对多关联关系中，定义当前实体表上的外键列。
 * <p>
 * 主要用于以下场景：
 * <ul>
 *   <li>外键位于当前实体表中</li>
 *   <li>外键由多个列组成（复合外键）</li>
 * </ul>
 *
 * <p><b>使用约束：</b></p>
 * <ul>
 *   <li>仅用于一对一（One-to-One）或一对多（One-to-Many）关系</li>
 *   <li>不可与 {@link JoinTable} 同时使用</li>
 * </ul>
 * @author 薛承城
 * @date 2025/12/23 11:01
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JoinColumns {

    JoinColumn[] value();
}
