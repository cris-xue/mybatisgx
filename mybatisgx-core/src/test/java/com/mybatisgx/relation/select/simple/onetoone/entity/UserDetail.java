package com.mybatisgx.relation.select.simple.onetoone.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.EmbeddedIdBaseEntity;

import javax.persistence.FetchType;

@Entity
@Table(name = "user_detail_complex_rl_sp")
public class UserDetail extends EmbeddedIdBaseEntity<Long> {

    private String code;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "user_id1", referencedColumnName = "id1"),
            @JoinColumn(name = "user_id2", referencedColumnName = "id2")
    })
    @Fetch(FetchMode.SIMPLE)
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
