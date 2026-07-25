package com.mybatisgx.relation.select.simple_simple_id.onetoone.entity;

import com.mybatisgx.annotation.QueryColumn;
import com.mybatisgx.annotation.QueryEntity;

import java.util.List;

/**
 * MGXQL 实体参数端到端测试查询实体
 *
 * @author 薛承城
 * @date 2026/7/24
 */
@QueryEntity(User.class)
public class MgxqlEntityParamUserQuery extends User {

    @QueryColumn(ignore = true)
    private List<Long> idIn;

    @QueryColumn(ignore = true)
    private List<User> userList;

    public List<Long> getIdIn() {
        return idIn;
    }

    public void setIdIn(List<Long> idIn) {
        this.idIn = idIn;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
