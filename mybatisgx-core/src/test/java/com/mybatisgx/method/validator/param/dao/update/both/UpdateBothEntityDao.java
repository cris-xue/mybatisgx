package com.mybatisgx.method.validator.param.dao.update.both;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import com.mybatisgx.method.validator.param.entity.ValidatorUserQuery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UpdateBothEntityDao extends SimpleDao<ValidatorUser, ValidatorUserQuery, Long> {

    int updateByNameLike(ValidatorUser user, ValidatorUserQuery query);
}
