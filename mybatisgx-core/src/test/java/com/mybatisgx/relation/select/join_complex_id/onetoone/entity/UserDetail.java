package com.mybatisgx.relation.select.join_complex_id.onetoone.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.EmbeddedIdBaseEntity;

import org.apache.ibatis.mapping.FetchType;

@Entity
@Table(name = "jci_oto_user_detail")
public class UserDetail extends EmbeddedIdBaseEntity<Long> {

    private String code;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "user_id1", referencedColumnName = "id1"),
            @JoinColumn(name = "user_id2", referencedColumnName = "id2")
    })
    @Fetch(FetchMode.JOIN)
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
