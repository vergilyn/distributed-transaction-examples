package com.vergilyn.examples.service.impl;

import com.vergilyn.examples.dto.AccountDTO;
import com.vergilyn.examples.repository.AccountRepository;
import com.vergilyn.examples.response.ObjectResponse;
import com.vergilyn.examples.service.AccountService;

import org.dromara.raincat.core.annotation.TxTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
//    @Transactional
    @TxTransaction
    public ObjectResponse decrease(AccountDTO accountDTO) {

        int account = accountRepository.decrease(accountDTO.getUserId(), accountDTO.getAmount().doubleValue());

        return account > 0 ? ObjectResponse.success() : ObjectResponse.failure();
    }
}
