package com.vergilyn.examples.service;

import com.vergilyn.examples.dto.BusinessDTO;
import com.vergilyn.examples.dto.StorageDTO;
import com.vergilyn.examples.response.ObjectResponse;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
public interface BusinessService {

    ObjectResponse buy(BusinessDTO businessDTO);

    ObjectResponse writeTransaction();

    ObjectResponse<Void> decreaseStorage(Long beforeMillis, Long afterMillis, boolean rollback);

    ObjectResponse<StorageDTO> getStorage(Long beforeMillis, Long afterMillis);
}
