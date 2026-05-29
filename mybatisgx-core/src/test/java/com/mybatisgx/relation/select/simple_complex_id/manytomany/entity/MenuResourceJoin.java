package com.mybatisgx.relation.select.simple_complex_id.manytomany.entity;

import com.mybatisgx.annotation.Entity;
import com.mybatisgx.annotation.Table;
import com.mybatisgx.entity.IdBaseEntity;

@Entity
@Table(name = "simple_mtm_menu_resource_complex")
public class MenuResourceJoin extends IdBaseEntity<Long> {

    private Long menuId1;

    private Long menuId2;

    private Long resourceId1;

    private Long resourceId2;

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

    public Long getResourceId1() {
        return resourceId1;
    }

    public void setResourceId1(Long resourceId1) {
        this.resourceId1 = resourceId1;
    }

    public Long getResourceId2() {
        return resourceId2;
    }

    public void setResourceId2(Long resourceId2) {
        this.resourceId2 = resourceId2;
    }
}
