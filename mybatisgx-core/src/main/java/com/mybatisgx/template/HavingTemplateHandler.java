package com.mybatisgx.template;

import com.mybatisgx.dsl.mgxql.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;

/**
 * HAVING 条件模板处理器，从 HavingExpression 树渲染 MyBatis XML
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public class HavingTemplateHandler {

    public String execute(HavingExpression expression) {
        if (expression == null || ObjectUtils.isEmpty(expression.getNodes())) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" having");
        this.renderExpression(sb, expression);
        return sb.toString();
    }

    private void renderExpression(StringBuilder sb, HavingExpression expression) {
        for (HavingConditionNode node : expression.getNodes()) {
            if (node.isNested()) {
                this.renderNestedNode(sb, node);
            } else {
                this.renderLeafNode(sb, node);
            }
        }
    }

    private void renderNestedNode(StringBuilder sb, HavingConditionNode node) {
        LogicOperator logicOp = node.getLogicOperator();
        String logicStr = logicOp != null && logicOp != LogicOperator.NULL ? " " + logicOp.getValue() : "";
        sb.append(logicStr).append(" (");
        this.renderExpression(sb, node.getSubExpression());
        sb.append(")");
    }

    private void renderLeafNode(StringBuilder sb, HavingConditionNode node) {
        BoundParam boundParam = node.getBoundParam();
        if (boundParam == null) {
            return;
        }

        LogicOperator logicOp = node.getLogicOperator();
        String logicStr = logicOp != null && logicOp != LogicOperator.NULL ? " " + logicOp.getValue() : "";

        String leftSql = "";
        if (ObjectUtils.isNotEmpty(boundParam.getEntries())) {
            BoundParamEntry entry = boundParam.getEntries().get(0);
            if (entry.getSqlExpression() != null) {
                leftSql = entry.getSqlExpression().toSql();
            }
        }

        String operatorStr = boundParam.getOperator().getValue();

        if (ObjectUtils.isNotEmpty(boundParam.getEntries())) {
            BoundParamEntry entry = boundParam.getEntries().get(0);
            if (entry.getLiteralValue() != null) {
                sb.append(String.format("%s %s %s %s", logicStr, leftSql, operatorStr, entry.getLiteralValue()));
            } else if (ObjectUtils.isNotEmpty(entry.getParamPath())) {
                String paramPath = StringUtils.join(entry.getParamPath(), ".");
                sb.append(String.format("%s %s %s #{%s}", logicStr, leftSql, operatorStr, paramPath));
            } else {
                sb.append(String.format("%s %s %s", logicStr, leftSql, operatorStr));
            }
        }
    }
}
