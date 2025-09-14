package com.mybatisgx.manytomany.entity;

import com.lc.mybatisx.annotation.*;
import com.mybatisgx.entity.BaseEntity;

import javax.persistence.FetchType;
import java.util.List;

@Entity
@Table(name = "role_simple")
public class Role extends BaseEntity<Long> {

    private String code;

    private String name;

    @LogicDelete
    private Integer status;

    @Lock
    private Integer version;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role_simple",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Fetch
    private List<User> userList;

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
