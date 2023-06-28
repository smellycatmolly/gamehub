package com.gamehub.backend.config.filter;

import com.gamehub.backend.mapper.UserMapper;
import com.gamehub.backend.pojo.User;
import com.gamehub.backend.service.impl.utils.UserDetailsImpl;
import com.gamehub.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);

        String userid;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        User user = userMapper.selectById(Integer.parseInt(userid));

        if (user == null) {
            throw new RuntimeException("用户名未登录");
        }

        UserDetailsImpl loginUser = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, null);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}

//这段代码是一个过滤器（Filter），用于在每个请求被处理前进行身份验证和授权的操作。下面是整体作用的解释：
//
//该代码位于 com.gamehub.backend.config.filter 包下，属于项目中的一个过滤器组件。
//
//该过滤器类名为 JwtAuthenticationTokenFilter，继承自 OncePerRequestFilter 类，意味着它是一个只执行一次的请求过滤器。
//
//该过滤器被标注为 @Component，以便被 Spring 框架自动扫描并注入到应用程序的上下文中。
//
//过滤器的核心逻辑在 doFilterInternal 方法中，该方法在每个请求被处理时会被调用。
//
//首先，从请求头中获取名为 "Authorization" 的信息，该信息包含了用户的身份认证凭证（token）。
//
//如果凭证为空或不以 "Bearer " 开头，则直接放行该请求，不进行身份验证和授权的操作。
//
//如果凭证有效，提取出其中的 token 信息，并通过 JwtUtil 解析出其中包含的用户 ID。
//
//使用该用户 ID 查询数据库，获取用户信息。
//
//如果用户不存在，抛出异常 "用户名未登录"。
//
//如果用户存在，创建一个 UserDetailsImpl 对象，将查询到的用户信息封装进去。
//
//创建一个 UsernamePasswordAuthenticationToken 对象，使用上述创建的 UserDetailsImpl 对象作为主体（Principal），并设置为已认证状态。
//
//将上述的认证信息设置到 Spring Security 的安全上下文中（SecurityContextHolder）。
//
//最后，将请求和响应传递给下一个过滤器或处理器进行处理。
//
//总的来说，该过滤器的作用是从请求头中提取认证凭证（token），验证凭证的有效性，并将认证后的用户信息设置到 Spring Security 的上下文中，以便后续的身份验证和授权操作使用。这样，每个请求在经过该过滤器时都会进行身份认证，保护需要授权访问的资源。