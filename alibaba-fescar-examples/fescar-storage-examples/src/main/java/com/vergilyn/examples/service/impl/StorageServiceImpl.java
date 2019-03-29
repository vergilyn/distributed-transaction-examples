package com.vergilyn.examples.service.impl;

import javax.transaction.Transactional;

import com.vergilyn.examples.dto.CommodityDTO;
import com.vergilyn.examples.repository.StorageRepository;
import com.vergilyn.examples.response.ObjectResponse;
import com.vergilyn.examples.service.StorageService;

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
    @Transactional
    public ObjectResponse<Void> decreaseStorage(CommodityDTO commodityDTO) {
        int storage = storageRepository.decreaseStorage(commodityDTO.getCommodityCode(), commodityDTO.getTotal());

        return storage > 0 ? ObjectResponse.success(): ObjectResponse.failure();
    }
}
