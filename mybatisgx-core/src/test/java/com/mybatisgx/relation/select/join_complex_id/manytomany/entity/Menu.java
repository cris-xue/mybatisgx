package com.mybatisgx.relation.select.join_complex_id.manytomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.EmbeddedIdBaseEntity;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Entity
@Table(name = "join_mtm_menu_complex")
public class Menu extends EmbeddedIdBaseEntity<Long> {

    private String code;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "join_mtm_role_menu_complex",
            joinColumns = {
                    @JoinColumn(name = "menu_id1", referencedColumnName = "id1"),
                    @JoinColumn(name = "menu_id2", referencedColumnName = "id2")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "role_id1", referencedColumnName = "id1"),
                    @JoinColumn(name = "role_id2", referencedColumnName = "id2")
            }
    )
    @Fetch(FetchMode.JOIN)
    private List<Role> roleList;

    @ManyToMany(mappedBy = "menuList", fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    private List<Resource> resourceList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public List<Resource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }
}
