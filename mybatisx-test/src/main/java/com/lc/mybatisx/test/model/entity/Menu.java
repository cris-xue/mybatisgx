package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.*;

import java.util.List;

@Entity
@Table(name = "menu")
public class Menu extends BaseEntity<Long> {

    private String name;

    private String code;

    @Fetch
    @ManyToMany
    @JoinTable(
            name = "role_menu",  // 中间表的名称
            joinColumns = @JoinColumn(name = "menu_id"),  // 外键指向学生
            inverseJoinColumns = @JoinColumn(name = "role_id")  // 外键指向课程
    )
    private List<Role> roleList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}
