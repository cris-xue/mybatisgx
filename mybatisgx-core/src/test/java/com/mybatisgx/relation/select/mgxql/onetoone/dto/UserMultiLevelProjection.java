package com.mybatisgx.relation.select.mgxql.onetoone.dto;

/**
 * 多层嵌套投影 DTO（User → UserDetail → UserDetailItem1 → UserDetailItem2）
 *
 * @author ccxuef
 * @date 2026/7/1
 */
public class UserMultiLevelProjection {

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
