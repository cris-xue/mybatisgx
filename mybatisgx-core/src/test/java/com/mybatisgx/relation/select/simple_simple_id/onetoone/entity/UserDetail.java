package com.mybatisgx.relation.select.simple_simple_id.onetoone.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;
import org.apache.ibatis.mapping.FetchType;

@Entity
@Table(name = "simple_oto_user_detail_simple")
public class UserDetail extends BaseEntity<Long> {

    private String code;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @Fetch(FetchMode.SIMPLE)
    private User user;

    @OneToOne(mappedBy = "userDetail", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SIMPLE)
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
