package com.lc.mybatisx.test.model.dto;

public class UserDetailDto extends BaseDto<Long> {

    private String code;

    private UserDto user;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
