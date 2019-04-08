package com.vergilyn.examples.controller;

import com.vergilyn.examples.dto.OrderDTO;
import com.vergilyn.examples.response.ObjectResponse;
import com.vergilyn.examples.service.OrderService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@RequestMapping("/order")
@RestController
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ObjectResponse<OrderDTO> create(@RequestBody OrderDTO orderDTO){
        log.info("请求订单微服务：{}", orderDTO.toString());
        return orderService.create(orderDTO);
    }
}
