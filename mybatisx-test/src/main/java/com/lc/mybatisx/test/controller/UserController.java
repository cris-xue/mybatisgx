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
        userDao.insert(user);
        return user;
    }

    @DeleteMapping
    public int deleteById(Long id) {
        return userDao.deleteById(id);
    }

    @PutMapping
    public User updateById(@RequestBody User user) {
        User u = userDao.findById(user.getId());
        user.setVersion(u.getVersion());
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

    @GetMapping(path = "/name")
    public List<User> findByName(String name) {
        return userDao.findByName(name);
    }

    @GetMapping(path = "/ids")
    public List<User> findByIdIn(@RequestParam("ids") List<Long> ids) {
        return userDao.findByIdIn(ids);
    }

    @GetMapping(path = "/role-ids")
    public List<User> findByRoleIds(@RequestParam("roleIds") List<Long> roleIds) {
        return userDao.findByRoleIds(roleIds);
    }

    @GetMapping(path = "/age")
    public List<User> findByAge(Integer age) {
        return userDao.findByAge(age);
    }

    @GetMapping(path = "/name-and-age")
    public List<User> findByNameAndAge(String name, Integer age) {
        return userDao.findByNameAndAge(name, age);
    }

    @GetMapping(path = "/name-or-age-or-roleids")
    public List<User> findByNameOrAgeOrRoleIds(String name, Integer age, @RequestParam(value = "roleIds", required = false) List<Long> roleIds) {
        return userDao.findByNameOrAgeOrRoleIds(name, age, roleIds);
    }

}