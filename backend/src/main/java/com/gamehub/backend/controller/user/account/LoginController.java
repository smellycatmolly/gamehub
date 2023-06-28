package com.gamehub.backend.controller.user.account;

import com.gamehub.backend.service.user.account.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController  // @RestController 注解表示该类是一个控制器，用于处理 HTTP 请求和响应，并以 JSON 或 XML 格式返回结果。
public class LoginController {
    @Autowired
    private LoginService loginService;  // 注入接口：@Autowired 注解用于自动装配依赖对象，这里将 LoginService 接口的实现类：LoginServiceImpl注入到 loginService 字段中。

    @PostMapping("/user/account/token")  // 记得把这个地址在SecurityConfig里放行、公开化。@PostMapping("/user/account/token") 注解指定了处理 HTTP POST 请求的路由路径为 "/user/account/token"。当接收到该请求时，将调用相应的方法进行处理。
    public Map<String, String> getToken(@RequestParam Map<String, String> map) {  // LoginService 接口是一个服务接口，定义了业务逻辑的方法。loginService 对象是通过依赖注入的方式实例化的，其实际类型是 LoginServiceImpl，即 LoginService 接口的实现类。
        String username = map.get("username");  // @RequestParam Map<String, String> map: 该注解用于将请求中的参数映射到方法的参数上。@RequestParam 注解表示这个 map 参数将从请求的查询参数中获取，每个查询参数的名称将作为 map 的键，对应的值将作为 map 的值。
        String password = map.get("password");
        return loginService.getToken(username, password);
    }
}
