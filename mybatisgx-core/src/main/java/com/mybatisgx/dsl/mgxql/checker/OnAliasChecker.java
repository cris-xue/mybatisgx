package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.FromEntity;
import com.mybatisgx.dsl.mgxql.model.JoinEntity;
import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import com.mybatisgx.dsl.mgxql.model.SelectStatement;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ON子句别名校验器
 * <p>
 * R6: ON左侧别名必须是之前已定义的实体别名（主实体或前面的JOIN）
 * R7: ON右侧别名必须是当前JOIN子句的实体别名
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class OnAliasChecker implements MgxqlSyntaxChecker {

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public boolean support(MgxqlStatement mgxqlStatement) {
        return mgxqlStatement instanceof SelectStatement;
    }

    @Override
    public void check(MgxqlStatement mgxqlStatement, SyntaxCheckerContext context) {
        SelectStatement selectStatement = (SelectStatement) mgxqlStatement;
        if (selectStatement.getFromClause() == null || selectStatement.getFromClause().getJoinEntities() == null) {
            return;
        }

        Set<String> previousAliases = new LinkedHashSet<>();
        FromEntity primaryEntity = selectStatement.getFromClause().getPrimaryEntity();
        if (primaryEntity != null && primaryEntity.getAlias() != null) {
            previousAliases.add(primaryEntity.getAlias());
        }

        for (JoinEntity joinEntity : selectStatement.getFromClause().getJoinEntities()) {
            String onLeftAlias = joinEntity.getOnLeftAlias();
            String onRightAlias = joinEntity.getOnRightAlias();

            if (onLeftAlias != null && onRightAlias != null) {
                // R7: ON右侧必须是当前JOIN的实体别名
                String currentJoinAlias = joinEntity.getAlias();
                if (currentJoinAlias != null && !currentJoinAlias.equals(onRightAlias)) {
                    context.addError(String.format(
                            "ON子句右侧别名必须是当前JOIN的实体别名，实际为 '%s'", onRightAlias));
                }

                // R6: ON左侧必须是之前已定义的实体别名
                if (!previousAliases.contains(onLeftAlias)) {
                    // 如果左侧是当前JOIN的别名，提示方向反了
                    if (currentJoinAlias != null && currentJoinAlias.equals(onLeftAlias)) {
                        context.addError(String.format(
                                "ON子句左侧别名 '%s' 是当前JOIN的实体别名，不能在左侧使用，请调整ON子句方向", onLeftAlias));
                    } else if (!context.isAliasDefined(onLeftAlias)) {
                        context.addError(String.format(
                                "ON子句中别名 '%s' 未在FROM子句中定义", onLeftAlias));
                    } else {
                        context.addError(String.format(
                                "ON子句左侧别名 '%s' 必须是已定义的实体别名", onLeftAlias));
                    }
                }
            }

            // 当前JOIN的别名加入已定义集合，供后续JOIN校验
            if (joinEntity.getAlias() != null) {
                previousAliases.add(joinEntity.getAlias());
            }
        }
    }
}
