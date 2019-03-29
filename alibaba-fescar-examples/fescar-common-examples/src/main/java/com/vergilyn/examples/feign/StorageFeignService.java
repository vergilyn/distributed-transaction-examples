package com.vergilyn.examples.feign;

import com.vergilyn.examples.constants.FescarConstant;
import com.vergilyn.examples.dto.CommodityDTO;
import com.vergilyn.examples.response.ObjectResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@FeignClient(name = FescarConstant.APPLICATION_STORAGE)
public interface StorageFeignService {

    /** 扣减库存 */
    @RequestMapping(path = "/storage/decrease-storage", method = RequestMethod.POST)
    ObjectResponse<Void> decreaseStorage(@RequestBody CommodityDTO commodityDTO);

}
