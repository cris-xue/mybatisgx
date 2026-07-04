package com.mybatisgx.relation.select.mgxql.manytomany.dto;

import java.util.List;

/**
 * 角色投影 DTO
 *
 * @author ccxuef
 * @date 2026/7/1
 */
public class RoleProjection {

    private String code;

    private String name;

    private List<MenuProjection> menuList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MenuProjection> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<MenuProjection> menuList) {
        this.menuList = menuList;
    }
}
