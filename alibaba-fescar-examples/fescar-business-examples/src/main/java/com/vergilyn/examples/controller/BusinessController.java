package com.vergilyn.examples.controller;

import com.vergilyn.examples.dto.BusinessDTO;
import com.vergilyn.examples.response.ObjectResponse;
import com.vergilyn.examples.service.BusinessService;

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
@RestController
@RequestMapping("/business")
@Slf4j
public class BusinessController {
    @Autowired
    private BusinessService businessService;

    /**
     * 模拟用户购买商品下单业务逻辑流程
     * @Param:
     * @Return:
     */
    @PostMapping("/buy")
    public ObjectResponse handleBusiness(@RequestBody BusinessDTO businessDTO){
        log.info("请求参数：{}", businessDTO.toString());
        return businessService.handleBusiness(businessDTO);
    }
}
