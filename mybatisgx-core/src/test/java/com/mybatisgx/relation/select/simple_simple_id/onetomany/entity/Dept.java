package com.mybatisgx.relation.select.simple_simple_id.onetomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Entity
@Table(name = "simple_otm_dept_simple")
public class Dept extends BaseEntity<Long> {

    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "org_id")
    @Fetch(FetchMode.SIMPLE)
    private Org org;

    @OneToMany(mappedBy = "dept", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SIMPLE)
    private List<Team> teamList;

    public Dept() {
    }

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

    public List<Team> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<Team> teamList) {
        this.teamList = teamList;
    }
}
