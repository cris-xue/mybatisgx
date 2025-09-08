package com.mybatisgx.simple.entity;

import com.lc.mybatisx.annotation.*;
import com.mybatisgx.entity.BaseEntity;
import com.mybatisgx.onetoone.entity.User;

@Entity
@Table(name = "user_detail")
public class UserDetail extends BaseEntity<Long> {

    private String code;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @Fetch
    private com.mybatisgx.onetoone.entity.User user;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public com.mybatisgx.onetoone.entity.User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
