package com.mybatisgx.method.validator.param.dao.delete.both;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import com.mybatisgx.method.validator.param.entity.ValidatorUserQuery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeleteBothEntityDao extends CurdDao<ValidatorUser, Long> {

    int deleteByNameLike(ValidatorUser user, ValidatorUserQuery query);
}
