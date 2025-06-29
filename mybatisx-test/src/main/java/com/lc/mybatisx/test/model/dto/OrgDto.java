package com.lc.mybatisx.test.model.dto;

import com.lc.mybatisx.annotation.Entity;
import com.lc.mybatisx.annotation.Table;

import java.util.List;

@Entity
@Table(name = "org")
public class OrgDto extends BaseDto<Long> {

    private OrgDto parent;

    private String name;

    private String code;

    private List<OrgDto> children;

    public OrgDto getParent() {
        return parent;
    }

    public void setParent(OrgDto parent) {
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

    public List<OrgDto> getChildren() {
        return children;
    }

    public void setChildren(List<OrgDto> children) {
        this.children = children;
    }
}
