package com.mybatisgx.relation.select.mgxql.onetoone.dto;

/**
 * 一层嵌套投影 DTO（User → UserDetail）
 *
 * @author ccxuef
 * @date 2026/7/1
 */
public class UserOneLevelProjection {

    private String code;

    private UserDetailProjection userDetail;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UserDetailProjection getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetailProjection userDetail) {
        this.userDetail = userDetail;
    }
}
