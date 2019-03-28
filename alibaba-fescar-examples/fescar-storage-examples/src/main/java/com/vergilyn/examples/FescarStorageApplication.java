package com.vergilyn.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class FescarStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(FescarStorageApplication.class, args);
    }
}
