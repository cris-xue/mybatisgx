package com.mybatisgx.relation.select.simple_simple_id.manytomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Entity
@Table(name = "simple_mtm_resource_simple")
public class Resource extends BaseEntity<Long> {

    private String code;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "simple_mtm_menu_resource_simple",
            joinColumns = @JoinColumn(name = "resource_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id")
    )
    @Fetch(FetchMode.SIMPLE)
    private List<Menu> menuList;

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

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }
}
