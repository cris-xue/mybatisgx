package com.lc.mybatisx.test.service;

import com.lc.mybatisx.test.dao.OrderDao;
import com.lc.mybatisx.test.model.converter.OrderConverter;
import com.lc.mybatisx.test.model.dto.OrderDto;
import com.lc.mybatisx.test.model.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderConverter orderConverter;
    @Autowired
    private OrderDao orderDao;

    public OrderDto findById(Long id) {
        Order order = orderDao.findById(id);
        OrderDto orderDto = orderConverter.toDto(order);
        return orderDto;
    }

    public List<OrderDto> list(OrderDto orderDto) {
        List<Order> orderList = orderDao.findList(orderConverter.toEntity(orderDto));
        return orderConverter.toDtoList(orderList);
    }
}
