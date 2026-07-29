package com.mybatisgx.dsl.mgxql.model;

import com.mybatisgx.dsl.mgxql.model.expression.ConditionSqlExpression;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.MethodParamInfo;

import java.util.Collections;
import java.util.List;

/**
 * WHERE条件节点模型，可以是基础条件或嵌套括号表达式
 * <p>作为 {@link WhereElement} 平面变体之一（普通条件），与 Bracket/If/Choose/When 变体并列（design D2）。
 * 原 {@code ?} 前缀 optional 已退役：可选条件统一由 {@link BracketDirectiveNode} / {@link IfDirectiveNode} 承载（design D3）。
 * {@code index} 保留（METHOD_NAME 来源 argN 绑定，task 1.4 确认不被波及），{@code subExpression} 保留承载 SQL 括号分组（design Q2）。
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class WhereConditionNode extends WhereElement {

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
     * 复杂 IN 集合信息（解析阶段填，design D6 task 5.3）：{@code in (item:coll)=>[field]} 的 itemName/collectionName/valueExpr。
     * 简单 IN（{@code in (:ids)}）不填，绑定阶段默认 itemName="item"。绑定阶段据此转挂 BoundParam.collectionInfo。
     */
    private CollectionInfo collectionInfo;

    /**
     * 查询条件在方法名中的位置，如findById、findByName，起始位置从0开始
     */
    private int index = -1;

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

    public boolean isNested() {
        return subExpression != null;
    }

    /**
     * 普通条件节点本身即 {@link WhereConditionNode}（design D2 平面变体之一）。
     */
    @Override
    public boolean isCondition() {
        return true;
    }

    @Override
    public WhereConditionNode asCondition() {
        return this;
    }

    /**
     * SQL 括号分组递归入口：嵌套时返回 [subExpression]，否则空（与现 7+ checker 的 isNested() 模式同构，design Q2/2.6）。
     */
    @Override
    public List<WhereExpression> getChildExpressions() {
        return subExpression != null ? Collections.singletonList(subExpression) : Collections.<WhereExpression>emptyList();
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

    public CollectionInfo getCollectionInfo() {
        return collectionInfo;
    }

    public void setCollectionInfo(CollectionInfo collectionInfo) {
        this.collectionInfo = collectionInfo;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
