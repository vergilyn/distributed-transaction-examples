package com.vergilyn.examples;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author VergiLyn
 * @date 2019-04-01
 */
public class ConcurrentTest {
    private HttpClient httpClient;

    @BeforeTest
    public void before(){
        httpClient = HttpClients.custom()
                .setMaxConnTotal(5)
                .setMaxConnPerRoute(5)
                .build();
    }

    @Test(invocationCount = 2, threadPoolSize = 2, timeOut = 300000)
    public void writeTransaction(){
        HttpGet httpGet = new HttpGet("http://127.0.0.1:8080/business/write");
        try {
            httpClient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
