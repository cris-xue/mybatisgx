package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * WHERE非空校验器，DELETE/UPDATE语句必须包含WHERE条件
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class WhereRequiredChecker implements MgxqlSyntaxChecker {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean support(MgxqlStatement statement) {
        return true;
    }

    @Override
    public void check(MgxqlStatement statement, SyntaxCheckerContext context) {
        SqlCommandType commandType = statement.getCommandType();
        if (commandType != SqlCommandType.DELETE && commandType != SqlCommandType.UPDATE) {
            return;
        }
        if (statement.getWhereClause() == null
                || statement.getWhereClause().getRootExpression() == null
                || statement.getWhereClause().getRootExpression().getNodes().isEmpty()) {
            String commandName = commandType == SqlCommandType.DELETE ? "DELETE" : "UPDATE";
            context.addError(String.format("%s 语句必须包含 WHERE 条件", commandName));
        }
    }
}
