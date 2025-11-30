package com.mybatisgx.relation.select.join.onetomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;

import javax.persistence.FetchType;

@Entity
@Table(name = "join_otm_user")
public class User extends BaseEntity<Long> {

    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "org_id")
    private Org org;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }
}
