package com.mybatisgx.relation.select.join_simple_id.onetoone.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;
import org.apache.ibatis.mapping.FetchType;

@Entity
@Table(name = "join_oto_user_detail_item2_simple")
public class UserDetailItem2 extends BaseEntity<Long> {

    private String code;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_detail_item1_id")
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
