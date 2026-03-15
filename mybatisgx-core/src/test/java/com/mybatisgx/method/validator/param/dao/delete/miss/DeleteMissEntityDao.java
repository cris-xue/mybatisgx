package com.mybatisgx.method.validator.param.dao.delete.miss;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.delete.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeleteMissEntityDao extends CurdDao<ValidatorUser, Long> {

    int deleteByName(String name);
}
