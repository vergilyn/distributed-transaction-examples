package com.vergilyn.examples.service.impl;

import javax.transaction.Transactional;

import com.alibaba.fescar.core.context.RootContext;
import com.vergilyn.examples.dto.CommodityDTO;
import com.vergilyn.examples.dto.StorageDTO;
import com.vergilyn.examples.entity.Storage;
import com.vergilyn.examples.repository.StorageRepository;
import com.vergilyn.examples.response.ObjectResponse;
import com.vergilyn.examples.service.StorageService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@Service
@Slf4j
public class StorageServiceImpl implements StorageService {
    @Autowired
    private StorageRepository storageRepository;

    @Override
    @Transactional
    public ObjectResponse<Void> decrease(CommodityDTO commodityDTO) {
        log.info("全局事务 begin, XID = {}, {}", RootContext.getXID(), commodityDTO.toString());

        int storage = storageRepository.decrease(commodityDTO.getCommodityCode(), commodityDTO.getTotal());

        log.info("全局事务 end, XID = {}, {}", RootContext.getXID(), commodityDTO.toString());


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
