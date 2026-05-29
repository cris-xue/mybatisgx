package com.mybatisgx.relation.select.join_simple_id.onetomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Entity
@Table(name = "join_otm_team_simple")
public class Team extends BaseEntity<Long> {

    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dept_id")
    @Fetch(FetchMode.JOIN)
    private Dept dept;

    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
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
