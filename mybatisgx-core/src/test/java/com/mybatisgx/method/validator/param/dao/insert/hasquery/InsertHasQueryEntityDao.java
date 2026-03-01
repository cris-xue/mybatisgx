package com.mybatisgx.method.validator.param.dao.insert.hasquery;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import com.mybatisgx.method.validator.param.entity.ValidatorUserQuery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InsertHasQueryEntityDao extends CurdDao<ValidatorUser, Long> {

    int insertHasQueryEntity(ValidatorUser user, ValidatorUserQuery query);
}
