package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.*;

@Entity
@Table(name = "user_detail")
public class UserDetail extends BaseEntity<Long> {

    @Id
    private Long tenantId;

    private String name;

    private Integer age;

    private String phone;

    @OneToOne(
            junctionEntity = UserDetail.class,
            joinEntity = User.class,
            foreignKeys = {@ForeignKey(name = "userId", referencedColumnName = "id")}
    )
    @ForeignKey(name = "user_id")
    private User user;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
