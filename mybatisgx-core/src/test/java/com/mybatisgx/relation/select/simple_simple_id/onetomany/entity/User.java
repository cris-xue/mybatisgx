package com.mybatisgx.relation.select.simple_simple_id.onetomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;
import org.apache.ibatis.mapping.FetchType;

@Entity
@Table(name = "simple_otm_user_simple")
public class User extends BaseEntity<Long> {

    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    @Fetch(FetchMode.SIMPLE)
    private Team team;

    public User() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
