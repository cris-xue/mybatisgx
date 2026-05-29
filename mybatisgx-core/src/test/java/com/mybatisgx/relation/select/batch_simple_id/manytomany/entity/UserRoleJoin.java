package com.mybatisgx.relation.select.batch_simple_id.manytomany.entity;

import com.mybatisgx.annotation.Entity;
import com.mybatisgx.annotation.Table;
import com.mybatisgx.entity.IdBaseEntity;

@Entity
@Table(name = "batch_mtm_user_role_simple")
public class UserRoleJoin extends IdBaseEntity<Long> {

    private Long userId;

    private Long roleId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
