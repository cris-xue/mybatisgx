package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.EntityInfo;

/**
 * MGXQL校验器链，按顺序执行所有校验器，收集全部错误后统一抛出异常
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class MgxqlCheckerChain {

    private final MgxqlSyntaxCheckerChain mgxqlSyntaxCheckerChain;
    private final MgxqlSemanticCheckerChain mgxqlSemanticCheckerChain;

    public MgxqlCheckerChain() {
        this.mgxqlSyntaxCheckerChain = new MgxqlSyntaxCheckerChain();
        this.mgxqlSemanticCheckerChain = new MgxqlSemanticCheckerChain();
    }

    /**
     * 执行校验器，根据commandType选择校验链
     *
     * @param mgxqlStatement MGXQL语句模型
     * @param entityInfo     主实体信息
     * @throws MybatisgxException 当存在校验错误时抛出
     */
    public void check(MgxqlStatement mgxqlStatement, EntityInfo entityInfo) {
        // 先执行语法校验，语法校验失败时直接抛出异常，不继续执行语义校验
        this.mgxqlSyntaxCheckerChain.check(mgxqlStatement);
        // 语义校验
        this.mgxqlSemanticCheckerChain.check(mgxqlStatement, entityInfo);
    }
}
