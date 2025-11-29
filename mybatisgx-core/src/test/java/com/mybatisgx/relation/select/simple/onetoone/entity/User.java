package com.mybatisgx.relation.select.simple.onetoone.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.EmbeddedIdBaseEntity;

import javax.persistence.FetchType;

@Entity
@Table(name = "simple_oto_user_complex")
public class User extends EmbeddedIdBaseEntity<Long> {

    private String code;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SIMPLE)
    private UserDetail userDetail;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }
}
