# alibaba-fescar-examples 
# 不能全局回滚！！！！！！

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


大致流程体现：
1. 某个服务的事务是真实已提交。例如，线程A执行decrease-storage完，其实数据库中已经是修改后的值（storage#total = 980）。
2. 会生成undo_log(回滚日志表)， 如果全局事务回滚成功，会删除undo_log的记录。如果全局事务提交成功，则保留undo_log的记录。 （为什么要这么设计 删除/保留？）
3. 回滚： 分析undo_log，执行 update/delete 进行回滚操作。



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