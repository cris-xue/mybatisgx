package com.mybatisgx.relation.select.mgxql.manytomany.dto;

import java.util.List;

/**
 * 跳层投影 DTO（User 直接包含 MenuProjection menuList，跳过 Role）
 *
 * @author ccxuef
 * @date 2026/7/1
 */
public class UserSkipProjection {

    private String code;

    private List<MenuProjection> menuList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<MenuProjection> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<MenuProjection> menuList) {
        this.menuList = menuList;
    }
}
