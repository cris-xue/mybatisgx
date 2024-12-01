package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.Entity;
import com.lc.mybatisx.annotation.Table;

@Entity
@Table(name = "user_role")
public class UserRole extends BaseEntity<Long> {

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
