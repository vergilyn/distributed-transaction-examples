package com.vergilyn.examples.feign;

import com.vergilyn.examples.constants.FescarConstant;
import com.vergilyn.examples.dto.AccountDTO;
import com.vergilyn.examples.response.ObjectResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@FeignClient(name = FescarConstant.APPLICATION_ACCOUNT)
public interface AccountFeignService {

    /** 从账户扣钱 */
    @RequestMapping(path = "/account/decrease-account", method = RequestMethod.POST)
    ObjectResponse<Void> decreaseAccount(@RequestBody AccountDTO accountDTO);
}
