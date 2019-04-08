package com.vergilyn.examples.service.impl;

import com.vergilyn.examples.dto.CommodityDTO;
import com.vergilyn.examples.dto.StorageDTO;
import com.vergilyn.examples.entity.Storage;
import com.vergilyn.examples.repository.StorageRepository;
import com.vergilyn.examples.response.ObjectResponse;
import com.vergilyn.examples.service.StorageService;

import org.dromara.raincat.core.annotation.TxTransaction;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@Service
public class StorageServiceImpl implements StorageService {
    @Autowired
    private StorageRepository storageRepository;

    @Override
//    @Transactional
    @TxTransaction
    public ObjectResponse<Void> decrease(CommodityDTO commodityDTO) {
        int storage = storageRepository.decrease(commodityDTO.getCommodityCode(), commodityDTO.getTotal());

        return storage > 0 ? ObjectResponse.success() : ObjectResponse.failure();
    }

    @Override
    public ObjectResponse<StorageDTO> getByCommodityCode(String commodityCode) {
        Storage storage = storageRepository.getFirstByCommodityCode(commodityCode);
        StorageDTO dto = new StorageDTO();
        BeanUtils.copyProperties(storage, dto);
        return ObjectResponse.success(dto);
    }
}
