package com.mybatisgx.model.handler.test.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;

@Entity
@Table(name = "test_user_entity")
public class UserEntity extends BaseEntity<Long> {

    @Column(name = "role_ids")
    private String roleIds;

    private String name;

    @Property(name = "nameEq")
    private String nameEq;

    @Property(name = "nameEq")
    private String nameEqEq;

    private Integer age;

    @Property(name = "ageGt")
    private Integer ageGt;

    @Property(name = "ageGt")
    private Integer ageGtGt;

    private String phone;

    @Property(name = "phoneIn")
    private String phoneIn;

    @Property(name = "phoneIn")
    private String phoneInIn;

    private String email;

    @Property(name = "emailLike")
    private String emailLike;

    @Property(name = "emailLike")
    private String emailLikeLike;

    @Column(name = "user_name")
    private String userName;

    private String password;

    @LogicDelete
    private Integer status;

    @Version
    private Integer version;
}
