package com.mybatisgx.relation.select.join.manytomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;

import javax.persistence.FetchType;
import java.util.List;

@Entity
@Table(name = "join_mtm_role")
public class Role extends BaseEntity<Long> {

    private String code;

    private String name;

    @LogicDelete
    private Integer status;

    @Lock
    private Integer version;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "join_mtm_user_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Fetch(FetchMode.JOIN)
    private List<User> userList;

    @ManyToMany(mappedBy = "roleList", fetch = FetchType.EAGER)
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
