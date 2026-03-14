package com.mybatisgx.method.validator.param.dao.update.entity;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UpdateEntityDao extends CurdDao<ValidatorUser, Long> {

    int updateByName(ValidatorUser user, @Param("name") String name);
}
