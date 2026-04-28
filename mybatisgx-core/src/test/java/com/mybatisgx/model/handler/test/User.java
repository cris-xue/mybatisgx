package com.mybatisgx.model.handler.test;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;
import com.mybatisgx.relation.select.batch.manytomany.entity.Role;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Entity
@Table(name = "test_user")
public class User extends BaseEntity<Long> {

    @Column(name = "role_ids")
    private String roleIds;

    private String name;

    private String nameEq;

    private Integer age;

    private String phone;

    private String email;

    @Column(name = "user_name")
    private String userName;

    private String password;

    @LogicDelete
    private Integer status;

    @Version
    private Integer version;

    @ManyToMany(mappedBy = "userList", fetch = FetchType.LAZY)
    @Fetch
    private List<Role> roleList;
}
