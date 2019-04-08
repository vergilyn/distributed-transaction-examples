package com.vergilyn.examples.service;

import com.vergilyn.examples.dto.OrderDTO;
import com.vergilyn.examples.response.ObjectResponse;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
public interface OrderService {

    /** 创建订单 */
    ObjectResponse<OrderDTO> create(OrderDTO orderDTO);
}
