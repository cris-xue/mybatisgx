package com.mybatisgx.relation.select.onetomany.entity;

import com.lc.mybatisx.annotation.*;
import com.mybatisgx.entity.IdBaseEntity;

import javax.persistence.FetchType;
import java.util.List;

@Entity
@Table(name = "test_org_complex")
public class Org extends IdBaseEntity<Integer> {

    private String code;

    @Fetch
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Org parent;

    @Fetch
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
