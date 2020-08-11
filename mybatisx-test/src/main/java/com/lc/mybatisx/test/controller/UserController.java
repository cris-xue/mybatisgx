package com.lc.mybatisx.test.controller;

import com.lc.mybatisx.test.dao.UserDao;
import com.lc.mybatisx.test.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/8/11 17:53
 */
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserDao userDao;

    @PostMapping()
    public User add(@RequestBody User user) {
        userDao.insertSelective(user);
        return user;
    }

}
