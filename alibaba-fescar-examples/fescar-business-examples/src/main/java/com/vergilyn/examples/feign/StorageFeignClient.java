package com.vergilyn.examples.feign;

import com.vergilyn.examples.constants.FescarConstants;
import com.vergilyn.examples.dto.CommodityDTO;
import com.vergilyn.examples.dto.StorageDTO;
import com.vergilyn.examples.response.ObjectResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author VergiLyn
 * @date 2019-04-08
 */
@FeignClient(name = FescarConstants.APPLICATION_STORAGE)
public interface StorageFeignClient {

    /** 扣减库存 */
    @RequestMapping(path = "/storage/decrease", method = RequestMethod.POST)
    ObjectResponse<Void> decrease(@RequestBody CommodityDTO commodityDTO);

    /** 扣减库存 */
    @RequestMapping(path = "/storage/get", method = RequestMethod.GET)
    ObjectResponse<StorageDTO> get(@RequestParam("commodityCode") String commodityCode);
}
