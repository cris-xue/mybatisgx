package com.mybatisgx.dsl.mgxql.model;

/**
 * MGXQL WHERE子句模型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class WhereClause {

    /**
     * 根表达式（OR层）
     */
    private ConditionExpression rootExpression;

    public WhereClause() {
    }

    public WhereClause(ConditionExpression rootExpression) {
        this.rootExpression = rootExpression;
    }

    public ConditionExpression getRootExpression() {
        return rootExpression;
    }

    public void setRootExpression(ConditionExpression rootExpression) {
        this.rootExpression = rootExpression;
    }
}
