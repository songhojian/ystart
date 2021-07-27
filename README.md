## 项目介绍
- 以人科技低代码项目

### 项目配置
127.0.0.1 ystart-register
127.0.0.1 ystart-gateway
127.0.0.1 ystart-redis
43.247.89.132 ystart-mysql
103.229.214.106  ystart-mongo
127.0.0.1 ystart-sentinel
127.0.0.1 ystart-monitor

### 项目启动步骤
- 清空redis: redis-cli 进入命令行，然后清空 flushall
- ystart-register/YstartNacosApplication.java
- ystart-upms-biz/YstartAdminApplication   [注意启动完毕输出路由初始化完毕再去启动其他模块]
- ystart-auth/YstartAuthApplication
- ystart-gateway/YstartGatewayApplication
- ystart-visual/ystart-lowcode/YstartLowcodeApplication

### 🚫禁止
源码不允许分享
