package com.mybatisgx.custom.condition.dao;

import com.mybatisgx.custom.condition.base.MultiId;
import com.mybatisgx.custom.condition.entity.MultiIdUser;
import com.mybatisgx.dao.SimpleDao;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MultiIdUserDao extends SimpleDao<MultiIdUser, MultiIdUser, MultiId> {
}
