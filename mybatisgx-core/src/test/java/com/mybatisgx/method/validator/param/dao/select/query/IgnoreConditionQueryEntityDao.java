package com.mybatisgx.method.validator.param.dao.select.query;

import com.mybatisgx.annotation.Dynamic;
import com.mybatisgx.annotation.Statement;
import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.select.IgnoreConditionUserQuery;
import com.mybatisgx.method.validator.param.entity.select.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IgnoreConditionQueryEntityDao extends CurdDao<ValidatorUser, Long> {

    @Dynamic
    List<ValidatorUser> findByName(IgnoreConditionUserQuery query);

    @Statement("delete ValidatorUser where name = :startDate")
    int deleteByStartDate(IgnoreConditionUserQuery query);
}
