package com.mybatisgx.method.validator.param.dao.select.both;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.select.ValidatorUser;
import com.mybatisgx.method.validator.param.entity.select.ValidatorUserQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SelectBothDao extends CurdDao<ValidatorUser, Long> {

    List<ValidatorUser> findByName(ValidatorUser user, ValidatorUserQuery query);
}
