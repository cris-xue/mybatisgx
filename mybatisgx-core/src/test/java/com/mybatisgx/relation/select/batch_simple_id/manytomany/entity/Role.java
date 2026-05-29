package com.mybatisgx.relation.select.batch_simple_id.manytomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Entity
@Table(name = "batch_mtm_role_simple")
public class Role extends BaseEntity<Long> {

    private String code;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "batch_mtm_user_role_simple",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Fetch(FetchMode.BATCH)
    private List<User> userList;

    @ManyToMany(mappedBy = "roleList", fetch = FetchType.LAZY)
    @Fetch(FetchMode.BATCH)
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

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }
}
