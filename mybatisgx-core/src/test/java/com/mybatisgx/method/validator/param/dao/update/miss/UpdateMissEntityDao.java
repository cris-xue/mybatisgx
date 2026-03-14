package com.mybatisgx.method.validator.param.dao.update.miss;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UpdateMissEntityDao extends CurdDao<ValidatorUser, Long> {

    int updateByName(Long name);
}
