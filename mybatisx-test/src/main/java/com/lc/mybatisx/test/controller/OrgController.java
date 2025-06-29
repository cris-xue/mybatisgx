package com.lc.mybatisx.test.controller;

import com.lc.mybatisx.test.model.dto.OrgDto;
import com.lc.mybatisx.test.service.OrgService;
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
@RequestMapping(path = "/api/v1/org")
public class OrgController {

    @Autowired
    private OrgService orgService;

    @GetMapping
    public OrgDto findById(Long id) {
        return orgService.findById(id);
    }

    @GetMapping(path = "/list")
    public List<OrgDto> list(OrgDto orgDto) {
        return orgService.list(orgDto);
    }
}