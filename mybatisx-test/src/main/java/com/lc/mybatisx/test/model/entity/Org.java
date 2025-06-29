package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.*;

import java.util.List;

@Entity
@Table(name = "org")
public class Org extends BaseEntity<Long> {

    // 自引用字段
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Org parent;

    private String name;

    private String code;

    @OneToMany(mappedBy = "parent")
    private List<Org> children;

    public Org getParent() {
        return parent;
    }

    public void setParent(Org parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Org> getChildren() {
        return children;
    }

    public void setChildren(List<Org> children) {
        this.children = children;
    }
}
