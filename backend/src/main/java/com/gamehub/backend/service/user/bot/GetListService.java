package com.gamehub.backend.service.user.bot;

import com.gamehub.backend.pojo.Bot;

import java.util.List;

public interface GetListService {
    List<Bot> getList();  // 自己的Bot与自己的id挂钩，自己的id存在自己的token里，所以不需要传参数了
}
