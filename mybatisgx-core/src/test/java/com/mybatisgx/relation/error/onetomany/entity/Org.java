package com.mybatisgx.relation.error.onetomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.IdBaseEntity;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Entity
@Table(name = "join_otm_org")
public class Org extends IdBaseEntity<Integer> {

    private String code;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "org_id")
    @Fetch(FetchMode.SIMPLE)
    private List<User> userList;

    /*@Fetch(FetchMode.SIMPLE)
    @OneToMany(mappedBy = "org", fetch = FetchType.EAGER)
    private List<User> userList;*/

    public Org() {
    }

    public Org(Integer id, String code) {
        this.id = id;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
