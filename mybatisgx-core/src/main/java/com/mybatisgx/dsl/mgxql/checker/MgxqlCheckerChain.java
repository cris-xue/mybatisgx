package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.EntityInfo;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MGXQL校验器链，按顺序执行所有校验器，收集全部错误后统一抛出异常
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class MgxqlCheckerChain {

    private final List<MgxqlChecker> selectCheckers;

    private final List<MgxqlChecker> dmlCheckers;

    private final MgxqlSyntaxCheckerChain syntaxCheckerChain;

    public MgxqlCheckerChain() {
        this.selectCheckers = Arrays.asList(
                new EntityChecker(),
                new SelectFieldChecker(),
                new JoinRelationChecker(),
                new OperatorTypeChecker(),
                new WhereFieldChecker()
        );
        this.dmlCheckers = Arrays.asList(
                new EntityChecker(),
                new WhereFieldChecker(),
                new OperatorTypeChecker()
        );
        this.syntaxCheckerChain = new MgxqlSyntaxCheckerChain();
    }

    /**
     * 执行校验器，根据commandType选择校验链
     *
     * @param statement  MGXQL语句模型
     * @param entityInfo 主实体信息
     * @throws MybatisgxException 当存在校验错误时抛出
     */
    public void check(MgxqlStatement statement, EntityInfo entityInfo) {
        // 先执行语法校验，语法校验失败时直接抛出异常，不继续执行语义校验
        this.syntaxCheckerChain.check(statement);

        CheckerContext context = new CheckerContext(entityInfo);

        SqlCommandType commandType = statement.getCommandType();
        List<MgxqlChecker> activeCheckers = (commandType == SqlCommandType.DELETE || commandType == SqlCommandType.UPDATE)
                ? this.dmlCheckers : this.selectCheckers;

        // 按order排序执行
        List<MgxqlChecker> sortedCheckers = activeCheckers.stream()
                .sorted(Comparator.comparingInt(MgxqlChecker::getOrder))
                .collect(Collectors.toList());

        for (MgxqlChecker checker : sortedCheckers) {
            if (checker.support(statement)) {
                checker.check(statement, context);
            }
        }

        // 收集所有错误后统一抛出
        if (context.hasErrors()) {
            String errorMessage = String.join("; ", context.getErrors());
            throw new MybatisgxException("MGXQL语义校验失败: %s", errorMessage);
        }
    }
}
