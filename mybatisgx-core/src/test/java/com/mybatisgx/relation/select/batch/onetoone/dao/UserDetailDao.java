package com.mybatisgx.relation.select.batch.onetoone.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.batch.onetoone.entity.UserDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDetailDao extends SimpleDao<UserDetail, UserDetail, MultiId> {

    UserDetail findByUser(UserDetail userDetail);

    UserDetail findByUserId1AndUserId2(@Param("userId1") String userId1, @Param("userId2") String userId2);
}
