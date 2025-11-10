package com.mybatisgx.model;

import java.util.List;

/**
 * 一句话描述
 *
 * @author 条件组
 * @date 2025/7/20 13:10
 */
public class ConditionGroupInfo {

    /**
     * 条件列表
     */
    private List<ConditionInfo> conditionInfoList;

    public List<ConditionInfo> getConditionInfoList() {
        return conditionInfoList;
    }

    public void setConditionInfoList(List<ConditionInfo> conditionInfoList) {
        this.conditionInfoList = conditionInfoList;
    }
}
