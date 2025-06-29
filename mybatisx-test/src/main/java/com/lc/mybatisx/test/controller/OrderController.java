package com.lc.mybatisx.test.controller;

import com.lc.mybatisx.test.model.dto.OrderDto;
import com.lc.mybatisx.test.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/8/11 17:53
 */
@RestController
@RequestMapping(path = "/api/v1/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public OrderDto findById(Long id) {
        OrderDto orderDto = orderService.findById(id);
        return orderDto;
    }
}