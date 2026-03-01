package com.mybatisgx.method.validator.param.dao.insert.success;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InsertSuccessDao extends CurdDao<ValidatorUser, Long> {

    int insertValidEntity(ValidatorUser user);
}
