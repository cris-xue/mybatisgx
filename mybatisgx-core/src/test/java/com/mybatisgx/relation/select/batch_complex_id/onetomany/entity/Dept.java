package com.mybatisgx.relation.select.batch_complex_id.onetomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.EmbeddedIdBaseEntity;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Entity
@Table(name = "bci_otm_dept")
public class Dept extends EmbeddedIdBaseEntity<Long> {

    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "org_id1", referencedColumnName = "id1"),
            @JoinColumn(name = "org_id2", referencedColumnName = "id2")
    })
    @Fetch(FetchMode.BATCH)
    private Org org;

    @OneToMany(mappedBy = "dept", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
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