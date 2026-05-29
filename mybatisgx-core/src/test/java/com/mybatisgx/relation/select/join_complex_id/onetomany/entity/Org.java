package com.mybatisgx.relation.select.join_complex_id.onetomany.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.EmbeddedIdBaseEntity;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Entity
@Table(name = "join_otm_org_complex")
public class Org extends EmbeddedIdBaseEntity<Long> {

    private String code;

    @OneToMany(mappedBy = "org", fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    private List<Dept> deptList;

    public Org() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Dept> getDeptList() {
        return deptList;
    }

    public void setDeptList(List<Dept> deptList) {
        this.deptList = deptList;
    }
}
