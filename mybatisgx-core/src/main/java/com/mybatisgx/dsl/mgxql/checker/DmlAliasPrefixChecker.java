package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.WhereExpression;
import com.mybatisgx.dsl.mgxql.model.WhereConditionNode;
import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import com.mybatisgx.dsl.mgxql.model.ModifyStatement;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * DELETE/UPDATE语句别名前缀校验器
 * <p>
 * DELETE/UPDATE语句没有FROM子句，不允许在WHERE条件字段中使用实体别名前缀
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class DmlAliasPrefixChecker implements MgxqlSyntaxChecker {

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public boolean support(MgxqlStatement mgxqlStatement) {
        return mgxqlStatement instanceof ModifyStatement;
    }

    @Override
    public void check(MgxqlStatement statement, SyntaxCheckerContext context) {
        SqlCommandType commandType = statement.getCommandType();
        if (commandType != SqlCommandType.DELETE && commandType != SqlCommandType.UPDATE) {
            return;
        }
        if (statement.getWhereClause() != null && statement.getWhereClause().getRootExpression() != null) {
            this.checkConditionExpression(statement.getWhereClause().getRootExpression(), context);
        }
    }

    private void checkConditionExpression(WhereExpression expression, SyntaxCheckerContext context) {
        if (expression == null || expression.getNodes() == null) {
            return;
        }
        for (WhereConditionNode node : expression.getNodes()) {
            if (node.isNested()) {
                this.checkConditionExpression(node.getSubExpression(), context);
            } else if (node.getFieldAlias() != null && !node.getFieldAlias().isEmpty()) {
                context.addError(String.format("DELETE/UPDATE 语句中不允许使用实体别名前缀 '%s'", node.getFieldAlias()));
            }
        }
    }
}
