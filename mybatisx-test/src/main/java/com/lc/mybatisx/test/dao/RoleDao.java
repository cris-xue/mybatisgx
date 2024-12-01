package com.lc.mybatisx.test.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.test.model.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/8/11 22:35
 */
@Repository
public interface RoleDao extends SimpleDao<Role, Long> {

    // List<Role> findByIds(List<Long> ids);

}
