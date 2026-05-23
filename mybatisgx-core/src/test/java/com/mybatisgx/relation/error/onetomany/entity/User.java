package com.mybatisgx.relation.error.onetomany.entity;

import com.mybatisgx.annotation.Column;
import com.mybatisgx.annotation.Entity;
import com.mybatisgx.annotation.Table;
import com.mybatisgx.entity.BaseEntity;

@Entity
@Table(name = "join_otm_user")
public class User extends BaseEntity<Long> {

    private String code;

    @Column(name = "org_id")
    private Long orgId;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    @Fetch(FetchMode.NONE)
    private Org org;*/

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    /*public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }*/
}
