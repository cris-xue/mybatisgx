package com.mybatisgx.method.validator.param.dao.insert.entity;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.insert.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InsertEntityDao extends CurdDao<ValidatorUser, Long> {

    int insertValid(ValidatorUser user);
}
