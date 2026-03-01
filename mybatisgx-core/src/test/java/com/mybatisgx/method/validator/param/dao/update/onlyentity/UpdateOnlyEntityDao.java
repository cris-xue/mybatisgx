package com.mybatisgx.method.validator.param.dao.update.onlyentity;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UpdateOnlyEntityDao extends CurdDao<ValidatorUser, Long> {

    int updateByName(ValidatorUser user);
}
