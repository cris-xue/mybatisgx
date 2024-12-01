package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.Entity;
import com.lc.mybatisx.annotation.ManyToMany;
import com.lc.mybatisx.annotation.Table;

import javax.persistence.FetchType;
import java.util.List;

@Entity
@Table(name = "role")
public class Role extends BaseEntity<Long> {

    private String name;

    private String code;

    @ManyToMany(targetEntity = User.class, associationEntity = UserRole.class, foreignKey = "role_id", inverseForeignKey = "user_id", fetch = FetchType.LAZY)
    private List<User> userList;

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

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
