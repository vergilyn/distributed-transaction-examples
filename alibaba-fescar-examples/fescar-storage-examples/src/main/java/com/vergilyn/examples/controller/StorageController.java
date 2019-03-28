package com.vergilyn.examples.controller;

import com.vergilyn.examples.dto.CommodityDTO;
import com.vergilyn.examples.response.ObjectResponse;
import com.vergilyn.examples.service.StorageService;

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
@RequestMapping("/storage")
@RestController
@Slf4j
public class StorageController {
    @Autowired
    private StorageService storageService;

    /**
     * 扣减库存
     */
    @PostMapping("/decrease-storage")
    ObjectResponse decreaseStorage(@RequestBody CommodityDTO commodityDTO){
        log.info("请求库存微服务：{}",commodityDTO.toString());
        return storageService.decreaseStorage(commodityDTO);
    }
}
