package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.Entity;
import com.lc.mybatisx.annotation.JoinColumn;
import com.lc.mybatisx.annotation.OneToOne;
import com.lc.mybatisx.annotation.Table;

@Entity
@Table(name = "user_detail")
public class UserDetail extends BaseEntity<Long> {

    private String code;

    @OneToOne
    /*@JoinColumns(
            value = {
                    @JoinColumn(name = "user_code", referencedColumnName = "code"),
                    @JoinColumn(name = "user_status", referencedColumnName = "status"),
            }
    )*/
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
