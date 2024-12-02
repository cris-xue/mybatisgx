package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.Entity;
import com.lc.mybatisx.annotation.JoinColumn;
import com.lc.mybatisx.annotation.ManyToOne;
import com.lc.mybatisx.annotation.Table;

@Entity
@Table(name = "order")
public class Order extends BaseEntity<Long> {

    private String name;

    private String code;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

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
}
