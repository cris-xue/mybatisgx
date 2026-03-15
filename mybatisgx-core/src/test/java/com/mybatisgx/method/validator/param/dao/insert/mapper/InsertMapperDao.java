package com.mybatisgx.method.validator.param.dao.insert.mapper;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.insert.MapperUser;
import com.mybatisgx.method.validator.param.entity.insert.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InsertMapperDao extends CurdDao<ValidatorUser, Long> {

    int insertValid(MapperUser user);
}
