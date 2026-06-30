package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.AggregateArgumentKind;
import com.mybatisgx.dsl.mgxql.model.AggregateFunction;
import com.mybatisgx.dsl.mgxql.model.HavingConditionNode;
import com.mybatisgx.dsl.mgxql.model.HavingExpression;
import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import com.mybatisgx.dsl.mgxql.model.SelectItem;
import com.mybatisgx.dsl.mgxql.model.SelectItemType;
import com.mybatisgx.dsl.mgxql.model.SelectStatement;
import com.mybatisgx.dsl.mgxql.model.expression.HavingAggregateExpression;

/**
 * 聚合函数参数类型校验器
 * <p>
 * 仅 COUNT 聚合函数允许 ASTERISK（count(*)）与 NUMBER（count(1)）参数；
 * MAX/MIN/AVG/SUM 的参数 MUST 为 FIELD，遇到 ASTERISK 或 NUMBER 时报错。
 * 覆盖 SELECT 与 HAVING 子句。
 *
 * @author 薛承城
 * @date 2026/6/30
 */
public class AggregateArgumentChecker implements MgxqlSyntaxChecker {

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public boolean support(MgxqlStatement mgxqlStatement) {
        return mgxqlStatement instanceof SelectStatement;
    }

    @Override
    public void check(MgxqlStatement mgxqlStatement, SyntaxCheckerContext context) {
        SelectStatement selectStatement = (SelectStatement) mgxqlStatement;

        if (selectStatement.getSelectItems() != null) {
            for (SelectItem selectItem : selectStatement.getSelectItems()) {
                SelectItemType type = selectItem.getType();
                if (type == null || !type.hasAggregateFunction() || type == SelectItemType.COUNT) {
                    continue;
                }
                AggregateArgumentKind kind = selectItem.getArgumentKind();
                String funcName = type.name().toLowerCase();
                if (kind == AggregateArgumentKind.ASTERISK) {
                    context.addError(String.format("聚合函数 %s 不支持参数 '*'，仅 count 支持", funcName));
                } else if (kind == AggregateArgumentKind.NUMBER) {
                    context.addError(String.format("聚合函数 %s 不支持数字参数，仅 count 支持", funcName));
                }
            }
        }

        if (selectStatement.getHavingExpression() != null) {
            this.checkHavingExpression(selectStatement.getHavingExpression(), context);
        }
    }

    private void checkHavingExpression(HavingExpression expression, SyntaxCheckerContext context) {
        if (expression == null || expression.getNodes() == null) {
            return;
        }
        for (HavingConditionNode node : expression.getNodes()) {
            if (node.isNested()) {
                this.checkHavingExpression(node.getSubExpression(), context);
            } else if (node.getLeftSide() instanceof HavingAggregateExpression) {
                HavingAggregateExpression aggExpr = (HavingAggregateExpression) node.getLeftSide();
                if (aggExpr.getFunction() == AggregateFunction.COUNT) {
                    continue;
                }
                AggregateArgumentKind kind = aggExpr.getArgumentKind();
                if (kind == AggregateArgumentKind.ASTERISK) {
                    context.addError(String.format("聚合函数 %s 不支持参数 '*'，仅 count 支持",
                            aggExpr.getFunction().getSqlKeyword()));
                } else if (kind == AggregateArgumentKind.NUMBER) {
                    context.addError(String.format("聚合函数 %s 不支持数字参数，仅 count 支持",
                            aggExpr.getFunction().getSqlKeyword()));
                }
            }
        }
    }
}
