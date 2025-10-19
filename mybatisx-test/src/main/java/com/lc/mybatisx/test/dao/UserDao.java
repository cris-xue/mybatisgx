package com.lc.mybatisx.test.dao;

import com.lc.mybatisx.annotation.Dynamic;
import com.lc.mybatisx.annotation.QueryCondition;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.test.model.entity.User;
import com.lc.mybatisx.test.model.entity.UserQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/8/11 22:35
 */
@Repository
public interface UserDao extends SimpleDao<User, Long> {

    List<User> findByIdIn(@Param("id") List<Long> id);

    List<User> findByRoleIds(List<Long> ids);

    List<User> findByName(@Param("name") String name);

    List<User> findByAge(Integer age);

    List<User> findByNameAndAge(String name, Integer age);

    // @Dynamic
    @QueryCondition("NameLikeAnd(NameOr(AgeOrRoleIds))Or(AgeAndName)")
    List<User> findByNameOrAgeOrRoleIds(String name, Integer age, List<Long> roleIds);

    @Dynamic
    int update(UserQuery userQuery);

    @Dynamic
    List<User> findList1(UserQuery userQuery);

    // List<User> findTestParam(UserQuery userQuery);

    List<User> findTestParam(String name, Integer age, List<Long> roleIds, @Param("id") Long id);

    List<User> findTestSubQuery();
}
