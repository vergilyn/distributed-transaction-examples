package com.vergilyn.examples.service.impl;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.Transactional;

import com.vergilyn.examples.dto.BusinessDTO;
import com.vergilyn.examples.dto.CommodityDTO;
import com.vergilyn.examples.dto.OrderDTO;
import com.vergilyn.examples.enums.RspStatusEnum;
import com.vergilyn.examples.exception.DefaultException;
import com.vergilyn.examples.feign.OrderFeignClient;
import com.vergilyn.examples.feign.StorageFeignClient;
import com.vergilyn.examples.response.ObjectResponse;
import com.vergilyn.examples.service.BusinessService;

import lombok.extern.slf4j.Slf4j;
import org.dromara.raincat.core.annotation.TxTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@Service
@Slf4j
public class BusinessServiceImpl implements BusinessService {
    @Autowired
    private StorageFeignClient storageFeignClient;
    @Autowired
    private OrderFeignClient orderFeignClient;

    private AtomicInteger thread = new AtomicInteger(0);

    @Override
//    @Transactional
    @TxTransaction
    public ObjectResponse buy(BusinessDTO businessDTO) {
        //1、扣减库存
        CommodityDTO commodityDTO = new CommodityDTO();
        commodityDTO.setCommodityCode(businessDTO.getCommodityCode());
        commodityDTO.setTotal(businessDTO.getTotal());

        // System.out.printf("storage-%d >>>> %s", thread.get(), storageFeignClient.get(commodityDTO.getCommodityCode()).getData());

        ObjectResponse storageResponse = storageFeignClient.decrease(commodityDTO);

        //2、创建订单
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(businessDTO.getUserId());
        orderDTO.setCommodityCode(businessDTO.getCommodityCode());
        orderDTO.setOrderTotal(businessDTO.getTotal());
        orderDTO.setOrderAmount(businessDTO.getAmount());
        ObjectResponse<OrderDTO> response = orderFeignClient.create(orderDTO);

        // true: 测试事务发生异常后，全局回滚功能
        if (Optional.ofNullable(businessDTO.getRollback()).orElse(Boolean.FALSE)) {
            throw new RuntimeException("测试抛异常后，分布式事务回滚！");
        }

        if (storageResponse.getStatus() != 200 || response.getStatus() != 200) {
            throw new DefaultException(RspStatusEnum.FAIL);
        }

        return ObjectResponse.success(response.getData());
    }

    @Override
    @Transactional
    public ObjectResponse writeTransaction() {
        long begin = System.currentTimeMillis();

        CommodityDTO commodityDTO = new CommodityDTO();
        commodityDTO.setCommodityCode("C201901140001");
        commodityDTO.setTotal(40);

        boolean first = thread.incrementAndGet() % 2 != 0;
        long millis = 0L;

        if (!first){
            millis = 3000L;
            sleep(millis);
        }
        log.trace("sleep-01 >>>>> first:" + first + ", sleep: " + millis);


        ObjectResponse response = storageFeignClient.decrease(commodityDTO);

        millis = 1000L;
        if (first){
            millis = 5000 - System.currentTimeMillis() + begin - 500;
            sleep(millis);
        }else {
            sleep(millis);
        }

        log.trace("sleep-02 >>>>> first:" + first + ", sleep: " + millis);

        return response;
    }


    private void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }
}
