package com.mybatisgx.onetoone.entity;

import com.lc.mybatisx.annotation.*;
import com.mybatisgx.entity.EmbeddedIdBaseEntity;

@Entity
@Table(name = "user_detail_item2_complex")
public class UserDetailItem2 extends EmbeddedIdBaseEntity<Long> {

    private String code;

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "user_detail_item1_id1", referencedColumnName = "id1"),
            @JoinColumn(name = "user_detail_item1_id2", referencedColumnName = "id2")
    })
    @Fetch(FetchMode.JOIN)
    private UserDetailItem1 userDetailItem1;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UserDetailItem1 getUserDetailItem1() {
        return userDetailItem1;
    }

    public void setUserDetailItem1(UserDetailItem1 userDetailItem1) {
        this.userDetailItem1 = userDetailItem1;
    }
}
