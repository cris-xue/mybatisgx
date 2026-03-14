package com.mybatisgx.method.validator.param.dao.insert.query;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import com.mybatisgx.method.validator.param.entity.ValidatorUserQuery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InsertQueryEntityDao extends CurdDao<ValidatorUser, Long> {

    int insertValid(ValidatorUserQuery query);
}
