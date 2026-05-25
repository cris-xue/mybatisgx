package com.mybatisgx.relation.select.batch_simple_id.onetoone.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;
import org.apache.ibatis.mapping.FetchType;

@Entity
@Table(name = "bsi_oto_user")
public class User extends BaseEntity<Long> {

    private String code;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
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
