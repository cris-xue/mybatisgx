package com.mybatisgx.custom.condition.dao;

import com.mybatisgx.custom.condition.entity.MultiIdUser;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.entity.MultiId;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MultiIdUserDao extends SimpleDao<MultiIdUser, MultiId<Long>> {
}
