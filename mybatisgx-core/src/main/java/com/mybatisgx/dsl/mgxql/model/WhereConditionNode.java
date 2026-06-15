package com.mybatisgx.dsl.mgxql.model;

import com.mybatisgx.dsl.mgxql.model.expression.ConditionSqlExpression;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.MethodParamInfo;

import java.util.List;

/**
 * WHERE条件节点模型，可以是基础条件或嵌套括号表达式
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class WhereConditionNode {

    private LogicOperator logicOperator = LogicOperator.NULL;

    private boolean optional;

    private String leftBracket;

    private String rightBracket;

    private String fieldAlias;

    private String fieldName;

    private ComparisonOperator notOperator;

    private ComparisonOperator operator;

    private List<String> paramValuePath;

    private Integer conditionValue;

    private WhereExpression subExpression;

    private ColumnInfo columnInfo;

    private MethodParamInfo methodParamInfo;

    /**
     * 绑定阶段产出：左侧 SQL 表达式
     */
    private ConditionSqlExpression sqlExpression;

    /**
     * 绑定阶段产出：统一绑定结果
     */
    private BoundParam boundParam;

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

    public WhereExpression getSubExpression() {
        return subExpression;
    }

    public void setSubExpression(WhereExpression subExpression) {
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

    public ConditionSqlExpression getSqlExpression() {
        return sqlExpression;
    }

    public void setSqlExpression(ConditionSqlExpression sqlExpression) {
        this.sqlExpression = sqlExpression;
    }

    public BoundParam getBoundParam() {
        return boundParam;
    }

    public void setBoundParam(BoundParam boundParam) {
        this.boundParam = boundParam;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isNested() {
        return subExpression != null;
    }
}
