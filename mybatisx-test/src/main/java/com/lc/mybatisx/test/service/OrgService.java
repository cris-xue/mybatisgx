package com.lc.mybatisx.test.service;

import com.lc.mybatisx.test.dao.OrgDao;
import com.lc.mybatisx.test.model.converter.OrgConverter;
import com.lc.mybatisx.test.model.dto.OrgDto;
import com.lc.mybatisx.test.model.entity.Org;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrgService {

    @Autowired
    private OrgConverter orgConverter;
    @Autowired
    private OrgDao orgDao;

    public OrgDto findById(Long id) {
        Org org = orgDao.findById(id);
        return orgConverter.toDto(org);
    }

    public List<OrgDto> list(OrgDto orgDto) {
        List<Org> orgList = orgDao.findList(orgConverter.toEntity(orgDto));
        return orgConverter.toDtoList(orgList);
    }
}
