package com.mybatisgx.dsl.test.entity;

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

    public String getNameEq() {
        return nameEq;
    }

    public void setNameEq(String nameEq) {
        this.nameEq = nameEq;
    }

    public String getNameEqEq() {
        return nameEqEq;
    }

    public void setNameEqEq(String nameEqEq) {
        this.nameEqEq = nameEqEq;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getAgeGt() {
        return ageGt;
    }

    public void setAgeGt(Integer ageGt) {
        this.ageGt = ageGt;
    }

    public Integer getAgeGtGt() {
        return ageGtGt;
    }

    public void setAgeGtGt(Integer ageGtGt) {
        this.ageGtGt = ageGtGt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneIn() {
        return phoneIn;
    }

    public void setPhoneIn(String phoneIn) {
        this.phoneIn = phoneIn;
    }

    public String getPhoneInIn() {
        return phoneInIn;
    }

    public void setPhoneInIn(String phoneInIn) {
        this.phoneInIn = phoneInIn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailLike() {
        return emailLike;
    }

    public void setEmailLike(String emailLike) {
        this.emailLike = emailLike;
    }

    public String getEmailLikeLike() {
        return emailLikeLike;
    }

    public void setEmailLikeLike(String emailLikeLike) {
        this.emailLikeLike = emailLikeLike;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
