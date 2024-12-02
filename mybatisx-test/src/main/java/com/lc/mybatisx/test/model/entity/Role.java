package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.*;

import java.util.List;

@Entity
@Table(name = "role")
public class Role extends BaseEntity<Long> {

    @Id
    private Long tenantId;

    private String name;

    private String code;

    @ManyToMany
    @JoinTable(
            name = UserRole.class,
            joinColumns = {@JoinColumn(name = "roleId", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "userId", referencedColumnName = "id")}
    )
    private List<User> userList;

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

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
