package com.mybatisgx.method.validator.param.dao.general.querymismatch;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import com.mybatisgx.method.validator.param.wrongentity.WrongUserQuery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GeneralQueryMismatchDao extends CurdDao<ValidatorUser, Long> {

    int deleteByWrongQuery(WrongUserQuery query);
}
