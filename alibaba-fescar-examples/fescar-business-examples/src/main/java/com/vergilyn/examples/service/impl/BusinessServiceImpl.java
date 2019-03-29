package com.vergilyn.examples.service.impl;

import javax.transaction.Transactional;

import com.alibaba.fescar.core.context.RootContext;
import com.alibaba.fescar.spring.annotation.GlobalTransactional;
import com.vergilyn.examples.constants.FescarConstant;
import com.vergilyn.examples.dto.BusinessDTO;
import com.vergilyn.examples.dto.CommodityDTO;
import com.vergilyn.examples.dto.OrderDTO;
import com.vergilyn.examples.enums.RspStatusEnum;
import com.vergilyn.examples.exception.DefaultException;
import com.vergilyn.examples.feign.OrderFeignService;
import com.vergilyn.examples.feign.StorageFeignService;
import com.vergilyn.examples.response.ObjectResponse;
import com.vergilyn.examples.service.BusinessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@Service
public class BusinessServiceImpl implements BusinessService {
    @Autowired
    private StorageFeignService storageFeignService;
    @Autowired
    private OrderFeignService orderFeignService;
    private boolean flag;

    @Override
    @GlobalTransactional(timeoutMills = 300000, name = FescarConstant.TX_SERVICE_GROUP)
    @Transactional
    public ObjectResponse handleBusiness(BusinessDTO businessDTO) {
        System.out.println("开始全局事务，XID = " + RootContext.getXID());
        //1、扣减库存
        CommodityDTO commodityDTO = new CommodityDTO();
        commodityDTO.setCommodityCode(businessDTO.getCommodityCode());
        commodityDTO.setTotal(businessDTO.getTotal());
        ObjectResponse storageResponse = storageFeignService.decreaseStorage(commodityDTO);

        //2、创建订单
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(businessDTO.getUserId());
        orderDTO.setCommodityCode(businessDTO.getCommodityCode());
        orderDTO.setOrderTotal(businessDTO.getTotal());
        orderDTO.setOrderAmount(businessDTO.getAmount());
        ObjectResponse<OrderDTO> response = orderFeignService.createOrder(orderDTO);

        //打开注释测试事务发生异常后，全局回滚功能
        if (!flag) {
            throw new RuntimeException("测试抛异常后，分布式事务回滚！");
        }

        if (storageResponse.getStatus() != 200 || response.getStatus() != 200) {
            throw new DefaultException(RspStatusEnum.FAIL);
        }

        return ObjectResponse.success(response.getData());
    }
}
