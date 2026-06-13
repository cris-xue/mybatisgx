package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;

/**
 * MGXQL语义校验器接口
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public interface MgxqlChecker {

    /**
     * 获取校验器执行顺序，值越小越先执行
     */
    int getOrder();

    boolean support(MgxqlStatement mgxqlStatement);

    /**
     * 执行语义校验
     *
     * @param mgxqlStatement MGXQL语句模型
     * @param context        校验上下文
     */
    void check(MgxqlStatement mgxqlStatement, CheckerContext context);
}
