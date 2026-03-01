package com.mybatisgx.method.validator.param.dao.insert.wrongtype;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import com.mybatisgx.method.validator.param.wrongentity.WrongUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InsertWrongTypeDao extends CurdDao<ValidatorUser, Long> {

    int insertWrongType(WrongUser user);
}
