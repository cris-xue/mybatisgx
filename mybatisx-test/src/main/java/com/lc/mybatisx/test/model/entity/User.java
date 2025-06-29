package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.*;
import com.lc.mybatisx.test.commons.handler.ListLongTypeHandler;

import java.util.List;

@Entity
@Table(name = "user")
public class User extends BaseEntity<Long> {

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
     * 关联查询，有可能外键是联合外键，所以外键需要是数组
     * <select id="aaaaa">
     * select role.* form
     * user_role.user_id left join role role on user_role.role_id = role.id
     * where user.user_id = id
     * </select>
     */
    /*@ManyToMany(mappedBy = "userList")
    private List<Role> roleList;*/

    @OneToMany(mappedBy = "user", fetchSize = 100)
    private List<Order> orderList;

    @OneToOne(mappedBy = "user")
    private UserDetail userDetail;

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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    /*public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }*/

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }
}
