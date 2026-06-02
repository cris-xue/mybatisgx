package com.mybatisgx.dsl.mgxql.model;

import com.mybatisgx.model.ComparisonOperator;
import com.mybatisgx.model.LogicOperator;

import java.util.List;

/**
 * MGXQL条件节点模型，可以是基础条件或嵌套括号表达式
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class ConditionNode {

    /**
     * 与前一节点的逻辑关系（AND/OR/NULL）
     */
    private LogicOperator logicOperator = LogicOperator.NULL;

    /**
     * 左括号
     */
    private String leftBracket;

    /**
     * 右括号
     */
    private String rightBracket;

    /**
     * 左侧实体别名（如 user，可选）
     */
    private String fieldAlias;

    /**
     * 左侧字段名（如 name）
     */
    private String fieldName;

    /**
     * NOT修饰符（用于 not in、not like 等）
     */
    private ComparisonOperator notOperator;

    /**
     * 比较运算符（=、<、>、like、in、is null 等）
     */
    private ComparisonOperator operator;

    /**
     * 右侧参数值路径（如 [name] 或 [role, menu, name]）
     */
    private List<String> paramValuePath;

    /**
     * 嵌套子表达式（括号内的表达式）
     */
    private ConditionExpression subExpression;

    public LogicOperator getLogicOperator() {
        return logicOperator;
    }

    public void setLogicOperator(LogicOperator logicOperator) {
        this.logicOperator = logicOperator;
    }

    public String getLeftBracket() {
        return leftBracket;
    }

    public void setLeftBracket(String leftBracket) {
        this.leftBracket = leftBracket;
    }

    public String getRightBracket() {
        return rightBracket;
    }

    public void setRightBracket(String rightBracket) {
        this.rightBracket = rightBracket;
    }

    public String getFieldAlias() {
        return fieldAlias;
    }

    public void setFieldAlias(String fieldAlias) {
        this.fieldAlias = fieldAlias;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public ComparisonOperator getNotOperator() {
        return notOperator;
    }

    public void setNotOperator(ComparisonOperator notOperator) {
        this.notOperator = notOperator;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public void setOperator(ComparisonOperator operator) {
        this.operator = operator;
    }

    public List<String> getParamValuePath() {
        return paramValuePath;
    }

    public void setParamValuePath(List<String> paramValuePath) {
        this.paramValuePath = paramValuePath;
    }

    public ConditionExpression getSubExpression() {
        return subExpression;
    }

    public void setSubExpression(ConditionExpression subExpression) {
        this.subExpression = subExpression;
    }

    /**
     * 是否是嵌套括号表达式
     */
    public boolean isNested() {
        return subExpression != null;
    }
}
