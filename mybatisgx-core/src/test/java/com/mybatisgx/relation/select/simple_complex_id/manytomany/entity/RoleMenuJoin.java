package com.mybatisgx.relation.select.simple_complex_id.manytomany.entity;

import com.mybatisgx.annotation.Entity;
import com.mybatisgx.annotation.Table;
import com.mybatisgx.entity.IdBaseEntity;

@Entity
@Table(name = "simple_mtm_role_menu_complex")
public class RoleMenuJoin extends IdBaseEntity<Long> {

    private Long roleId1;

    private Long roleId2;

    private Long menuId1;

    private Long menuId2;

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

    public Long getMenuId1() {
        return menuId1;
    }

    public void setMenuId1(Long menuId1) {
        this.menuId1 = menuId1;
    }

    public Long getMenuId2() {
        return menuId2;
    }

    public void setMenuId2(Long menuId2) {
        this.menuId2 = menuId2;
    }
}
