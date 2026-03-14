package com.mybatisgx.method.validator.param.dao.select.query;

import com.mybatisgx.annotation.Dynamic;
import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import com.mybatisgx.method.validator.param.entity.ValidatorUserQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SelectQueryEntityDao extends CurdDao<ValidatorUser, Long> {

    @Dynamic
    List<ValidatorUser> findByName(ValidatorUserQuery query);
}
