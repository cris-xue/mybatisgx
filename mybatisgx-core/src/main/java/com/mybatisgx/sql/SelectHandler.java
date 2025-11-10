package com.mybatisgx.sql;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.JsonOperator;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

public class SelectHandler {

    public String processSelect(Statement statement, String tenantId) {
        processSelect((Select) statement, tenantId);
        return statement.toString(); // 非查询语句直接返回
    }

    /**
     * 处理SELECT语句
     */
    private void processSelect(Select select, String tenantId) {
        // 处理WITH子句（CTE）
        if (select.getWithItemsList() != null) {
            for (WithItem withItem : select.getWithItemsList()) {
                processSelectBody(withItem.getSelectBody(), tenantId);
            }
        }

        // 处理主查询体
        processSelectBody(select.getSelectBody(), tenantId);
    }

    /**
     * 处理SELECT主体
     */
    private void processSelectBody(Select select, String tenantId) {
        if (select instanceof PlainSelect) {
            processPlainSelect((PlainSelect) select, tenantId);
        }
        // 处理UNION/INTERSECT等复合查询
        else if (select instanceof SetOperationList) {
            SetOperationList setOperationList = (SetOperationList) select;
            for (Select body : setOperationList.getSelects()) {
                processSelectBody(body, tenantId);
            }
        }
    }

    /**
     * 处理普通SELECT语句
     */
    private void processPlainSelect(PlainSelect plainSelect, String tenantId) {
        // 1. 处理主表
        processFromItem(plainSelect.getFromItem(), plainSelect, tenantId);

        // 2. 处理JOIN表
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                processFromItem(join.getRightItem(), plainSelect, tenantId);
            }
        }

        // 3. 处理WHERE子句中的子查询
        if (plainSelect.getWhere() != null) {
            processExpression(plainSelect.getWhere(), tenantId);
        }

        // 4. 处理HAVING子句中的子查询
        if (plainSelect.getHaving() != null) {
            processExpression(plainSelect.getHaving(), tenantId);
        }

        // 5. 处理SELECT列表中的子查询
        for (SelectItem selectItem : plainSelect.getSelectItems()) {
            if (selectItem instanceof SelectItem) {
                Expression expression = selectItem.getExpression();
                processExpression(expression, tenantId);
            }
        }
    }

    /**
     * 处理FROM子句项
     */
    private void processFromItem(FromItem fromItem, PlainSelect parentSelect, String tenantId) {
        if (fromItem instanceof Table) {
            // 处理普通表
            addTenantCondition(parentSelect, (Table) fromItem, tenantId);
        } else if (fromItem instanceof ParenthesedFromItem) {
            // 处理带括号的FROM项（通常是子查询）
            ParenthesedFromItem parenthesedItem = (ParenthesedFromItem) fromItem;
            if (parenthesedItem.getFromItem() instanceof Select) {
                // 递归处理子查询
                processSelect((Select) parenthesedItem.getFromItem(), tenantId);
            }
        } else if (fromItem instanceof LateralSubSelect) {
            // 处理LATERAL子查询
            LateralSubSelect lateral = (LateralSubSelect) fromItem;
            processSelect(lateral.getSelect(), tenantId);
        }
        // VALUES和其他类型不需要处理
    }

    /**
     * 处理表达式中的子查询
     */
    private void processExpression(Expression expression, String tenantId) {
        if (expression == null) return;

        expression.accept(new ExpressionVisitorAdapter() {
            @Override
            public void visit(Select select) {
                // 处理表达式中的子查询
                processSelect(select, tenantId);
            }

            @Override
            public void visit(CaseExpression caseExpression) {
                // 处理CASE WHEN中的子查询
                if (caseExpression.getSwitchExpression() != null) {
                    processExpression(caseExpression.getSwitchExpression(), tenantId);
                }
                for (WhenClause whenClause : caseExpression.getWhenClauses()) {
                    processExpression(whenClause.getWhenExpression(), tenantId);
                    processExpression(whenClause.getThenExpression(), tenantId);
                }
                if (caseExpression.getElseExpression() != null) {
                    processExpression(caseExpression.getElseExpression(), tenantId);
                }
            }

            @Override
            public void visit(Function function) {
                // 处理函数参数中的子查询
                if (function.getParameters() != null) {
                    for (Expression param : function.getParameters()) {
                        processExpression(param, tenantId);
                    }
                }
            }

            @Override
            public void visit(ExistsExpression existsExpression) {
                // 处理EXISTS子查询
                processExpression(existsExpression.getRightExpression(), tenantId);
            }

            @Override
            public void visit(JsonOperator jsonOperator) {
                // 处理JSON表达式中的子查询
                processExpression(jsonOperator.getLeftExpression(), tenantId);
                processExpression(jsonOperator.getRightExpression(), tenantId);
            }
        });
    }

    /**
     * 添加租户条件
     */
    private void addTenantCondition(PlainSelect plainSelect, Table table, String tenantId) {
        // 构建租户条件表达式: tenant_id = 'xxx'
        EqualsTo tenantCondition = new EqualsTo(
                new Column(getTableAlias(table) + ".tenant_id"),
                new StringValue(tenantId)
        );

        // 合并到WHERE条件
        Expression where = plainSelect.getWhere();
        if (where == null) {
            plainSelect.setWhere(tenantCondition);
        } else {
            plainSelect.setWhere(new AndExpression(where, tenantCondition));
        }
    }

    /**
     * 获取表别名
     */
    private String getTableAlias(Table table) {
        if (table.getAlias() != null) {
            return table.getAlias().getName();
        }
        return table.getName();
    }

    /*private void addTenantCondition(PlainSelect plainSelect, Table table, String tenantId) {
        // 构建租户条件表达式: tenant_id = 'xxx'
        EqualsTo tenantCondition = new EqualsTo(
                new Column(table.getAlias() != null ? table.getAlias().getName() + ".tenant_id" : "tenant_id"),
                new StringValue(tenantId)
        );

        // 合并到WHERE条件
        Expression where = plainSelect.getWhere();
        if (where == null) {
            plainSelect.setWhere(tenantCondition);
        } else {
            plainSelect.setWhere(new AndExpression(where, tenantCondition));
        }
    }*/
}
