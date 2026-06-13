package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.FromEntity;
import com.mybatisgx.dsl.mgxql.model.JoinEntity;
import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import com.mybatisgx.dsl.mgxql.model.SelectStatement;
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

    private final List<MgxqlSyntaxChecker> selectCheckers;

    private final List<MgxqlSyntaxChecker> dmlCheckers;

    public MgxqlSyntaxCheckerChain() {
        this.selectCheckers = Arrays.asList(
                new AliasRequirementChecker(),
                new FieldAliasChecker(),
                new OnAliasChecker(),
                new SelectStarChecker()
        );
        this.dmlCheckers = Arrays.asList(
                new WhereRequiredChecker(),
                new DmlAliasPrefixChecker()
        );
    }

    /**
     * 执行语法校验器，根据commandType选择校验链
     *
     * @param statement MGXQL语句模型
     * @throws MybatisgxException 当存在语法校验错误时抛出
     */
    public void check(MgxqlStatement statement) {
        SqlCommandType commandType = statement.getCommandType();
        if (commandType == SqlCommandType.DELETE || commandType == SqlCommandType.UPDATE) {
            this.checkDml(statement);
        } else {
            this.checkSelect(statement);
        }
    }

    private void checkSelect(MgxqlStatement mgxqlStatement) {
        SelectStatement selectStatement = (SelectStatement) mgxqlStatement;

        SyntaxCheckerContext context = new SyntaxCheckerContext();
        context.setHasMultipleEntities(isMultipleEntities(selectStatement));
        collectDefinedAliases(selectStatement, context);

        List<MgxqlSyntaxChecker> sortedCheckers = this.selectCheckers.stream()
                .sorted(Comparator.comparingInt(MgxqlSyntaxChecker::getOrder))
                .collect(java.util.stream.Collectors.toList());

        for (MgxqlSyntaxChecker checker : sortedCheckers) {
            checker.check(selectStatement, context);
        }

        if (context.hasErrors()) {
            String errorMessage = String.join("; ", context.getErrors());
            throw new MybatisgxException("MGXQL语法校验失败: %s", errorMessage);
        }
    }

    private void checkDml(MgxqlStatement statement) {
        SyntaxCheckerContext context = new SyntaxCheckerContext();

        List<MgxqlSyntaxChecker> sortedCheckers = this.dmlCheckers.stream()
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

    private boolean isMultipleEntities(SelectStatement statement) {
        if (statement.getFromClause() == null) {
            return false;
        }
        List<JoinEntity> joinEntities = statement.getFromClause().getJoinEntities();
        return joinEntities != null && !joinEntities.isEmpty();
    }

    private void collectDefinedAliases(SelectStatement statement, SyntaxCheckerContext context) {
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
