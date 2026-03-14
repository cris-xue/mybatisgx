package com.mybatisgx.method.validator.param.dao.update.mapper;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.update.MapperUser;
import com.mybatisgx.method.validator.param.entity.update.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UpdateMapperDao extends CurdDao<ValidatorUser, Long> {

    int updateByName(MapperUser user);
}
