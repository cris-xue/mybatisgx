package com.mybatisgx.method.validator.param.dao.select.miss;

import com.mybatisgx.dao.CurdDao;
import com.mybatisgx.method.validator.param.entity.ValidatorUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SelectMissEntityDao extends CurdDao<ValidatorUser, Long> {

    List<ValidatorUser> findByName(Long name);
}
