package com.lc.mybatisx.test.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.test.model.entity.Test;
import org.springframework.stereotype.Repository;

@Repository
public interface TestDao extends SimpleDao<Test, Long> {
}
