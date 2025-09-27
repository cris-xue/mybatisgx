package com.lc.mybatisx.template;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.util.function.Supplier;

public class ConditionBuilder {

    public static class ColumnBuilder {

        public static Column with(String tableName, String columnName) {
            return new Column(new Table(tableName), columnName);
        }
    }

    /**
     * 创建等值条件
     */
    public static EqualsTo eq(String left, String right) {
        return createComparison(left, right, EqualsTo::new);
    }

    /**
     * 创建不等条件
     */
    public static NotEqualsTo neq(String left, String right) {
        return createComparison(left, right, NotEqualsTo::new);
    }

    /**
     * 创建大于条件
     */
    public static GreaterThan gt(String left, String right) {
        return createComparison(left, right, GreaterThan::new);
    }

    public static AndExpression and() {
        return new AndExpression();
    }

    /**
     * 创建 AND 条件
     */
    public static AndExpression and(Expression left, Expression right) {
        return new AndExpression(left, right);
    }

    /**
     * 创建 OR 条件
     */
    public static OrExpression or(Expression left, Expression right) {
        return new OrExpression(left, right);
    }

    /**
     * 创建带括号的表达式
     */
    public static Parenthesis paren(Expression expr) {
        return new Parenthesis(expr);
    }

    /**
     * 通用比较表达式创建方法
     */
    private static <T extends ComparisonOperator> T createComparison(String left, String right, Supplier<T> constructor) {
        T operator = constructor.get();
        operator.setLeftExpression(new Column(left));
        operator.setRightExpression(new Column(right));
        return operator;
    }

    public static ComparisonOperator buildComparisonOperator(Column leftColumn, Column rightColumn) {
        ComparisonOperator comparisonOperator = new EqualsTo();
        comparisonOperator.setLeftExpression(leftColumn);
        comparisonOperator.setRightExpression(rightColumn);
        return comparisonOperator;
    }
}
