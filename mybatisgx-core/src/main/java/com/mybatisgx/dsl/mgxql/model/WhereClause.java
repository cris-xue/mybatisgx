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
    private WhereExpression rootExpression;

    public WhereClause() {
    }

    public WhereClause(WhereExpression rootExpression) {
        this.rootExpression = rootExpression;
    }

    public WhereExpression getRootExpression() {
        return rootExpression;
    }

    public void setRootExpression(WhereExpression rootExpression) {
        this.rootExpression = rootExpression;
    }
}
