package com.gamehub.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {  // 在两个springboot进程间进行通讯
    @Bean  // 使用@Bean注解，你可以将方法返回的对象作为Bean纳入Spring容器的管理。这样，你就可以在其他组件中使用@Autowired、@Resource或@Inject等注解来引用这个Bean，并进行依赖注入。
    public RestTemplate getRestTemplate() {
        return new RestTemplate();  // 返回一个实例
    }
}
// 通过 @Autowired 注解将 RestTemplate 注入到其他需要发送 HTTP 请求的类中，然后使用它来执行各种类型的 HTTP 请求，如 GET、POST、PUT、DELETE 等。