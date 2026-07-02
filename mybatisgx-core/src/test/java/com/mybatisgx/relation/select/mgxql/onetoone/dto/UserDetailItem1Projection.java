package com.mybatisgx.relation.select.mgxql.onetoone.dto;

/**
 * UserDetailItem1 投影 DTO
 *
 * @author ccxuef
 * @date 2026/7/1
 */
public class UserDetailItem1Projection {

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
