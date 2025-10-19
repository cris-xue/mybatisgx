package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.*;

import java.util.List;

@Entity
@Table(name = "role")
public class Role extends BaseEntity<Long> {

    private String name;

    private String code;

    @Fetch
    @ManyToMany
    @JoinTable(
            name = "user_role",  // 中间表的名称
            joinColumns = @JoinColumn(name = "role_id"),  // 外键指向学生
            inverseJoinColumns = @JoinColumn(name = "user_id")  // 外键指向课程
    )
    private List<User> userList;

    @Fetch
    @ManyToMany(mappedBy = "roleList")
    private List<Menu> menuList;

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
