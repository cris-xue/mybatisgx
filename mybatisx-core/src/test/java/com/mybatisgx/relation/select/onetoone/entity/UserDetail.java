package com.mybatisgx.relation.select.onetoone.entity;

import com.lc.mybatisx.annotation.*;
import com.mybatisgx.entity.EmbeddedIdBaseEntity;

import javax.persistence.FetchType;

@Entity
@Table(name = "user_detail_complex")
public class UserDetail extends EmbeddedIdBaseEntity<Long> {

    private String code;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "user_id1", referencedColumnName = "id1"),
            @JoinColumn(name = "user_id2", referencedColumnName = "id2")
    })
    @Fetch(FetchMode.BATCH)
    private User user;

    @OneToOne(mappedBy = "userDetail", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
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
