package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.WhereExpression;
import com.mybatisgx.dsl.mgxql.model.WhereConditionNode;
import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.EntityInfo;

/**
 * 字段存在性校验器
 * <p>
 * 校验SELECT字段、WHERE条件字段、ORDER BY字段、GROUP BY字段是否存在于对应实体中
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public abstract class FieldChecker implements MgxqlSemanticChecker {

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public boolean support(MgxqlStatement mgxqlStatement) {
        return false;
    }

    @Override
    public abstract void check(MgxqlStatement statement, CheckerContext context);

    /**
     * 递归校验条件表达式中的字段
     */
    protected void checkConditionExpressionFields(WhereExpression expression, CheckerContext context) {
        if (expression == null || expression.getNodes() == null) {
            return;
        }
        for (WhereConditionNode node : expression.getNodes()) {
            if (node.isNested()) {
                // 递归检查嵌套表达式
                this.checkConditionExpressionFields(node.getSubExpression(), context);
            } else if (node.getFieldName() != null) {
                this.checkFieldExistence(node.getFieldAlias(), node.getFieldName(), "WHERE", context);
            }
        }
    }

    /**
     * 校验字段是否存在于对应实体中
     */
    protected void checkFieldExistence(String entityAlias, String fieldName, String clauseName, CheckerContext context) {
        EntityInfo entityInfo;
        if (entityAlias != null && !entityAlias.isEmpty()) {
            entityInfo = context.getEntityInfoByAlias(entityAlias);
            if (entityInfo == null) {
                // 别名未找到实体，跳过（由EntityChecker报告错误）
                return;
            }
        } else {
            // 没有别名时使用主实体
            entityInfo = context.getPrimaryEntityInfo();
            if (entityInfo == null) {
                return;
            }
        }

        ColumnInfo columnInfo = entityInfo.getColumnInfo(fieldName);
        if (columnInfo == null) {
            context.addError(String.format("%s 子句中字段 '%s' 在实体 '%s' 中不存在", clauseName, fieldName, entityInfo.getClazz().getSimpleName()));
        }
    }
}
