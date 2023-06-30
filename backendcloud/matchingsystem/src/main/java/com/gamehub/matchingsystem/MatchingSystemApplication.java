package com.gamehub.matchingsystem;

import com.gamehub.matchingsystem.service.impl.MatchingServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MatchingSystemApplication {
    public static void main(String[] args) {
        MatchingServiceImpl.matchingPool.start();  // 启动匹配线程
        SpringApplication.run(MatchingSystemApplication.class, args);
    }
}

//  Spring Boot 应用程序的入口类。它使用了 @SpringBootApplication 注解，表示这是一个 Spring Boot 应用程序的入口点，并自动进行了一些默认的配置。
//  SpringApplication.run() 方法用于启动 Spring Boot 应用程序，并指定应用程序的入口类和命令行参数。在这个例子中，入口类是 MatchingSystemApplication 类。
//  当你运行这个应用程序时，它将启动 Spring Boot 容器，并根据配置自动装配所需的组件和依赖项。这个类是整个应用程序的起点，它将触发 Spring Boot 的自动配置、组件扫描和其他初始化过程。
//  你可以将其他的配置、控制器、服务等添加到这个应用程序中，以构建你的具体业务逻辑。