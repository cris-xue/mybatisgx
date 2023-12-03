package com.lc.mybatisx.handler.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.handler.model.User;
import org.springframework.stereotype.Repository;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/8/11 22:35
 */
@Repository
public interface UserDao extends SimpleDao<User, Long> {
}
