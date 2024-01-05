package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.Lock;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.annotation.TypeHandler;
import com.lc.mybatisx.test.commons.handler.ListLongTypeHandler;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "user")
public class User extends BaseEntity<Long> {

    @ElementCollection
    @Column(name = "role_ids", columnDefinition = "varchar")
    @TypeHandler(value = ListLongTypeHandler.class)
    private List<Long> roleIds;

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

    /**
     * 关联查询
     */
    // @OneToMany
    // private List<UserRole> userRole;
    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
