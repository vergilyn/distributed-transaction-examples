package com.vergilyn.examples.feign;

import com.vergilyn.examples.constants.FescarConstants;
import com.vergilyn.examples.dto.OrderDTO;
import com.vergilyn.examples.response.ObjectResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author VergiLyn
 * @date 2019-04-08
 */
@FeignClient(name = FescarConstants.APPLICATION_ORDER)
public interface OrderFeignClient {
    /** 创建订单 */
    @RequestMapping(path = "/order/create", method = RequestMethod.POST)
    ObjectResponse<OrderDTO> create(@RequestBody OrderDTO orderDTO);
}
