package com.gamehub.backend.service.impl.user.bot;

import com.gamehub.backend.mapper.BotMapper;
import com.gamehub.backend.pojo.Bot;
import com.gamehub.backend.pojo.User;
import com.gamehub.backend.service.impl.utils.UserDetailsImpl;
import com.gamehub.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddServiceImpl implements AddService {

    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> add(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();  // 将从 SecurityContextHolder 中获取的 Authentication 对象转换为 UsernamePasswordAuthenticationToken 类型。

        UserDetailsImpl loginUser = (UserDetailsImpl) authentication.getPrincipal();  // 将 Authentication 对象中的 Principal 对象转换为 UserDetailsImpl 类型。Principal 对象代表当前经过身份验证的用户。
        User user = loginUser.getUser();  // 从 UserDetailsImpl 对象中获取 User 对象，该对象包含了用户的详细信息。

        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Map<String, String> map = new HashMap<>();

        if (title == null || title.length() == 0) {
            map.put("error_message", "标题不能为空");
            return map;
        }

        if (title.length() > 100) {
            map.put("error_message", "标题长度不能大于100");
            return map;
        }

        if (description == null || description.length() == 0) {
            description = "这个用户很懒，什么也没留下~";
        }

        if (description.length() > 300) {
            map.put("error_message", "Bot描述的长度不能大于300");
            return map;
        }

        if (content != null && content.length() == 0) {
            map.put("error_message", "代码不能为空");
            return map;
        }

        if (content.length() > 10000) {
            map.put("error_message", "代码长度不能超过10000");
            return map;
        }

        Date now = new Date();
        Bot bot = new Bot(null, user.getId(), title, description, content, 1500, now, now);

        botMapper.insert(bot);
        map.put("error_message", "success");

        return map;
    }
}
