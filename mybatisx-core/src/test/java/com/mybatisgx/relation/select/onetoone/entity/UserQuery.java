package com.mybatisgx.relation.select.onetoone.entity;

import com.lc.mybatisx.annotation.QueryEntity;
import com.mybatisgx.entity.MultiId;

@QueryEntity
public class UserQuery extends User {

    private MultiId<Long> code;

}
