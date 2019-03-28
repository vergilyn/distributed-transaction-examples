package com.vergilyn.examples.controller;

import com.vergilyn.examples.dto.AccountDTO;
import com.vergilyn.examples.response.ObjectResponse;
import com.vergilyn.examples.service.AccountService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@RequestMapping("/account")
@RestController
@Slf4j
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/decrease-account")
    ObjectResponse decreaseAccount(@RequestBody AccountDTO accountDTO){
        log.info("请求账户微服务：{}", accountDTO.toString());
        return accountService.decreaseAccount(accountDTO);
    }
}

