package com.mybatisgx.annotation;

import java.lang.annotation.*;

/**
 * 在 Mapper 方法上声明一条 mgxsql 简化语法的 SQL 语句。
 * <p>
 * mgxsql 是原生 SQL 的动态语法简化，替代 MyBatis XML 的 if/foreach/bind 标签：
 * <ul>
 *   <li>?条件 → 参数非空时才拼接（等价 &lt;if test="参数!=null"&gt;）</li>
 *   <li>?(expr)(条件) → 表达式为 true 时才拼接</li>
 *   <li>in #{list} → 自动展开为 foreach</li>
 *   <li>%#{x}% → 自动处理 LIKE 模式</li>
 *   <li>where → 自动转为 &lt;where&gt; 标签</li>
 *   <li>set → 自动转为 &lt;set&gt; 标签</li>
 * </ul>
 * <p>
 * 与 {@link Statement} 注解互斥，同一方法不能同时使用。
 *
 * @author 薛承城
 * @date 2026/7/7
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SimpleSql {

    /**
     * mgxsql SQL 文本
     *
     * @return mgxsql SQL 文本
     */
    String value();
}
