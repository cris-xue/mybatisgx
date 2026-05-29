package com.mybatisgx.relation.select.join_complex_id.manytomany.entity;

import com.mybatisgx.annotation.Entity;
import com.mybatisgx.annotation.Table;
import com.mybatisgx.entity.IdBaseEntity;

@Entity
@Table(name = "join_mtm_user_role_complex")
public class UserRoleJoin extends IdBaseEntity<Long> {

    private Long userId1;

    private Long userId2;

    private Long roleId1;

    private Long roleId2;

    public Long getUserId1() {
        return userId1;
    }

    public void setUserId1(Long userId1) {
        this.userId1 = userId1;
    }

    public Long getUserId2() {
        return userId2;
    }

    public void setUserId2(Long userId2) {
        this.userId2 = userId2;
    }

    public Long getRoleId1() {
        return roleId1;
    }

    public void setRoleId1(Long roleId1) {
        this.roleId1 = roleId1;
    }

    public Long getRoleId2() {
        return roleId2;
    }

    public void setRoleId2(Long roleId2) {
        this.roleId2 = roleId2;
    }
}
