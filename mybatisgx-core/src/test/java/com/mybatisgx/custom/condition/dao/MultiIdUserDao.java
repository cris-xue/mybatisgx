package com.mybatisgx.custom.condition.dao;

import com.mybatisgx.annotation.Statement;
import com.mybatisgx.custom.condition.base.MultiId;
import com.mybatisgx.custom.condition.entity.MultiIdUser;
import com.mybatisgx.dao.SimpleDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MultiIdUserDao extends SimpleDao<MultiIdUser, MultiIdUser, MultiId> {

    @Statement("select * from MultiIdUser u where u.id = :id")
    List<MultiIdUser> findByEmbeddedId(MultiIdUser entity);
}
