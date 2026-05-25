package com.mybatisgx.relation.select.join_complex_id.onetoone.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.EmbeddedIdBaseEntity;
import org.apache.ibatis.mapping.FetchType;

@Entity
@Table(name = "jci_oto_user")
public class User extends EmbeddedIdBaseEntity<Long> {

    private String code;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
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
