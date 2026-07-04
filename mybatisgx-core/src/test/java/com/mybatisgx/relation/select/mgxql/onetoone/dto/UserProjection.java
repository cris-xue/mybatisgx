package com.mybatisgx.relation.select.mgxql.onetoone.dto;

public class UserProjection {

    private String code;

    private UserDetailProjection userDetail;

    private UserDetailProjection userDetailProjection;

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

    public UserDetailProjection getUserDetailProjection() {
        return userDetailProjection;
    }

    public void setUserDetailProjection(UserDetailProjection userDetailProjection) {
        this.userDetailProjection = userDetailProjection;
    }
}
