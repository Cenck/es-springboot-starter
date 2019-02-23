# es-springboot-starter

#### 项目介绍
对elasticsearch的springboot封装

#### 简要说明 
1. 基于spring-boot-actuator和spring-boot-autoconfiguration实现yml配置elasticsearch;
2. 对elastic做简单的crud模板封装，建造者模式EsCretiria传递参数，返回EsResult的详细返回值。


#### 安装顺序

1. cg-parent 首先Install
2. 然后直接安装外层cg-frame下的pom

#### 使用说明
1. @EnableEs //使springboot开启Es的自动扫描配置支付
2. EsCrudTemplate //使用模板一键增删改
3. yml配置: 
```
spring:
  elasticsearch:
    host: 192.168.0.2
    port: 9300
    #集群名称
    clusterName: server01 
```
