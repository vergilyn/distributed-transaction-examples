package com.vergilyn.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients
@ImportResource({"classpath:applicationContext.xml"})
public class RaincatAccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaincatAccountApplication.class, args);
    }
}
