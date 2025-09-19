package com.mybatisgx.manytomany.entity;

import com.lc.mybatisx.annotation.Entity;
import com.lc.mybatisx.annotation.Table;
import com.mybatisgx.entity.IdBaseEntity;

@Entity
@Table(name = "user_role_simple")
public class UserRole extends IdBaseEntity<Long> {

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
