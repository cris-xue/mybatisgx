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

    @DeleteMapping(path = "/delete-by-id")
    public int deleteById(Long id) {
        Test test = testDao.findById(id);
        int count = testDao.deleteById(id);
        return count;
    }

}
