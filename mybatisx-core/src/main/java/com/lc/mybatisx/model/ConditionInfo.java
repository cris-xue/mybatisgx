package com.lc.mybatisx.model;

import java.util.List;

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
    private LogicOperator logicOperator;
    /**
     * 条件字段名，如：findByNameAndUserId，条件名为name、userId
     */
    private String columnName;
    /**
     * 比较操作符【=、<=、!=】
     */
    private ComparisonOperator comparisonOperator = ComparisonOperator.EQ;
    /**
     * 条件来源
     */
    private ConditionOriginType conditionOriginType = ConditionOriginType.METHOD_NAME;
    /**
     * 参数名，如：param1、param2、param3。如：arg0、arg1、arg2 。有@Param则使用@Param中的值
     */
    private List<String> methodParamNameList;
    /**
     * 条件绑定的实体中的字段信息
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
    private ConditionGroupInfo conditionGroupInfo;

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

    public List<String> getMethodParamNameList() {
        return methodParamNameList;
    }

    public void setMethodParamNameList(List<String> methodParamNameList) {
        this.methodParamNameList = methodParamNameList;
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

    public ConditionGroupInfo getConditionGroupInfo() {
        return conditionGroupInfo;
    }

    public void setConditionGroupInfo(ConditionGroupInfo conditionGroupInfo) {
        this.conditionGroupInfo = conditionGroupInfo;
    }
}
