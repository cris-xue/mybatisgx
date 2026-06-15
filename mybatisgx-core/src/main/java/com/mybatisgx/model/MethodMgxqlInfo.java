package com.mybatisgx.model;

import org.apache.ibatis.mapping.SqlCommandType;

import java.util.ArrayList;
import java.util.List;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2026/6/9 13:58
 */
@Deprecated
public class MethodMgxqlInfo {

    /**
     * mgxql来源
     */
    private MgxqlSourceType mgxqlSourceType;
    /**
     * sql动作，insert、delete、update、select
     */
    private SqlCommandType sqlCommandType;
    /**
     * 语义表达式
     */
    private String dsl;
    /**
     * 查询节点信息
     */
    private SelectItemInfo selectItemInfo;
    /**
     * 查询排序信息
     */
    private List<SelectOrderByInfo> selectOrderByInfoList;
    /**
     * 查询数量限制
     */
    private MethodRowLimitInfo methodRowLimitInfo;
    /**
     * 方法名条件信息【修改、删除、查询都可以存在条件】
     */
    private List<ConditionInfo> conditionInfoList = new ArrayList<>();
    /**
     * FROM 子句信息
     */
    private FromInfo fromInfo;
    /**
     * GROUP BY 字段列表
     */
    private List<GroupByFieldInfo> groupByInfoList;
    /**
     * HAVING 条件列表
     */
    private List<HavingInfo> havingInfoList;

    public MgxqlSourceType getMgxqlSourceType() {
        return mgxqlSourceType;
    }

    public void setMgxqlSourceType(MgxqlSourceType mgxqlSourceType) {
        this.mgxqlSourceType = mgxqlSourceType;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }

    public String getDsl() {
        return dsl;
    }

    public void setDsl(String dsl) {
        this.dsl = dsl;
    }

    public SelectItemInfo getSelectItemInfo() {
        return selectItemInfo;
    }

    public void setSelectItemInfo(SelectItemInfo selectItemInfo) {
        this.selectItemInfo = selectItemInfo;
    }

    public List<SelectOrderByInfo> getSelectOrderByInfoList() {
        return selectOrderByInfoList;
    }

    public void setSelectOrderByInfoList(List<SelectOrderByInfo> selectOrderByInfoList) {
        this.selectOrderByInfoList = selectOrderByInfoList;
    }

    public MethodRowLimitInfo getMethodRowLimitInfo() {
        return methodRowLimitInfo;
    }

    public void setMethodRowLimitInfo(MethodRowLimitInfo methodRowLimitInfo) {
        this.methodRowLimitInfo = methodRowLimitInfo;
    }

    public List<ConditionInfo> getConditionInfoList() {
        return conditionInfoList;
    }

    public void setConditionInfoList(List<ConditionInfo> conditionInfoList) {
        this.conditionInfoList = conditionInfoList;
    }

    public FromInfo getFromInfo() {
        return fromInfo;
    }

    public void setFromInfo(FromInfo fromInfo) {
        this.fromInfo = fromInfo;
    }

    public List<GroupByFieldInfo> getGroupByInfoList() {
        return groupByInfoList;
    }

    public void setGroupByInfoList(List<GroupByFieldInfo> groupByInfoList) {
        this.groupByInfoList = groupByInfoList;
    }

    public List<HavingInfo> getHavingInfoList() {
        return havingInfoList;
    }

    public void setHavingInfoList(List<HavingInfo> havingInfoList) {
        this.havingInfoList = havingInfoList;
    }
}
