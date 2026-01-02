package com.mybatisgx.annotation;

import java.lang.annotation.*;

/**
 *
 * 用于在 Mapper 方法上声明一条数据库操作语句的定义入口。
 * 该注解描述的是**“语句的表达方式”**，而非最终执行的 SQL。
 *
 * Statement 支持多种语句描述形式：
 *
 * 基于方法名的派生规则
 *
 * 基于对象模型的表达式
 * -（预留）基于原生 SQL 或其他表达语言
 *
 * 在框架启动阶段，Statement 会被解析为内部的语句模型，用于后续执行阶段的 SQL 生成与执行。
 * @author 薛承城
 * @date 2025/11/11 11:22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Statement {

    /**
     * 表示语句的原始描述内容，其具体含义由 type 指定的语句语言决定。
     * 例如方法名：findByAaaAnd(bbbOrCcc)、updateByIdAndName(bbbOrCcc)
     * @return
     */
    String value();

    /**
     * 语言类型
     * @return
     */
    StatementLanguage language() default StatementLanguage.METHOD;
}
