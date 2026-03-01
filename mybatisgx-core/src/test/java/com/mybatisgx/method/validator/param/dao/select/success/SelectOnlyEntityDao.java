package com.mybatisgx.method.validator.param.dao.select.success;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SelectOnlyEntityDao extends CurdDao<ValidatorUser, Long> {

    List<ValidatorUser> findByName(ValidatorUser user);
}
