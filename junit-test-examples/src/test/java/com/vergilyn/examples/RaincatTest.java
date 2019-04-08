package com.vergilyn.examples;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;
import com.vergilyn.examples.dto.BusinessDTO;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author VergiLyn
 * @date 2019-04-08
 */
public class RaincatTest {
    private HttpClient httpClient;
    private AtomicInteger count = new AtomicInteger();

    @BeforeTest
    public void before(){
        httpClient = HttpClients.custom()
                .setMaxConnPerRoute(10)
                .setMaxConnTotal(20)
                .build();
    }

    @Test(invocationCount = 2, threadPoolSize = 2)
    public void businessBuy() {
        int incr = count.getAndIncrement();

        HttpPost post = new HttpPost("http://127.0.0.1:9000/business/buy");
        BusinessDTO param = new BusinessDTO();
        param.setUserId("1");
        param.setCommodityCode("C201901140001");
        param.setName("test-" + incr);
        param.setTotal(40);
        param.setAmount(new BigDecimal("400.00"));
        param.setRollback(incr % 2 == 0);

        post.addHeader("Content-type","application/json; charset=utf-8");
        post.setHeader("Accept", "application/json");
        post.setEntity(new StringEntity(JSON.toJSONString(param), Charset.forName("UTF-8")));

        try {
            HttpResponse response = httpClient.execute(post);
            String str = EntityUtils.toString(response.getEntity());
            System.out.println(post.getURI().getPath() + " >>>> " +str );
            Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
