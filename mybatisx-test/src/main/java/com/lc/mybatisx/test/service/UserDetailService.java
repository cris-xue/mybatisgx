package com.lc.mybatisx.test.service;

import com.lc.mybatisx.test.dao.UserDetailDao;
import com.lc.mybatisx.test.model.converter.UserDetailConverter;
import com.lc.mybatisx.test.model.dto.UserDetailDto;
import com.lc.mybatisx.test.model.entity.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService {

    @Autowired
    private UserDetailConverter userDetailConverter;
    @Autowired
    private UserDetailDao userDetailDao;

    public UserDetailDto findById(Long id) {
        UserDetail userDetail = userDetailDao.findById(id);
        UserDetailDto userDetailDto = userDetailConverter.toDto(userDetail);
        return userDetailDto;
    }
}
