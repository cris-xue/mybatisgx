package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 别名要求校验器
 * <p>
 * R1: 多实体时所有实体必须定义别名
 * R2: 别名唯一性（不同实体不能用相同别名）
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class AliasRequirementChecker implements MgxqlSyntaxChecker {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void check(MgxqlStatement statement, SyntaxCheckerContext context) {
        if (!context.isHasMultipleEntities()) {
            return;
        }

        FromClause fromClause = statement.getFromClause();
        if (fromClause == null) {
            return;
        }

        // R1: 多实体时所有实体必须定义别名
        FromEntity primaryEntity = fromClause.getPrimaryEntity();
        if (primaryEntity != null && primaryEntity.getAlias() == null) {
            context.addError(String.format("多实体查询中，实体 '%s' 必须定义别名", primaryEntity.getEntityName()));
        }

        if (fromClause.getJoinEntities() != null) {
            for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
                if (joinEntity.getAlias() == null) {
                    context.addError(String.format("多实体查询中，实体 '%s' 必须定义别名", joinEntity.getEntityName()));
                }
            }
        }

        // R2: 别名唯一性
        Set<String> seenAliases = new HashSet<>();
        checkAliasUniqueness(primaryEntity, seenAliases, context);
        if (fromClause.getJoinEntities() != null) {
            for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
                checkAliasUniqueness(joinEntity, seenAliases, context);
            }
        }
    }

    private void checkAliasUniqueness(FromEntity entity, Set<String> seenAliases, SyntaxCheckerContext context) {
        if (entity == null || entity.getAlias() == null) {
            return;
        }
        if (!seenAliases.add(entity.getAlias())) {
            context.addError(String.format("别名 '%s' 重复定义，不同实体不能使用相同别名", entity.getAlias()));
        }
    }
}
