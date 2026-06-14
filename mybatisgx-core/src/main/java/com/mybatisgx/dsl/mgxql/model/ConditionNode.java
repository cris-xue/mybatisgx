package com.mybatisgx.dsl.mgxql.model;

import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.MethodParamInfo;

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
     * 是否为可选条件（对应 ? 前缀，等价于 MyBatis if 标签）
     */
    private boolean optional;
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
     * 右侧数字字面量值（如 where age > 18 中的 18）
     */
    private Integer conditionValue;
    /**
     * 嵌套子表达式（括号内的表达式）
     */
    private ConditionExpression subExpression;
    /**
     * 编译期绑定：对应数据库字段信息（列名、jdbcType 等）
     */
    private ColumnInfo columnInfo;
    /**
     * 编译期绑定：对应方法参数信息（参数路径、类型等）
     */
    private MethodParamInfo methodParamInfo;
    /**
     * 查询条件在方法名中的位置，如findById、findByName，起始位置从0开始
     */
    private int index = -1;

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

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public Integer getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(Integer conditionValue) {
        this.conditionValue = conditionValue;
    }

    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }

    public void setColumnInfo(ColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
    }

    public MethodParamInfo getMethodParamInfo() {
        return methodParamInfo;
    }

    public void setMethodParamInfo(MethodParamInfo methodParamInfo) {
        this.methodParamInfo = methodParamInfo;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * 是否是嵌套括号表达式
     */
    public boolean isNested() {
        return subExpression != null;
    }
}
