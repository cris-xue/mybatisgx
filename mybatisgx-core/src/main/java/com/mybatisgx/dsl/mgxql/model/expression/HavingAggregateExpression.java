package com.mybatisgx.dsl.mgxql.model.expression;

import com.mybatisgx.dsl.mgxql.model.AggregateArgumentKind;
import com.mybatisgx.dsl.mgxql.model.AggregateFunction;

/**
 * HAVING 条件中的聚合函数表达式
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public class HavingAggregateExpression extends HavingSqlExpression {

    private final AggregateFunction function;

    private final String argument;

    private final String typeHandler;

    /**
     * 聚合函数参数类型：FIELD/NUMBER/ASTERISK；与 SelectItem.argumentKind 语义一致
     */
    private AggregateArgumentKind argumentKind;

    public HavingAggregateExpression(AggregateFunction function, String argument, String typeHandler) {
        this.function = function;
        this.argument = argument;
        this.typeHandler = typeHandler;
    }

    public HavingAggregateExpression(AggregateFunction function, String argument) {
        this(function, argument, null);
    }

    @Override
    public String toSql() {
        String arg = argument != null ? argument : "1";
        return function.getSqlKeyword() + "(" + arg + ")";
    }

    public AggregateFunction getFunction() {
        return function;
    }

    public String getArgument() {
        return argument;
    }

    public String getTypeHandler() {
        return typeHandler;
    }

    public AggregateArgumentKind getArgumentKind() {
        return argumentKind;
    }

    public void setArgumentKind(AggregateArgumentKind argumentKind) {
        this.argumentKind = argumentKind;
    }
}
