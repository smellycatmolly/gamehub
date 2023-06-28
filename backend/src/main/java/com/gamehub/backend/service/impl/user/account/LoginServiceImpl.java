package com.gamehub.backend.service.impl.user.account;

import com.gamehub.backend.pojo.User;
import com.gamehub.backend.service.impl.utils.UserDetailsImpl;
import com.gamehub.backend.service.user.account.LoginService;
import com.gamehub.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service  // @Service 是 Spring 框架中的一个注解，用于标记一个类为服务层组件
public class LoginServiceImpl implements LoginService {  // command+N - 实现方法

    @Autowired // 凡是用到的就要注入进来。通过在需要使用 AuthenticationManager 的地方添加 @Autowired 注解，Spring 容器会自动扫描并查找与该类型匹配的依赖对象，并将其注入到该字段中。这样，你就可以直接使用 authenticationManager 对象进行身份验证的操作，而无需手动创建或获取 AuthenticationManager 实例。
    private AuthenticationManager authenticationManager;  // 依赖注入的好处是解耦和灵活性。你可以在其他地方配置和管理 AuthenticationManager 的实例，而不需要在每个使用它的地方手动创建实例。同时，如果将来需要更换或扩展 AuthenticationManager 的实现，你只需要修改配置，而不需要修改大量依赖该对象的代码。总之，@Autowired 注解使得依赖注入变得简单和自动化，提高了代码的可读性和可维护性。

    @Override
    public Map<String, String> getToken(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);  // 把username和password封装，password存的是加密之后的

        Authentication authenticate = authenticationManager.authenticate(authenticationToken); // 如果登录失败会自动处理报异常 // authenticationManager.authenticate(authenticationToken).var回车自动返回Authentication authenticate变量类型、变量名
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();  // authenticate.getPrincipal() 返回一个 Principal 对象，该对象代表了当前经过身份验证的用户。你使用 (UserDetailsImpl) 进行强制类型转换，将 Principal 对象转换为 UserDetailsImpl 对象。UserDetailsImpl 类是自定义的实现了 Spring Security 的 UserDetails 接口的类。它包含了用户的详细信息，如用户名、密码、权限等。通过将 Principal 对象强制类型转换为 UserDetailsImpl 对象，你可以获取到该用户的详细信息。
        User user = loginUser.getUser();  // 通过 loginUser.getUser()，你获取到了 UserDetailsImpl 对象中的 User 对象。User 类是自定义的一个 POJO（Plain Old Java Object），它代表了应用程序中的用户信息，包括用户的 id、用户名、密码等。通过以上代码，你成功地获取到了经过身份验证的用户对象的详细信息，并将其赋值给 user 变量，以便后续进行处理和操作。
        String jwt = JwtUtil.createJWT(user.getId().toString());  // 创建了一个 JWT（JSON Web Token）

        Map<String, String> map = new HashMap<>();
        map.put("error_message", "success");  // 如果登录失败那么authenticate那一步就会报异常了，能运行到这一步都是成功了
        map.put("token", jwt);

        return map;  // 你成功地生成了一个 JWT，并将登录成功的信息和 JWT 封装到了一个 Map 对象中，以便后续返回给客户端。这样客户端就可以获取到该 JWT，并在后续的请求中使用它来进行身份验证和访问控制。
    }
}
