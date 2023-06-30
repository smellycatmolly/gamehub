package com.gamehub.backend.service.impl.user.account;

import com.gamehub.backend.pojo.User;
import com.gamehub.backend.service.impl.utils.UserDetailsImpl;
import com.gamehub.backend.service.user.account.InfoService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InfoServiceImpl implements InfoService {
    @Override
    public Map<String, String> getinfo() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();  // 将从 SecurityContextHolder 中获取的 Authentication 对象转换为 UsernamePasswordAuthenticationToken 类型。

        UserDetailsImpl loginUser = (UserDetailsImpl) authentication.getPrincipal();  // 将 Authentication 对象中的 Principal 对象转换为 UserDetailsImpl 类型。Principal 对象代表当前经过身份验证的用户。
        User user = loginUser.getUser();  // 从 UserDetailsImpl 对象中获取 User 对象，该对象包含了用户的详细信息。

        Map<String, String> map = new HashMap<>();
        map.put("error_message", "success");
        map.put("id", user.getId().toString());
        map.put("username", user.getUsername());
        map.put("photo", user.getPhoto());
        return map;
    }
}
