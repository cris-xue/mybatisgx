package com.mybatisgx.relation.select.join_simple_id.onetoone.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;
import org.apache.ibatis.mapping.FetchType;

@Entity
@Table(name = "jsi_oto_user_detail")
public class UserDetail extends BaseEntity<Long> {
    private String code;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @Fetch(FetchMode.JOIN)
    private User user;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
