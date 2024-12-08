package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.Entity;
import com.lc.mybatisx.annotation.Table;

@Entity
@Table(name = "role")
public class Role extends BaseEntity<Long> {

    private String name;

    private String code;

    // 多对多关系，学生可以选修多个课程
    /*@ManyToMany
    @JoinTable(
            name = "user_role",  // 中间表的名称
            joinColumns = @JoinColumn(name = "user_id"),  // 外键指向学生
            inverseJoinColumns = @JoinColumn(name = "role_id")  // 外键指向课程
    )
    private List<User> userList;*/

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
}
