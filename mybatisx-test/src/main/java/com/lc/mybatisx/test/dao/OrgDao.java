package com.lc.mybatisx.test.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.test.model.entity.Org;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgDao extends SimpleDao<Org, Long> {
}
