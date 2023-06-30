package com.gamehub.backend.service.impl.pk;

import com.gamehub.backend.consumer.WebSocketServer;
import com.gamehub.backend.service.pk.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer aId, Integer bId) {
        System.out.println("start game: " + aId + " " + bId);
        WebSocketServer.startGame(aId, bId);
        return "start game success";
    }
}
