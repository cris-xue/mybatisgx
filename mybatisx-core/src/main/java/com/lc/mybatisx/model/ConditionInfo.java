package com.lc.mybatisx.model;

public class ConditionInfo {

    /**
     * 查询条件在方法名中的位置，如findById、findByName，其实位置从0开始
     */
    private Integer index;
    /**
     * AndUserNameEq
     */
    private String origin;
    /**
     * 逻辑操作符【And、Or】
     */
    private LogicOp logicOp;
    /**
     * 条件名，如：findByNameAndUserId，条件名为name、userId
     */
    private String conditionName;
    /**
     * 比较操作符【=、<=、!=】
     */
    private ComparisonOp comparisonOp = ComparisonOp.EQ;
    /**
     * 是否是条件实体中的字段
     */
    private ConditionType conditionType = ConditionType.METHOD_NAME;
    /**
     * 条件实体java字段名称，如nameLike、userIdEq，条件名为name、userId。
     */
    private String conditionEntityJavaColumnName;
    /**
     * 方法参数信息列表
     */
    private MethodParamInfo methodParamInfo;
    /**
     * 条件字段关联的实体中的字段信息
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

    public ConditionInfo() {
    }

    public ConditionInfo(Integer index) {
        this.index = index;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public LogicOp getLogicOp() {
        return logicOp;
    }

    public void setLogicOp(LogicOp logicOp) {
        this.logicOp = logicOp;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public ComparisonOp getComparisonOp() {
        return comparisonOp;
    }

    public void setComparisonOp(ComparisonOp comparisonOp) {
        this.comparisonOp = comparisonOp;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public String getConditionEntityJavaColumnName() {
        return conditionEntityJavaColumnName;
    }

    public void setConditionEntityJavaColumnName(String conditionEntityJavaColumnName) {
        this.conditionEntityJavaColumnName = conditionEntityJavaColumnName;
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

    public ConditionGroupInfo getConditionGroupInfo() {
        return conditionGroupInfo;
    }

    public void setConditionGroupInfo(ConditionGroupInfo conditionGroupInfo) {
        this.conditionGroupInfo = conditionGroupInfo;
    }
}
