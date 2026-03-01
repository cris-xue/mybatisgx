package com.mybatisgx.method.validator.param.dao.update.onlyquery;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import com.mybatisgx.method.validator.param.entity.ValidatorUserQuery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UpdateOnlyQueryEntityDao extends CurdDao<ValidatorUser, Long> {

    int updateByIdEq(ValidatorUserQuery query);
}
