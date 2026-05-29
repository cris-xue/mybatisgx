package com.mybatisgx.relation.select.batch_complex_id.manytomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.EmbeddedIdBaseEntity;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Entity
@Table(name = "batch_mtm_user_complex")
public class User extends EmbeddedIdBaseEntity<Long> {

    private String code;

    @ManyToMany(mappedBy = "userList", fetch = FetchType.LAZY)
    @Fetch(FetchMode.BATCH)
    private List<Role> roleList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}