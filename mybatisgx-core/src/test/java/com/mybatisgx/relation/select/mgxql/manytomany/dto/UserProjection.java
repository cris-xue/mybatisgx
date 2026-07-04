package com.mybatisgx.relation.select.mgxql.manytomany.dto;

import java.util.List;

/**
 * 用户投影 DTO（一层嵌套：包含 RoleProjection menuList）
 *
 * @author ccxuef
 * @date 2026/7/1
 */
public class UserProjection {

    private String code;

    private List<RoleProjection> roleList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<RoleProjection> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<RoleProjection> roleList) {
        this.roleList = roleList;
    }
}
