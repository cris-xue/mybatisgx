package com.mybatisgx.method.validator.param.dao.insert.missing;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InsertMissingEntityDao extends CurdDao<ValidatorUser, Long> {

    int insertMissingEntity(@Param("id") Long id);
}
