package com.lc.mybatisx.test.controller;

import com.lc.mybatisx.test.model.dto.RoleDto;
import com.lc.mybatisx.test.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/8/11 17:53
 */
@RestController
@RequestMapping(path = "/api/v1/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public RoleDto findById(Long id) {
        RoleDto roleDto = roleService.findById(id);
        return roleDto;
    }

    @GetMapping(path = "/list")
    public List<RoleDto> findList(RoleDto roleDto) {
        return roleService.list(roleDto);
    }
}