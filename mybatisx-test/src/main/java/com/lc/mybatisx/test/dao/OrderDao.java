package com.lc.mybatisx.test.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.test.model.entity.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDao extends SimpleDao<Order, Long> {

    Order findBYcodecodecinputUserIduser(String name);
}
