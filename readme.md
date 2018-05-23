## 统一打印 Controller 请求和相应日志
 
### 使用方法
1、添加依赖

```
        <dependency>
            <groupId>com.ibeiliao</groupId>
            <artifactId>spring-boot-starter-web-log</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>

```

2、添加配置
```
web.log:
    ## 拦截路径
    mapping-path: "/*"
    ## 排除路径
    exclude-mapping-path: "/files/*;/swagger*;/webjars/*;/favicon.ico;/login;/captcha/*;/v2/api-docs"
    ## 打印header,多个按照';'分隔
    print-header: "Authorization"
    ## 是否允许打印日志，默认true,建议生成关闭
    enable: true
```

