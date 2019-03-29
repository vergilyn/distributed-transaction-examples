package com.vergilyn.examples.config;

import com.alibaba.fescar.spring.annotation.GlobalTransactionScanner;
import com.vergilyn.examples.constants.FescarConstant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author VergiLyn
 * @date 2019-03-29
 */
@Configuration
public class FescarConfiguration {
    /**
     * init global transaction scanner
     */
    @Bean
    public GlobalTransactionScanner globalTransactionScanner(@Value("${spring.application.name}") String applicationId){
        return new GlobalTransactionScanner(applicationId, FescarConstant.TX_SERVICE_GROUP);
    }
}
