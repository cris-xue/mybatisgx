package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.ConditionEntity;

import javax.persistence.Table;
import java.util.List;

@ConditionEntity
@Table(name = "user")
public class UserQuery extends User {

    private String nameLike;

    // @JoinColumn("id")
    private List<Role> roleList;

}
