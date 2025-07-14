package com.lc.mybatisx.test.controller;

import com.lc.mybatisx.dao.Page;
import com.lc.mybatisx.test.dao.UserDao;
import com.lc.mybatisx.test.model.dto.UserDto;
import com.lc.mybatisx.test.model.entity.User;
import com.lc.mybatisx.test.model.entity.UserQuery;
import com.lc.mybatisx.test.service.UserService;
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
    @Autowired
    private UserService userService;

    @PostMapping
    public User add(@RequestBody User user) {
        userDao.insert(user);
        return user;
    }

    @PostMapping(path = "/batch")
    public int addBatch(@RequestBody List<UserDto> userDtoList) {
        return userService.addBatch(userDtoList);
    }

    @DeleteMapping
    public int deleteById(Long id) {
        return userDao.deleteById(id);
    }

    @PutMapping(path = "/id")
    public User updateById(@RequestBody User user) {
        userDao.updateById(user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody UserQuery user) {
        userDao.update(user);
        return user;
    }

    @GetMapping
    public UserDto findById(Long id) {
        UserDto user = userService.findById(id);
        return user;
    }

    @GetMapping(path = "/list")
    public Page<UserDto> list(User user) {
        return userService.list(user);
    }

    @GetMapping(path = "/name")
    public List<User> findByName(String name) {
        return userDao.findByName(name);
    }

    @GetMapping(path = "/ids")
    public List<User> findByIdIn(@RequestParam("ids") List<Long> ids) {
        List<User> userList = userDao.findByIdIn(ids);
        return userList;
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

    @GetMapping(path = "/test-param")
    public List<UserDto> findTestParam(Long id) {
        return userService.findTestParam(id);
    }
}