package com.vergilyn.examples;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;
import com.vergilyn.examples.constants.DataConstants;
import com.vergilyn.examples.dto.BusinessDTO;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * <a href="https://blog.csdn.net/dingguanyi/article/details/80888441">事务并发的问题</a>
 * @author VergiLyn
 * @date 2019-04-08
 */
public class FescarTest {
    private HttpClient httpClient;
    private ExecutorService executorThread;
    private AtomicInteger count = new AtomicInteger();
    private static final BusinessUrl BUSINESS_URL = new BusinessUrl("http://127.0.0.1:8080");

    @BeforeTest
    public void before() {
        httpClient = HttpClients.custom().setMaxConnPerRoute(10).setMaxConnTotal(20).build();

        executorThread = Executors.newFixedThreadPool(10);
    }

    /**
     * 相关:
     *   - com.vergilyn.examples.service.impl.StorageServiceImpl#decrease(com.vergilyn.examples.dto.CommodityDTO)
     *   - com.alibaba.fescar.server.session.BranchSession#lock()
     *
     * StorageServiceImpl#decrease() 中执行 storageRepository#decrease() 时并不会触发获取 fescar-global-lock。
     * 而是在 StorageServiceImpl#decrease() 方法体执行完，提交本地事务时，去获取fescar-global-lock。
     * @throws IOException
     */
    @Test(description = "fescar获取全局锁的时间段")
    public void acquireLock() throws IOException {
        HttpGet txA = new HttpGet(BUSINESS_URL.decreaseStorage(0L, 0L, false));

        System.out.println(EntityUtils.toString(httpClient.execute(txA).getEntity()));
    }

    @Test(invocationCount = 1, threadPoolSize = 1)
    public void businessBuy() {
        int incr = count.getAndIncrement();

        HttpPost post = new HttpPost(BUSINESS_URL.buy());
        BusinessDTO param = new BusinessDTO();
        param.setUserId(DataConstants.ACCOUNT_USER_ID);
        param.setCommodityCode(DataConstants.STORAGE_CODE);
        param.setName("test-" + incr);
        param.setTotal(40);
        param.setAmount(new BigDecimal("400.00"));
        param.setRollback(true);

        post.addHeader("Content-type", "application/json; charset=utf-8");
        post.setHeader("Accept", "application/json");
        post.setEntity(new StringEntity(JSON.toJSONString(param), Charset.forName("UTF-8")));

        try {
            HttpResponse response = httpClient.execute(post);
            String str = EntityUtils.toString(response.getEntity());
            System.out.println(post.getURI().getPath() + " >>>> " + str);
            Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证：fescar是否会出现 脏读
     * 脏读：A事务读取B事务尚未提交的更改数据，并在这个数据的基础上操作。如果恰巧B事务回滚，那么A事务读到的数据根本是不被承认的。
     *
     * 结论：会出现脏读，因为fescar本地事务已经提交，对全局事务来说 isolation = read-uncommitted。（所以也会出现 不可重复读、幻读）
     */
    @Test
    public void dirtyRead() throws ExecutionException, InterruptedException {

        Future<String> fa = executorThread.submit(() -> {
            HttpGet txA = new HttpGet(BUSINESS_URL.getStorage(1500L, 0L));
            return EntityUtils.toString(httpClient.execute(txA).getEntity());
        });

        Future<String> fb = executorThread.submit(() -> {
            HttpGet txB = new HttpGet(BUSINESS_URL.decreaseStorage(500L, 3000L));
            return EntityUtils.toString(httpClient.execute(txB).getEntity());
        });
        System.out.println("dirty-read executing >>>>> ");
        System.out.println("txa response >>>> " + fa.get());
        System.out.println("txb response >>>> " + fb.get());

    }


    /**
     * 验证：第一类丢失更新
     * 概念：A事务撤销时，把已经提交的B事务的更新数据覆盖
     *
     * FIXME txB的事务无法提交！
     */
    @Test
    public void firstlyLose() throws ExecutionException, InterruptedException, IOException {
        Future<String> fa = executorThread.submit(() -> {
            HttpGet txA = new HttpGet(BUSINESS_URL.decreaseStorage(0L, 1000L, true));
            return EntityUtils.toString(httpClient.execute(txA).getEntity());
        });

        Future<String> fb = executorThread.submit(() -> {
            HttpGet txB = new HttpGet(BUSINESS_URL.decreaseStorage(500L, 0L, false));
            return EntityUtils.toString(httpClient.execute(txB).getEntity());
        });
        System.out.println("firstly-lose executing >>>>> ");
        System.out.println("txa response >>>> " + fa.get());
        System.out.println("txb response >>>> " + fb.get());

        String result = EntityUtils.toString(httpClient.execute(new HttpGet(BUSINESS_URL.getStorage(0L, 0L))).getEntity());
        System.out.println("result >>>> " + result);
    }

    /**
     * 验证：第二类丢失更新
     * 概念：A事务覆盖B事务已经提交的数据
     */
    @Test
    public void secondlyLose(){

    }
}
