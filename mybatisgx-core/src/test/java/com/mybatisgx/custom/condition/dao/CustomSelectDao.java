package com.mybatisgx.custom.condition.dao;

import com.mybatisgx.custom.condition.entity.User;
import com.mybatisgx.custom.condition.entity.UserQuery;
import com.mybatisgx.dao.SelectDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author：薛承城
 * @description：一句话描述
 * @date：2026/3/20 14:01
 */
@Mapper
public interface CustomSelectDao extends SelectDao<User, UserQuery> {
}
