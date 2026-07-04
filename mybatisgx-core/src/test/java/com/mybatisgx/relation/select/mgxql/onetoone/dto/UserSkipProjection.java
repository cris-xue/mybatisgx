package com.mybatisgx.relation.select.mgxql.onetoone.dto;

/**
 * 跳层投影 DTO（User 直接包含 userDetailItem2，跳过 UserDetailItem1）
 *
 * @author ccxuef
 * @date 2026/7/1
 */
public class UserSkipProjection {

    private String code;

    private UserDetailItem2Projection userDetailItem2;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UserDetailItem2Projection getUserDetailItem2() {
        return userDetailItem2;
    }

    public void setUserDetailItem2(UserDetailItem2Projection userDetailItem2) {
        this.userDetailItem2 = userDetailItem2;
    }
}
