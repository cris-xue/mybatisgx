package com.mybatisgx.method.validator.param.dao.insert.miss;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.insert.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InsertMissEntityDao extends CurdDao<ValidatorUser, Long> {

    int insertValid(@Param("id") Long id);
}
