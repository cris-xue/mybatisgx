package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;

/**
 * MGXQL语法校验器接口，仅依赖MgxqlStatement模型，不依赖EntityInfo等运行时元数据
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public interface MgxqlSyntaxChecker {

    int getOrder();

    boolean support(MgxqlStatement mgxqlStatement);

    void check(MgxqlStatement mgxqlStatement, SyntaxCheckerContext context);
}
