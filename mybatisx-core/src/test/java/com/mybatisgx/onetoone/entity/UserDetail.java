package com.mybatisgx.onetoone.entity;

import com.lc.mybatisx.annotation.*;
import com.mybatisgx.entity.BaseEntity;

@Entity
@Table(name = "user_detail")
public class UserDetail extends BaseEntity<Long> {

    private String code;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @Fetch
    private User user;

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
