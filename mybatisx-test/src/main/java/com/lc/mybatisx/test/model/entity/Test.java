package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.Version;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "mybatisx")
public class Test extends BaseEntity<Long> {

    private String name;

    private Integer age;

    private String userName;

    private String password;

    private String payStatus;

    @Version
    private int version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
