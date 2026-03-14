package com.mybatisgx.method.validator.param.dao.select.mapper;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.MapperUser;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SelectMapperDao extends CurdDao<ValidatorUser, Long> {

    List<ValidatorUser> findByName(MapperUser user);
}
