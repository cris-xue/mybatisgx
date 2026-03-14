package com.mybatisgx.method.validator.param.dao.update.both;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import com.mybatisgx.method.validator.param.entity.ValidatorUserQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UpdateBothDao extends CurdDao<ValidatorUser, Long> {

    int updateByName(ValidatorUser user, ValidatorUserQuery query);
}
