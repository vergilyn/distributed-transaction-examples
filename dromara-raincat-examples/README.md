# dromara-raincat-examples


## raincat-manager  
代码来至[raincat]，2.0.0-RELEASE。只修改了pom依赖。
事务协调者(Transaction Coordinator)，采用eureka做注册中心，支持集群模式。

## 测试
1. 启动raincat-manager
注：raincat-manager即是服务注册中心（eureka-server），也是服务客户端（eureka-client）。
- `http://127.0.0.1:8761`，eureka注册中心界面
- `http://127.0.0.1:8761/index`，raincat-TxManager查看界面

2. business、storage、account、order
- 注意eureka-client的serviceUrl
- `applicationContext.xml`中，`txManagerUrl`即是raincat-manager的地址。另外compensationCacheType支持redis、db、zookeeper等，对应不同的txXxxxConfig。

## 备注
1. 不清楚为什么raincat-manager要与eureka-server捆绑在一起？

2. `applicationContext.xml`，貌似还未完全提供spring-boot的配置形式，可以自行整合。



[raincat]: https://github.com/yu199195/Raincat