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
     * 字段引用，统一表达左侧实体别名与字段名；columnInfo 由校验端经 resolveAndSetFieldReferenceColumnInfo 写回 fieldRef.columnInfo。可空（嵌套括号节点无字段引用）
     */
    private FieldReference fieldRef;

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
    private WhereExpression subExpression;

    /**
     * 编译期绑定：对应方法参数信息（参数路径、类型等）
     */
    private MethodParamInfo methodParamInfo;

    /**
     * 绑定阶段产出：左侧 SQL 表达式
     */
    private ConditionSqlExpression sqlExpression;

    /**
     * 绑定阶段产出：统一绑定结果
     */
    private BoundParam boundParam;

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

    public FieldReference getFieldRef() {
        return fieldRef;
    }

    public void setFieldRef(FieldReference fieldRef) {
        this.fieldRef = fieldRef;
    }

    /**
     * 委托 fieldRef.getEntityAlias()，便于调用方沿用旧访问方式
     */
    public String getFieldAlias() {
        return fieldRef != null ? fieldRef.getEntityAlias() : null;
    }

    /**
     * 委托 fieldRef.getFieldName()，便于调用方沿用旧访问方式
     */
    public String getFieldName() {
        return fieldRef != null ? fieldRef.getFieldName() : null;
    }

    /**
     * 委托 fieldRef.getColumnInfo()，便于调用方沿用旧访问方式
     */
    public ColumnInfo getColumnInfo() {
        return fieldRef != null ? fieldRef.getColumnInfo() : null;
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
