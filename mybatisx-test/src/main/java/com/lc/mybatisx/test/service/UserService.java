package com.lc.mybatisx.test.service;

import com.lc.mybatisx.dao.Page;
import com.lc.mybatisx.dao.Pageable;
import com.lc.mybatisx.test.dao.UserDao;
import com.lc.mybatisx.test.model.converter.UserConverter;
import com.lc.mybatisx.test.model.dto.UserDto;
import com.lc.mybatisx.test.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserConverter userConverter;
    @Autowired
    private UserDao userDao;

    @Transactional
    public int addBatch(List<UserDto> userDtoList) {
        return userDao.insertBatch(userConverter.toEntityList(userDtoList), 1000);
    }

    @Transactional
    public UserDto findById(Long id) {
        User user = userDao.findById(id);
        UserDto userDto = userConverter.toDto(user);
        return userDto;
    }

    public List<UserDto> findTestParam(Long id) {
        // List<User> userList = userDao.findTestParam(new UserQuery());
        List<User> userList = userDao.findTestParam(null, null, new ArrayList<>(), id);
        List<UserDto> userDtoList = userConverter.toDtoList(userList);
        return userDtoList;
    }

    public Page<UserDto> list(User user) {
        Pageable pageable = new Pageable();
        pageable.setPageNo(1);
        pageable.setPageSize(10);
        // PageHelper.startPage(1, 10);
        Page<User> page = userDao.findListPage(user, pageable);
        return userConverter.toPage(page);
    }
}
