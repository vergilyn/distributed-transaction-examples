package com.vergilyn.examples.service;

import com.vergilyn.examples.dto.CommodityDTO;
import com.vergilyn.examples.response.ObjectResponse;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
public interface StorageService {

    /**
     * 扣减库存
     */
    ObjectResponse<Void> decreaseStorage(CommodityDTO commodityDTO);
}
