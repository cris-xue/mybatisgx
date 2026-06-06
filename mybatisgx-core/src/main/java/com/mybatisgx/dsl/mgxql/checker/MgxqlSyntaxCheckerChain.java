package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.exception.MybatisgxException;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * MGXQL语法校验器链，按顺序执行所有语法校验器，仅依赖MgxqlStatement模型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class MgxqlSyntaxCheckerChain {

    private final List<MgxqlSyntaxChecker> checkers;

    public MgxqlSyntaxCheckerChain() {
        this.checkers = Arrays.asList(
                new AliasRequirementChecker(),
                new FieldAliasChecker(),
                new OnAliasChecker(),
                new SelectStarChecker()
        );
    }

    /**
     * 执行所有语法校验器
     *
     * @param statement MGXQL语句模型
     * @throws MybatisgxException 当存在语法校验错误时抛出
     */
    public void check(MgxqlStatement statement) {
        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(isMultipleEntities(statement));

        // 收集FROM中定义的所有别名
        collectDefinedAliases(statement, context);

        // 按order排序执行
        List<MgxqlSyntaxChecker> sortedCheckers = this.checkers.stream()
                .sorted(Comparator.comparingInt(MgxqlSyntaxChecker::getOrder))
                .collect(java.util.stream.Collectors.toList());

        for (MgxqlSyntaxChecker checker : sortedCheckers) {
            checker.check(statement, context);
        }

        if (context.hasErrors()) {
            String errorMessage = String.join("; ", context.getErrors());
            throw new MybatisgxException("MGXQL语法校验失败: %s", errorMessage);
        }
    }

    private boolean isMultipleEntities(MgxqlStatement statement) {
        if (statement.getFromClause() == null) {
            return false;
        }
        List<JoinEntity> joinEntities = statement.getFromClause().getJoinEntities();
        return joinEntities != null && !joinEntities.isEmpty();
    }

    private void collectDefinedAliases(MgxqlStatement statement, SyntaxCheckerContext context) {
        if (statement.getFromClause() == null) {
            return;
        }
        FromEntity primaryEntity = statement.getFromClause().getPrimaryEntity();
        if (primaryEntity != null && primaryEntity.getAlias() != null) {
            context.registerAlias(primaryEntity.getAlias());
        }
        if (statement.getFromClause().getJoinEntities() != null) {
            for (JoinEntity joinEntity : statement.getFromClause().getJoinEntities()) {
                if (joinEntity.getAlias() != null) {
                    context.registerAlias(joinEntity.getAlias());
                }
            }
        }
    }
}
