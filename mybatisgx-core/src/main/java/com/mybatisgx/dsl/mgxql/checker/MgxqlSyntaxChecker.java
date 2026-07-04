package com.mybatisgx.dsl.mgxql.checker;

/**
 * MGXQL语法校验器接口，仅依赖MgxqlStatement模型，不依赖EntityInfo等运行时元数据
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public interface MgxqlSyntaxChecker extends MgxqlChecker<SyntaxCheckerContext> {
}
