package com.lc.mybatisx.test.controller;

import com.lc.mybatisx.test.dao.RoleDao;
import com.lc.mybatisx.test.model.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    private RoleDao roleDao;

    @PostMapping
    public Role add(@RequestBody Role role) {
        roleDao.insert(role);
        return role;
    }

    @DeleteMapping
    public int deleteById(Long id) {
        return roleDao.deleteById(id);
    }

    @PutMapping
    public Role updateById(@RequestBody Role role) {
        roleDao.updateById(role);
        return role;
    }

    @GetMapping
    public Role findById(Long id) {
        return roleDao.findById(id);
    }

    @GetMapping(path = "/list")
    public List<Role> findList(Role role) {
        return roleDao.findList(role);
    }

    /*@GetMapping(path = "/ids")
    public List<Role> findByIdIn(@RequestParam("ids") List<Long> ids) {
        return roleDao.findByIds(ids);
    }*/

}