package com.mybatisgx.custom.condition.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.custom.condition.test.LogicDeleteIdValueProcessor;
import com.mybatisgx.entity.BaseEntity;

@Entity
@Table(name = "custom_condition_user")
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

    @LogicDeleteId
    @GeneratedValue(value = LogicDeleteIdValueProcessor.class)
    private Long logicDeleteId;

    @Lock
    private Integer version;

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getLogicDeleteId() {
        return logicDeleteId;
    }

    public void setLogicDeleteId(Long logicDeleteId) {
        this.logicDeleteId = logicDeleteId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
