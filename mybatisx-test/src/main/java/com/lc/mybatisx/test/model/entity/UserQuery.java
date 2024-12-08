package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.ConditionEntity;

import javax.persistence.Table;

@ConditionEntity
@Table(name = "user")
public class UserQuery extends User {

    private String nameLt;

}
