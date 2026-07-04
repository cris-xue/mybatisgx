package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;

/**
 * MGXQL校验器顶层泛型接口，定义完整的校验协议
 * <p>
 * C 为校验上下文类型：语法校验使用 SyntaxCheckerContext，语义校验使用 CheckerContext
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public interface MgxqlChecker<C> {

    /**
     * 获取校验器执行顺序，值越小越先执行
     */
    int getOrder();

    boolean support(MgxqlStatement mgxqlStatement);

    void check(MgxqlStatement mgxqlStatement, C context);
}
