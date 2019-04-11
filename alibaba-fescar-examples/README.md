# alibaba-fescar-examples 

- [fescar-samples Github]
- [fescar AT-Mode](https://github.com/alibaba/fescar/wiki/AT-Mode)
- [springboot+fescar注意事项](https://segmentfault.com/a/1190000018693315)

1. fescar的`DatasourceProxy`貌似只支持DruidDataSource

2. nacos-config: datasource-fescar-druid.yaml
```
# datasource-fescar-druid.yaml
spring:
  datasource:
    username: root
    password: 123456
    driver-class-name: com.mysql.jdbc.Driver  # mysql8.0以前使用com.mysql.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/test_distributed_transaction?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull
    #type: com.alibaba.druid.pool.DruidDataSource  # 使用Druid数据源
    druid:
      initial-size: 5  # 初始化大小
      min-idle: 5  # 最小
      max-active: 100  # 最大
      max-wait: 60000  # 连接超时时间
      time-between-eviction-runs-millis: 60000  # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
      min-evictable-idle-time-millis: 300000  # 指定一个空闲连接最少空闲多久后可被清除，单位是毫秒
      validationQuery: select 'x'
      test-while-idle: true  # 当连接空闲时，是否执行连接测试
      test-on-borrow: false  # 当从连接池借用连接时，是否测试该连接
      test-on-return: false  # 在连接归还到连接池时是否测试该连接
      filters: config,wall,stat  # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
      poolPreparedStatements: true
      maxPoolPreparedStatementPerConnectionSize: 20
      maxOpenPreparedStatements: 20
      connectionProperties: druid.stat.slowSqlMillis=200;druid.stat.logSlowSql=true;config.decrypt=false # 特别注意decrypt
      web-stat-filter:
        enabled: true
        url-pattern: /*
        exclusions: /druid/*,*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico
        session-stat-enable: true
        session-stat-max-count: 10
      stat-view-servlet:
        enabled: true
        url-pattern: /druid/*
        reset-enable: true
        login-username: admin
        login-password: admin
```

## 读/写隔离
- [fescar AT-Mode]

### 写隔离
- 一阶段本地事务提交前，需要确保先拿到 **全局锁** 。
- 拿不到 **全局锁** ，不能提交本地事务。
- 拿 **全局锁** 的尝试被限制在一定范围内，超出范围将放弃，并回滚本地事务，释放本地锁。

（更详细的参考wiki）**感觉这是导致吞吐量过低的主要原因！**

1. **脏写**
由wiki可知（可能描述更好）。
tx1在1-phase后，会释放**本地锁**。同时，tx2可以在1-phase获取到**本地锁**，但tx2无法获取到**全局锁**（tx1并未释放**全局锁**），所以tx2无法提交本地事务。

tx1如果是2-phase-rollback，那么需要重新获取该数据的**本地锁**，但此时被tx2持有，同时tx2在等待**全局锁**。

此时tx1无法获取到**本地锁**，所以tx1的分支回滚会失败，并一直重试。
直到tx2等待获取**全局锁**超时，放弃获取**全局锁**并回滚本地事务和释放**本地锁**。
tx1之后的某次重试能成功获取到**本地锁**，最终回滚成功。

整个过程，**全局锁** 在tx1结束前一直被tx1持有，所以不会发生**脏写**的问题。


2. **全局锁** 
wiki中写到: 提交前，向 TC 注册分支：申请 product 表中，主键值等于 1 的记录的 全局锁 。

疑问: 这个 全局锁 指的到底是什么？
之前理解类似表锁，但wiki中的这句话更像是行锁（提升吞吐量）。并且感觉 行锁，更合理。


3. 2-phase-rollback（二阶段回滚）
  1、undo_log表中的记录 更新前、更新后的数据。
  2、如果是update，则根据记录中的 更新前数据，生成`update xx set filed = value`。如果是insert，则执行`delete`，
  例如:
    storage#total = 1000。 扣减库存 total = total - 40 = 960。此时undo_log中记录了 `before: {total: 1000}, after: {total: 960}`。
    当2-phase-rollback，则解析生成回滚SQL `update storage set total = 1000`。
  源代码参考: `com.alibaba.fescar.rm.datasource.undo.UndoLogManager#undo(DataSourceProxy dataSourceProxy, String xid, long branchId)`



## 测试

1. 正常请求及结果
```
POST http://127.0.0.1:8080/business/buy
{
	"userId": 1,
	"commodityCode": "C201901140001",
	"name": "name",
	"total": 20,
	"amount": 400
}

RESPONSE >>>>
{
    "status": 200,
    "message": "成功",
    "data": null
}

DATABASE >>>>
t_account.amount: 4000 - 400 = 3600
t_order: insert one row, {userId=1,commodity_code=C201901140001, total=20, amount=400}
t_storage.total: 1000 - 20 = 980
```

2. 异常全局回滚
```
RESPONSE >>>>
{
    "timestamp": "2019-04-01T02:08:04.897+0000",
    "status": 500,
    "error": "Internal Server Error",
    "message": "测试抛异常后，分布式事务回滚！",
    "path": "/business/buy"
}
```

## 备注
### 1. Error creating bean with name 'dataSourceProxy': Requested bean is currently in creation: Is there an unresolvable circular reference?
```
***************************
APPLICATION FAILED TO START
***************************

Description:

The dependencies of some of the beans in the application context form a cycle:

   servletEndpointRegistrar defined in class path resource [org/springframework/boot/actuate/autoconfigure/endpoint/web/ServletEndpointManagementContextConfiguration$WebMvcServletEndpointManagementContextConfiguration.class]
      ↓
   healthEndpoint defined in class path resource [org/springframework/boot/actuate/autoconfigure/health/HealthEndpointConfiguration.class]
      ↓
   healthIndicatorRegistry defined in class path resource [org/springframework/boot/actuate/autoconfigure/health/HealthIndicatorAutoConfiguration.class]
      ↓
   org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthIndicatorAutoConfiguration
┌─────┐
|  dataSourceProxy defined in class path resource [com/vergilyn/examples/config/FescarConfiguration.class]
└─────┘
```

本来打算是想通过`com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure`来完成对DruidDataSource的register。
但是会出现以上failed提示。

如果`@Import(DruidDataSourceAutoConfigure.class)`一切正常，但是无法正确register `EntityManagerFactoryBuilder`。
此时并未加载`org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration`，导致并不存在bean `EntityManagerFactoryBuilder`。

具体并不清楚，经过各种尝试，虽然解决了问题。但并未了解其原由，感觉是Bean实例化顺序的问题。

注意到DruidDataSourceAutoConfigure中的源码有`@AutoConfigureBefore(DataSourceAutoConfiguration.class)`
```
@Configuration
@ConditionalOnClass(DruidDataSource.class)
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties({DruidStatProperties.class, DataSourceProperties.class})
@Import({DruidSpringAopConfiguration.class,
    DruidStatViewServletConfiguration.class,
    DruidWebStatFilterConfiguration.class,
    DruidFilterConfiguration.class})
public class DruidDataSourceAutoConfigure {

    private static final Logger LOGGER = LoggerFactory.getLogger(DruidDataSourceAutoConfigure.class);

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        LOGGER.info("Init DruidDataSource");
        return new DruidDataSourceWrapper();
    }
}
```


正确的bean实例代码调用顺序: 
```
com.vergilyn.examples.config.FescarConfiguration#dataSource
↓
com.vergilyn.examples.config.FescarConfiguration#dataSourceProxy
↓
org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration#entityManagerFactoryBuilder
↓
com.vergilyn.examples.config.FescarConfiguration#entityManagerFactory
↓
com.vergilyn.examples.config.FescarConfiguration#jdbcTemplate
↓
com.vergilyn.examples.config.FescarConfiguration#namedParameterJdbcTemplate
↓
com.vergilyn.examples.config.FescarConfiguration#transactionManager
```

错误的调用顺序是，在DataSourceProxy实例化后，并未执行`JpaBaseConfiguration#entityManagerFactoryBuilder`，导致不存在实例`EntityManagerFactoryBuilder`。
尝试过跟着实例化的顺序做代码上的修改，并未找到解决方法。


[fescar-samples Github]: https://github.com/fescar-group/fescar-samples
[fescar AT-Mode]: (https://github.com/alibaba/fescar/wiki/AT-Mode)

### 2. Unable to commit against JDBC Connection
```
2019-04-10 17:16:10.572  INFO 19340 --- [nio-8083-exec-5] c.v.e.service.impl.StorageServiceImpl    : 全局事务 begin, XID = 10.2.14.20:8091:2008615616, CommodityDTO(id=null, commodityCode=C201901140001, name=false, total=40)
2019-04-10 17:16:10.574  INFO 19340 --- [nio-8083-exec-5] c.v.e.service.impl.StorageServiceImpl    : 全局事务 end, XID = 10.2.14.20:8091:2008615616, CommodityDTO(id=null, commodityCode=C201901140001, name=false, total=40)
org.springframework.orm.jpa.JpaSystemException: Unable to commit against JDBC Connection; nested exception is org.hibernate.TransactionException: Unable to commit against JDBC Connection
	at org.springframework.orm.jpa.vendor.HibernateJpaDialect.convertHibernateAccessException(HibernateJpaDialect.java:351)
	at org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:253)
	at org.springframework.orm.jpa.JpaTransactionManager.doCommit(JpaTransactionManager.java:536)
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.processCommit(AbstractPlatformTransactionManager.java:746)
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.commit(AbstractPlatformTransactionManager.java:714)
	......
Caused by: org.hibernate.TransactionException: Unable to commit against JDBC Connection
	at org.hibernate.resource.jdbc.internal.AbstractLogicalConnectionImplementor.commit(AbstractLogicalConnectionImplementor.java:87)
	at org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorImpl$TransactionDriverControlImpl.commit(JdbcResourceLocalTransactionCoordinatorImpl.java:272)
	at org.hibernate.engine.transaction.internal.TransactionImpl.commit(TransactionImpl.java:98)
	at org.springframework.orm.jpa.JpaTransactionManager.doCommit(JpaTransactionManager.java:532)
	... 75 more
Caused by: com.alibaba.fescar.rm.datasource.exec.LockConflictException
	at com.alibaba.fescar.rm.datasource.ConnectionProxy.recognizeLockKeyConflictException(ConnectionProxy.java:122)
	at com.alibaba.fescar.rm.datasource.ConnectionProxy.processGlobalTransactionCommit(ConnectionProxy.java:173)
	at com.alibaba.fescar.rm.datasource.ConnectionProxy.commit(ConnectionProxy.java:150)
	at org.hibernate.resource.jdbc.internal.AbstractLogicalConnectionImplementor.commit(AbstractLogicalConnectionImplementor.java:81)
	... 78 more
```

- [issues#199 LockConflictException](https://github.com/seata/seata/issues/199)
- [issues#220 BranchSession not release lock after GlobalSession is timeout](https://github.com/seata/seata/issues/220)

异常原因是，fescar-server中锁未释放。导致其他事务在提交时，无法获取到该条数据的锁。

但具体造成原因并未很好复现流程（并发下容易出现），并不是@GlobalTransaction timeout造成。
