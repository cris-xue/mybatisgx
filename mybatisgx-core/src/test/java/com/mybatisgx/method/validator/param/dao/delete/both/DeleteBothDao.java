package com.mybatisgx.method.validator.param.dao.delete.both;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import com.mybatisgx.method.validator.param.entity.ValidatorUserQuery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeleteBothDao extends CurdDao<ValidatorUser, Long> {

    int deleteByName(ValidatorUser user, ValidatorUserQuery query);
}
