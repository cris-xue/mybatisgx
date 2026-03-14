package com.mybatisgx.method.validator.param.dao.delete.entity;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.delete.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeleteEntityDao extends CurdDao<ValidatorUser, Long> {

    int deleteByName(ValidatorUser user);
}
