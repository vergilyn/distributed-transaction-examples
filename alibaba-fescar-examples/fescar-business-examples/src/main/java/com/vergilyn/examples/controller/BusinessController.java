package com.vergilyn.examples.controller;

import com.vergilyn.examples.dto.BusinessDTO;
import com.vergilyn.examples.dto.StorageDTO;
import com.vergilyn.examples.response.ObjectResponse;
import com.vergilyn.examples.service.BusinessService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ObjectResponse buy(@RequestBody BusinessDTO businessDTO){
        log.info("请求参数：{}", businessDTO.toString());
        return businessService.buy(businessDTO);
    }

    @GetMapping("/write")
    public ObjectResponse writeTransaction(){
        return businessService.writeTransaction();
    }

    @GetMapping("/decrease-storage")
    public ObjectResponse<Void> decreaseStorage(Long beforeMillis, Long afterMillis, @RequestParam(required = false, defaultValue = "false") boolean rollback){
        ObjectResponse<Void> response;
        try {
            response = businessService.decreaseStorage(beforeMillis, afterMillis, rollback);
        }catch (Exception e){
            response = ObjectResponse.failureOther(e.getMessage());
            e.printStackTrace();
        }
        log.info("response >>>> {}", response);
        return response;
    }

    @GetMapping("/get-storage")
    public ObjectResponse<StorageDTO> getStorage(Long beforeMillis, Long afterMillis){
        return businessService.getStorage(beforeMillis, afterMillis);
    }
}
