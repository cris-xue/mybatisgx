package com.mybatisgx.onetoone.entity;

import com.lc.mybatisx.annotation.*;
import com.mybatisgx.entity.EmbeddedIdBaseEntity;

import javax.persistence.FetchType;

@Entity
@Table(name = "user_detail_complex")
public class UserDetail extends EmbeddedIdBaseEntity<Long> {

    private String code;

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "user_id1", referencedColumnName = "id1"),
            @JoinColumn(name = "user_id2", referencedColumnName = "id2")
    })
    @Fetch(FetchMode.JOIN)
    private User user;

    @OneToOne(mappedBy = "userDetail", fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    private UserDetailItem1 userDetailItem1;

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

    public UserDetailItem1 getUserDetailItem1() {
        return userDetailItem1;
    }

    public void setUserDetailItem1(UserDetailItem1 userDetailItem1) {
        this.userDetailItem1 = userDetailItem1;
    }
}
