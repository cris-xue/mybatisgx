package com.lc.mybatisx.test.model.dto;

import java.util.List;

public class RoleDto extends BaseDto<Long> {

    private String name;

    private String code;

    private List<UserDto> userList;

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

    public List<UserDto> getUserList() {
        return userList;
    }

    public void setUserList(List<UserDto> userList) {
        this.userList = userList;
    }
}
