package com.lc.mybatisx.test.controller;

import com.lc.mybatisx.test.dao.TestDao;
import com.lc.mybatisx.test.model.entity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/test")
public class TestController {

    @Autowired
    private TestDao testDao;

    @PostMapping(path = "/insert-selective")
    public Test insertSelective(@RequestBody Test test) {
        int count = testDao.insertSelective(test);
        return test;
    }

    @PutMapping(path = "/update-by-id")
    public int updateById(Long id) {
        Test test = testDao.findById(id);
        int count = testDao.updateById(test);
        return count;
    }

    @PutMapping(path = "/update-by-id-name-username")
    public int updateByIdAndNameOrUserName(Long id, String name, String userName) {
        // int count = testDao.updateByIdAndNameOrUserName(id, name, userName);
        int count = 1;
        return count;

    }

}
