package com.lc.mybatisx.test.service;

import com.lc.mybatisx.test.dao.RoleDao;
import com.lc.mybatisx.test.model.converter.RoleConverter;
import com.lc.mybatisx.test.model.dto.RoleDto;
import com.lc.mybatisx.test.model.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleConverter roleConverter;
    @Autowired
    private RoleDao roleDao;

    public RoleDto findById(Long id) {
        Role role = roleDao.findById(id);
        return roleConverter.toDto(role);
    }

    public List<RoleDto> list(RoleDto roleDto) {
        List<Role> orderList = roleDao.findList(roleConverter.toEntity(roleDto));
        return roleConverter.toDtoList(orderList);
    }
}
