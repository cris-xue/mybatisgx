package com.mybatisgx.relation.select.batch_complex_id.onetomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.EmbeddedIdBaseEntity;
import org.apache.ibatis.mapping.FetchType;

@Entity
@Table(name = "batch_otm_user_complex")
public class User extends EmbeddedIdBaseEntity<Long> {

    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "team_id1", referencedColumnName = "id1"),
            @JoinColumn(name = "team_id2", referencedColumnName = "id2")
    })
    @Fetch(FetchMode.BATCH)
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