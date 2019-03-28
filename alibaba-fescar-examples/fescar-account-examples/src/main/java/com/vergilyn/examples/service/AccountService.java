package com.vergilyn.examples.service;

import com.vergilyn.examples.dto.AccountDTO;
import com.vergilyn.examples.response.ObjectResponse;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
public interface AccountService {
    /** 扣用户钱 */
    ObjectResponse decreaseAccount(AccountDTO accountDTO);
}
