package com.lc.mybatisx.model;

/**
 * 多对多批量查询结果映射信息
 * @author ccxuef
 * @date 2025/9/22 20:46
 */
public class ManyToManyBatchSelectResultMapInfo extends ResultMapInfo {

    private EntityInfo mainEntityInfo;

    private ResultMapInfo manyToManyBatchSelectResultMapInfo;

    public EntityInfo getMainEntityInfo() {
        return mainEntityInfo;
    }

    public void setMainEntityInfo(EntityInfo mainEntityInfo) {
        this.mainEntityInfo = mainEntityInfo;
    }

    public ResultMapInfo getManyToManyBatchSelectResultMapInfo() {
        return manyToManyBatchSelectResultMapInfo;
    }

    public void setManyToManyBatchSelectResultMapInfo(ResultMapInfo manyToManyBatchSelectResultMapInfo) {
        this.manyToManyBatchSelectResultMapInfo = manyToManyBatchSelectResultMapInfo;
    }
}
