package com.mybatisgx.method.validator.param.dao.select.entity;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SelectEntityDao extends CurdDao<ValidatorUser, Long> {

    List<ValidatorUser> findByName(ValidatorUser user);
}
