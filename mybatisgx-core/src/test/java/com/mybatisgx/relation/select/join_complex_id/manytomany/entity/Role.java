package com.mybatisgx.relation.select.join_complex_id.manytomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.EmbeddedIdBaseEntity;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Entity
@Table(name = "join_mtm_role_complex")
public class Role extends EmbeddedIdBaseEntity<Long> {

    private String code;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "join_mtm_user_role_complex",
            joinColumns = {
                    @JoinColumn(name = "role_id1", referencedColumnName = "id1"),
                    @JoinColumn(name = "role_id2", referencedColumnName = "id2")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "user_id1", referencedColumnName = "id1"),
                    @JoinColumn(name = "user_id2", referencedColumnName = "id2")
            }
    )
    @Fetch(FetchMode.JOIN)
    private List<User> userList;

    @ManyToMany(mappedBy = "roleList", fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
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
