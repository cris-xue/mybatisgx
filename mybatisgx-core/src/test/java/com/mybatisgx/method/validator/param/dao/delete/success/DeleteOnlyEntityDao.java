package com.mybatisgx.method.validator.param.dao.delete.success;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeleteOnlyEntityDao extends CurdDao<ValidatorUser, Long> {

    int deleteByName(ValidatorUser user);
}
