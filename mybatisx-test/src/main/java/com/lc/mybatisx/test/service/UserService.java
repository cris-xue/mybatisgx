package com.lc.mybatisx.test.service;

import com.lc.mybatisx.test.dao.UserDao;
import com.lc.mybatisx.test.model.converter.UserConverter;
import com.lc.mybatisx.test.model.dto.UserDto;
import com.lc.mybatisx.test.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserConverter userConverter;
    @Autowired
    private UserDao userDao;

    @Transactional
    public UserDto findById(Long id) {
        User user = userDao.findById(id);
        UserDto userDto = userConverter.toDto(user);
        return userDto;
    }
}
