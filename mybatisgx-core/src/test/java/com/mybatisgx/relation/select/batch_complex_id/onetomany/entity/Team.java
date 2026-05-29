package com.mybatisgx.relation.select.batch_complex_id.onetomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.EmbeddedIdBaseEntity;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Entity
@Table(name = "batch_otm_team_complex")
public class Team extends EmbeddedIdBaseEntity<Long> {

    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "dept_id1", referencedColumnName = "id1"),
            @JoinColumn(name = "dept_id2", referencedColumnName = "id2")
    })
    @Fetch(FetchMode.BATCH)
    private Dept dept;

    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private List<User> userList;

    public Team() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Dept getDept() {
        return dept;
    }

    public void setDept(Dept dept) {
        this.dept = dept;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}