package com.lc.mybatisx.test.controller;

import com.lc.mybatisx.test.model.dto.UserDetailDto;
import com.lc.mybatisx.test.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/8/11 17:53
 */
@RestController
@RequestMapping(path = "/api/v1/user-detail")
public class UserDetailController {

    @Autowired
    private UserDetailService userDetailService;

    @GetMapping
    public UserDetailDto findById(Long id) {
        UserDetailDto userDetailDto = userDetailService.findById(id);
        return userDetailDto;
    }
}