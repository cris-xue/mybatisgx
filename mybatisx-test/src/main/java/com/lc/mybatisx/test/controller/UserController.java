package com.lc.mybatisx.test.controller;

import com.lc.mybatisx.test.dao.UserDao;
import com.lc.mybatisx.test.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/8/11 17:53
 */
@RestController
@RequestMapping(path = "/api/v1/user")
public class UserController {

    @Autowired
    private UserDao userDao;

    @PostMapping
    public User add(@RequestBody User user) {
        userDao.insertSelective(user);
        return user;
    }

    @DeleteMapping
    public int deleteById(Long id) {
        return userDao.deleteById(id);
    }

    @PutMapping
    public User updateById(@RequestBody User user) {
        userDao.updateById(user);
        return user;
    }

    @GetMapping
    public User findById(Long id) {
        return userDao.findById(id);
    }

    @GetMapping(path = "/all")
    public List<User> findByAll() {
        return userDao.findAll();
    }

}
