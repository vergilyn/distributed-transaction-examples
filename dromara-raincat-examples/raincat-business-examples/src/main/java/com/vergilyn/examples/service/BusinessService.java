package com.vergilyn.examples.service;

import com.vergilyn.examples.dto.BusinessDTO;
import com.vergilyn.examples.response.ObjectResponse;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
public interface BusinessService {

    ObjectResponse buy(BusinessDTO businessDTO);

    ObjectResponse writeTransaction();

}
