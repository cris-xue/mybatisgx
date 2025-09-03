package com.mybatisgx.dao.test.entity;

import com.lc.mybatisx.annotation.*;

@Entity
@Table(name = "test_user")
public class User extends BaseEntity<Long> {

    @Column(name = "role_ids")
    private String roleIds;

    private String name;

    private Integer age;

    private String phone;

    private String email;

    @Column(name = "user_name")
    private String userName;

    private String password;

    @LogicDelete
    private Integer status;

    @Lock
    private Integer version;
}
