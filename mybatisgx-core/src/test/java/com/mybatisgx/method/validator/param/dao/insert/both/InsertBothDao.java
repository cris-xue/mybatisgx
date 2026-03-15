package com.mybatisgx.method.validator.param.dao.insert.both;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.insert.ValidatorUser;
import com.mybatisgx.method.validator.param.entity.insert.ValidatorUserQuery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InsertBothDao extends CurdDao<ValidatorUser, Long> {

    int insertValid(ValidatorUser user, ValidatorUserQuery query);
}
