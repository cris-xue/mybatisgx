package com.mybatisgx.method.validator.param.dao.update.query;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import com.mybatisgx.method.validator.param.entity.ValidatorUserQuery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UpdateQueryEntityDao extends CurdDao<ValidatorUser, Long> {

    int updateByName(ValidatorUserQuery query);
}
