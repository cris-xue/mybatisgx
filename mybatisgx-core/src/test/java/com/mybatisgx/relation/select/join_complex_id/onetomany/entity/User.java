package com.mybatisgx.relation.select.join_complex_id.onetomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;

import org.apache.ibatis.mapping.FetchType;

@Entity
@Table(name = "jci_otm_user")
public class User extends BaseEntity<Long> {

    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
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
