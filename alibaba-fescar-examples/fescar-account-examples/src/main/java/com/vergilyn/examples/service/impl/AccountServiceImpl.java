package com.vergilyn.examples.service.impl;

import javax.transaction.Transactional;

import com.alibaba.fescar.core.context.RootContext;
import com.vergilyn.examples.dto.AccountDTO;
import com.vergilyn.examples.repository.AccountRepository;
import com.vergilyn.examples.response.ObjectResponse;
import com.vergilyn.examples.service.AccountService;

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
    @Transactional
    public ObjectResponse decreaseAccount(AccountDTO accountDTO) {
        System.out.println("开始全局事务，XID = " + RootContext.getXID());

        int account = accountRepository.decreaseAccount(accountDTO.getUserId(), accountDTO.getAmount().doubleValue());

        return account > 0 ? ObjectResponse.success() : ObjectResponse.failure();
    }
}
