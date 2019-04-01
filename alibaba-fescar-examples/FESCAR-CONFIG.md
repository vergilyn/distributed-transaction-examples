# fescar-config

```
transport {
  # tcp udt unix-domain-socket
  type = "TCP"
  #NIO NATIVE
  server = "NIO"
  #enable heartbeat
  heartbeat = true
  #thread factory for netty
  thread-factory {
    boss-thread-prefix = "NettyBoss"
    worker-thread-prefix = "NettyServerNIOWorker"
    server-executor-thread-prefix = "NettyServerBizHandler"
    share-boss-worker = false
    client-selector-thread-prefix = "NettyClientSelector"
    client-selector-thread-size = 1
    client-worker-thread-prefix = "NettyClientWorkerThread"
    # netty boss thread size,will not be used for UDT
    boss-thread-size = 1
    #auto default pin or 8
    worker-thread-size = 8
  }
}
service {
  #vgroup->rgroup
  vgroup_mapping.fescar_vergilyn_tx_group = "localRgroup"
  #only support single node
  localRgroup.grouplist = "127.0.0.1:8091"
  #degrade current not support
  enableDegrade = false
  #disable
  disable = false
}

client {
  async.commit.buffer.limit = 10000
  lock {
    retry.internal = 10
    retry.times = 30
  }
}

```

## client

1. retry.internal, retry.times
retry.internal: 重试间隔(ms)
retry.times: 重试次数

``` JAVA
// com.alibaba.fescar.rm.datasource.exec.LockRetryController
public class LockRetryController {

    private static int LOCK_RETRY_INTERNAL =
        ConfigurationFactory.getInstance().getInt(ConfigurationKeys.CLIENT_LOCK_RETRY_INTERNAL, 10);
    private static int LOCK_RETRY_TIMES =
        ConfigurationFactory.getInstance().getInt(ConfigurationKeys.CLIENT_LOCK_RETRY_TIMES, 30);

    private int lockRetryInternal = LOCK_RETRY_INTERNAL;
    private int lockRetryTimes = LOCK_RETRY_TIMES;

    /**
     * Instantiates a new Lock retry controller.
     */
    public LockRetryController() {
    }

    public void sleep(Exception e) throws LockWaitTimeoutException {
        if (--lockRetryTimes < 0) {
            throw new LockWaitTimeoutException("Global lock wait timeout", e);
        }

        try {
            Thread.sleep(lockRetryInternal);
        } catch (InterruptedException ignore) {
        }
    }
}

```