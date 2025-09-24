package com.lc.mybatisx.model;

/**
 * 多对多批量查询结果映射信息
 * @author ccxuef
 * @date 2025/9/22 20:46
 */
public class ManyToManyBatchSelectResultMapInfo extends ResultMapInfo {

    private EntityInfo mainEntityInfo;

    public EntityInfo getMainEntityInfo() {
        return mainEntityInfo;
    }

    public void setMainEntityInfo(EntityInfo mainEntityInfo) {
        this.mainEntityInfo = mainEntityInfo;
    }
}
