package com.lc.mybatisx.model;

import java.util.ArrayList;
import java.util.List;

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
    private String logicOp;
    /**
     * user_name
     */
    private String dbColumnName;
    /**
     * 比较操作符【=、<=、!=】
     */
    private String comparisonOp = "=";
    /**
     * [userName、Name]
     */
    private String javaColumnName;
    /**
     * 是否是条件实体中的字段
     */
    private Boolean conditionEntity = false;
    /**
     * 原始java字段
     */
    private String conditionEntityJavaColumnName;
    /**
     * 方法参数信息列表
     */
    private List<MethodParamInfo> methodParamInfoList = new ArrayList<>();
    /**
     * 条件对应的方法中的参数
     */
    private List<String> paramName = new ArrayList<>();
    /**
     * 条件字段关联的字段信息
     */
    private ColumnInfo columnInfo;
    /**
     * 条件字段关联的id字段信息列表
     */
    private List<IdColumnInfo> idColumnInfoList = new ArrayList();
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

    public String getLogicOp() {
        return logicOp;
    }

    public void setLogicOp(String logicOp) {
        this.logicOp = logicOp;
    }

    public String getDbColumnName() {
        return dbColumnName;
    }

    public void setDbColumnName(String dbColumnName) {
        this.dbColumnName = dbColumnName;
    }

    public String getComparisonOp() {
        return comparisonOp;
    }

    public void setComparisonOp(String comparisonOp) {
        this.comparisonOp = comparisonOp;
    }

    public String getJavaColumnName() {
        return javaColumnName;
    }

    public void setJavaColumnName(String javaColumnName) {
        this.javaColumnName = javaColumnName;
    }

    public Boolean getConditionEntity() {
        return conditionEntity;
    }

    public void setConditionEntity(Boolean conditionEntity) {
        this.conditionEntity = conditionEntity;
    }

    public String getConditionEntityJavaColumnName() {
        return conditionEntityJavaColumnName;
    }

    public void setConditionEntityJavaColumnName(String conditionEntityJavaColumnName) {
        this.conditionEntityJavaColumnName = conditionEntityJavaColumnName;
    }

    public List<MethodParamInfo> getMethodParamInfoList() {
        return methodParamInfoList;
    }

    public void addMethodParamInfo(MethodParamInfo methodParamInfo) {
        this.methodParamInfoList.add(methodParamInfo);
    }

    public void setMethodParamInfoList(List<MethodParamInfo> methodParamInfoList) {
        this.methodParamInfoList = methodParamInfoList;
    }

    public List<String> getParamName() {
        return paramName;
    }

    public void addParamName(String paramName) {
        this.paramName.add(paramName);
    }

    public void setParamName(List<String> paramName) {
        this.paramName = paramName;
    }

    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }

    public void setColumnInfo(ColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
        this.dbColumnName = columnInfo.getDbColumnName();
        this.javaColumnName = columnInfo.getJavaColumnName();
    }

    public List<IdColumnInfo> getIdColumnInfoList() {
        return idColumnInfoList;
    }

    public void setIdColumnInfoList(List<IdColumnInfo> idColumnInfoList) {
        this.idColumnInfoList = idColumnInfoList;
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
