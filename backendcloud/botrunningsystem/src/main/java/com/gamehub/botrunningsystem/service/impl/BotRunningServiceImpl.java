package com.gamehub.botrunningsystem.service.impl;

import com.gamehub.botrunningsystem.service.BotRunningService;
import com.gamehub.botrunningsystem.service.impl.utils.BotPool;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {
    public final static BotPool botPool = new BotPool();  // new一个线程，线程的启动在Application之前的Springboot启动前启动

    @Override
    public String addBot(Integer userId, String botCode, String input) {
        System.out.println("add bot: " + userId + " " + botCode + " " + input);
        botPool.addBot(userId, botCode, input);
        return "add bot success";
    }
}
