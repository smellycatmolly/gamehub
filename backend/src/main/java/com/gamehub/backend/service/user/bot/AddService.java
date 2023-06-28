package com.gamehub.backend.service.user.bot;

import java.util.Map;

public interface AddService {
    Map<String, String> add(Map<String, String> data);  // 接口里的函数add默认是public所以这行开头省略了public
}
