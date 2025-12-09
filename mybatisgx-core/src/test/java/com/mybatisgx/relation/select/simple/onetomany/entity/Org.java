package com.mybatisgx.relation.select.simple.onetomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.IdBaseEntity;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Entity
@Table(name = "simple_otm_org")
public class Org extends IdBaseEntity<Integer> {

    private String code;

    @Fetch(FetchMode.SIMPLE)
    @OneToMany(mappedBy = "org", fetch = FetchType.EAGER)
    private List<User> userList;

    @Fetch(FetchMode.SIMPLE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Org parent;

    @Fetch(FetchMode.SIMPLE)
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    private List<Org> children;

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

    public Org getParent() {
        return parent;
    }

    public void setParent(Org parent) {
        this.parent = parent;
    }

    public List<Org> getChildren() {
        return children;
    }

    public void setChildren(List<Org> children) {
        this.children = children;
    }
}
