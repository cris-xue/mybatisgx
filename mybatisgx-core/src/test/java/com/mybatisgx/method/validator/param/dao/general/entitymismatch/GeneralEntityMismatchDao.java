package com.mybatisgx.method.validator.param.dao.general.entitymismatch;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import com.mybatisgx.method.validator.param.wrongentity.WrongUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GeneralEntityMismatchDao extends CurdDao<ValidatorUser, Long> {

    int deleteByWrong(WrongUser user);
}
