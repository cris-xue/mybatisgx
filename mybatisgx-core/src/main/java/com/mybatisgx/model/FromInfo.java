package com.mybatisgx.model;

import java.util.ArrayList;
import java.util.List;

/**
 * FROM 子句目标模型，对称保持 primaryEntity + List<JoinInfo>
 *
 * @author 薛承城
 * @date 2026/6/12
 */
@Deprecated
public class FromInfo {

    private EntityRefInfo primaryEntity;

    private List<JoinInfo> joins = new ArrayList<>();

    public EntityRefInfo getPrimaryEntity() {
        return primaryEntity;
    }

    public void setPrimaryEntity(EntityRefInfo primaryEntity) {
        this.primaryEntity = primaryEntity;
    }

    public List<JoinInfo> getJoins() {
        return joins;
    }

    public void setJoins(List<JoinInfo> joins) {
        this.joins = joins;
    }
}
