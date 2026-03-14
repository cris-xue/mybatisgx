package com.mybatisgx.method.validator.param.dao.delete.mapper;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.MapperUser;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeleteMapperDao extends CurdDao<ValidatorUser, Long> {

    int deleteByName(MapperUser user);
}
