package com.mybatisgx.onetoone.entity;

import com.lc.mybatisx.annotation.*;
import com.mybatisgx.entity.EmbeddedIdBaseEntity;

import javax.persistence.FetchType;

@Entity
@Table(name = "user_detail_item1_complex")
public class UserDetailItem1 extends EmbeddedIdBaseEntity<Long> {

    private String code;

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "user_detail_id1", referencedColumnName = "id1"),
            @JoinColumn(name = "user_detail_id2", referencedColumnName = "id2")
    })
    @Fetch(FetchMode.JOIN)
    private UserDetail userDetail;

    @OneToOne(mappedBy = "userDetailItem1", fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    private UserDetailItem2 userDetailItem2;

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

    public UserDetailItem2 getUserDetailItem2() {
        return userDetailItem2;
    }

    public void setUserDetailItem2(UserDetailItem2 userDetailItem2) {
        this.userDetailItem2 = userDetailItem2;
    }
}
