package com.mybatisgx.model;

import java.util.List;

/**
 * 方法名条件信息【修改、删除、查询都可以存在条件】
 * @author 薛承城
 * @date 2025/11/18 12:04
 */
public class ConditionInfo {

    /**
     * 查询条件在方法名中的位置，如findById、findByName，其实位置从0开始
     */
    private Integer index;
    /**
     * 条件原始片段，AndUserNameEq
     */
    private String originSegment;
    /**
     * 逻辑操作符【And、Or】
     */
    private LogicOperator logicOperator = LogicOperator.NULL;
    /**
     * 条件字段名
     * 类型为方法名：findByNameAndUserId，条件名为name、userId
     * 类型为实体字段：条件名为nameLike、userId、userId、timeBetween
     */
    private String columnName;
    /**
     * 比较操作符not
     */
    private ComparisonOperator comparisonNotOperator;
    /**
     * 比较操作符【=、<=、!=】
     */
    private ComparisonOperator comparisonOperator = ComparisonOperator.EQ;
    /**
     * 条件来源
     */
    private ConditionOriginType conditionOriginType;
    /**
     * 条件绑定的参数信息
     */
    private MethodParamInfo methodParamInfo;
    /**
     * 条件绑定数据库表实体对应的字段信息，条件字段必须都是数据库表实体中的字段，在解析完条件后这个字段是不能为空的
     */
    private ColumnInfo columnInfo;
    /**
     * 左括号
     */
    private String leftBracket;
    /**
     * 右括号
     */
    private String rightBracket;
    /**
     * 条件组信息，一个条件组包含多个条件，一个条件可能由一个条件组或者一个条件字段组成
     */
    private List<ConditionInfo> conditionInfoList;
    /**
     * 条件值来源于方法参数或者方法参数实体，需要提前计算出公共取值路径，模板渲染的时候就不再需要多重逻辑判断
     */
    private List<String> paramValueCommonPathItemList;

    public ConditionInfo(Integer index, ConditionOriginType conditionOriginType) {
        this.index = index;
        this.conditionOriginType = conditionOriginType;
    }

    public ConditionInfo(Integer index, ConditionOriginType conditionOriginType, MethodParamInfo methodParamInfo) {
        this.index = index;
        this.conditionOriginType = conditionOriginType;
        this.methodParamInfo = methodParamInfo;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getOriginSegment() {
        return originSegment;
    }

    public void setOriginSegment(String originSegment) {
        this.originSegment = originSegment;
    }

    public LogicOperator getLogicOperator() {
        return logicOperator;
    }

    public void setLogicOperator(LogicOperator logicOperator) {
        this.logicOperator = logicOperator;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public ComparisonOperator getComparisonNotOperator() {
        return comparisonNotOperator;
    }

    public void setComparisonNotOperator(ComparisonOperator comparisonNotOperator) {
        this.comparisonNotOperator = comparisonNotOperator;
    }

    public ComparisonOperator getComparisonOperator() {
        return comparisonOperator;
    }

    public void setComparisonOperator(ComparisonOperator comparisonOperator) {
        this.comparisonOperator = comparisonOperator;
    }

    public ConditionOriginType getConditionOriginType() {
        return conditionOriginType;
    }

    public void setConditionOriginType(ConditionOriginType conditionOriginType) {
        this.conditionOriginType = conditionOriginType;
    }

    public MethodParamInfo getMethodParamInfo() {
        return methodParamInfo;
    }

    public void setMethodParamInfo(MethodParamInfo methodParamInfo) {
        this.methodParamInfo = methodParamInfo;
    }

    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }

    public void setColumnInfo(ColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
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

    public List<ConditionInfo> getConditionInfoList() {
        return conditionInfoList;
    }

    public void setConditionInfoList(List<ConditionInfo> conditionInfoList) {
        this.conditionInfoList = conditionInfoList;
    }

    public List<String> getParamValueCommonPathItemList() {
        return paramValueCommonPathItemList;
    }

    public void setParamValueCommonPathItemList(List<String> paramValueCommonPathItemList) {
        this.paramValueCommonPathItemList = paramValueCommonPathItemList;
    }
}
