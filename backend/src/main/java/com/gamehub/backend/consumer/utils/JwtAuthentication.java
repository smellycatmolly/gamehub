package com.gamehub.backend.consumer.utils;

import com.gamehub.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;

public class JwtAuthentication {
    public static Integer getUserId(String token) {  // 这函数是一个static静态类型，这样的话在外部文件调用时不需要实例就可以调用了
        int userId = -1;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = Integer.parseInt(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return userId;
    }
}
