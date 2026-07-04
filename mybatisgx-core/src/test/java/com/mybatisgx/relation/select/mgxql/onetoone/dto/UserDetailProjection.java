package com.mybatisgx.relation.select.mgxql.onetoone.dto;

/**
 * 用户详情投影 DTO（含嵌套字段 userDetailItem1）
 *
 * @author ccxuef
 * @date 2026/7/1
 */
public class UserDetailProjection {

    private String code;

    private UserDetailItem1Projection userDetailItem1;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UserDetailItem1Projection getUserDetailItem1() {
        return userDetailItem1;
    }

    public void setUserDetailItem1(UserDetailItem1Projection userDetailItem1) {
        this.userDetailItem1 = userDetailItem1;
    }
}
