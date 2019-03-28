package com.vergilyn.examples.feign;

import com.vergilyn.examples.constants.FescarConstant;
import com.vergilyn.examples.dto.OrderDTO;
import com.vergilyn.examples.response.ObjectResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@FeignClient(name = FescarConstant.APPLICATION_ORDER, path = "order")
public interface OrderFeignService {

    /** 创建订单 */
    @RequestMapping(path = "create-order", method = RequestMethod.POST)
    ObjectResponse<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO);
}
