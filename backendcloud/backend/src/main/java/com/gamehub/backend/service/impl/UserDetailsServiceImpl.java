package com.gamehub.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gamehub.backend.mapper.UserMapper;
import com.gamehub.backend.pojo.User;
import com.gamehub.backend.service.impl.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service  // 不加的话15行的autowired报错：必须在有效 Spring Bean 中定义自动装配成员(@Component|@Service|…)
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    // 用数据库时加上，依赖注入的作用，把UserMapper上下文中类型匹配的对象注入道userMapper字段里
    private UserMapper userMapper;

    @Override   // 与数据库的用户名对应
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // 在上述代码中，没有直接判断输入的用户名和密码是否匹配的代码。这段代码主要是用于根据用户名从数据库中查询用户信息，并将查询结果封装成一个实现了 UserDetails 接口的对象 UserDetailsImpl 返回。在认证过程中，Spring Security 会自动将用户输入的密码与 UserDetails 对象中的密码进行比对，以判断是否匹配。
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();  // 注意这个User打字时要选择pojo的User
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        return new UserDetailsImpl(user); // UserDetailsImpl 类实现了UserDetails 接口，该接口是 Spring Security 框架用于身份验证的核心接口。在身份验证过程中，Spring Security 会自动根据用户输入的用户名和密码以及 UserDetails 对象中的用户名和密码进行比对，判断是否匹配。具体的比对逻辑是在 Spring Security 的认证过滤器链中处理的，并不需要在 UserDetailsImpl 类中显式编写代码。
    }
}
