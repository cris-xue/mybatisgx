package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.*;

@Entity
@Table(name = "order_test")
public class Order extends BaseEntity<Long> {

    private String name;

    private String code;

    @Fetch
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @LogicDelete
    private Integer status;

    @Lock
    private Integer version;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
