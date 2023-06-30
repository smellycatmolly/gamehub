package com.gamehub.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.gamehub.backend.consumer.utils.Game;
import com.gamehub.backend.consumer.utils.JwtAuthentication;
import com.gamehub.backend.mapper.RecordMapper;
import com.gamehub.backend.mapper.UserMapper;
import com.gamehub.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component  // WebSocketServer 类声明为一个 Spring 组件，以便进行自动扫描和注入。
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾  注解将它标记为 WebSocket 服务器端的端点。  注解指定 WebSocket 服务器端的访问路径为 "/websocket/{token}"。客户端可以通过此路径连接到 WebSocket 服务器。
public class WebSocketServer {

    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();  // 存储所有连接的 WebSocketServer 实例  // 线程安全的哈希表：把用户ID映射到线程  // 加static静态变量：对于所有实例来说访问的都是同一个哈希表
    private User user;
    private Session session = null;  // 定义一个 Session 对象，用于维护与客户端的 WebSocket 会话。每个链接通过session来维护。session 在这里代表了与客户端建立的 WebSocket 连接会话对象
    // 另一种注入数据库的方式：Spring 容器在启动时会创建该组件的单个实例，并在需要时将该实例注入到其他组件中。这样可以确保在整个应用程序中共享同一个组件实例，避免了多个实例之间的状态冲突和资源浪费。但是websocket不是单例模式，所以不能像controller里一样简单注入
    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    private static RestTemplate restTemplate;
    private Game game = null;
    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper  = userMapper;  // 静态变量userMapper被访问时要通过类名访问
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper  = recordMapper;  // 静态变量recordMapper被访问时要通过类名访问
    }
    @Autowired   // 加了Autowired的话就会看这个restTemplate是不是config里有唯一的Bean注解和它对应，如果有的话就返回return
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {  // 建立连接:Session session：表示当前建立的 WebSocket 会话对象。通过该对象可以与客户端进行通信，发送和接收消息等操作。使用 @PathParam 注解指定的路径参数，用于接收从客户端传递过来的名为 "token" 的参数值。路径参数是在客户端通过 WebSocket 连接 URL 中的占位符进行传递的。
        this.session = session;  // 将传入的 session 对象赋值给实例变量 session。this.session 是一个实例变量，它在类的其他方法中也可以被访问和使用。
        System.out.println("connected!");
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);

        if (this.user != null) {
            users.put(userId, this);
        } else {
            this.session.close();
        }

        System.out.println(users);
    }

    @OnClose
    public void onClose() {  // 关闭链接
        System.out.println("disconnected!");
        if (this.user != null) {
            users.remove(this.user.getId());
        }
    }

    public static void startGame(Integer aId, Integer bId) {  // 为了在StartGameServiceImpl里通过类名调用这个函数，要加上static
        User a = userMapper.selectById(aId), b = userMapper.selectById(bId);

        Game game = new Game(13, 14, 20, a.getId(), b.getId());
        game.createMap();
        if (users.get(a.getId()) != null)
            users.get(a.getId()).game = game;  //  users.get(a.getId())是一个WebSocketServer对象，有game属性
        if (users.get(b.getId()) != null)
            users.get(b.getId()).game = game;  //  如果玩家a在匹配时没有点取消而是直接下线，那么users.get(a.getId())是一个空的WebSocketServer对象，就会报错！所以要加一个判断考虑这种情况。加了判断以后，谁匹配时下线了谁就会输


        game.start();  // 另起一个线程执行：一局游戏对应一个线程

        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getG());

        JSONObject respA = new JSONObject();
        respA.put("event", "start-matching");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respA.put("game", respGame);
        if (users.get(a.getId()) != null)
            users.get(a.getId()).sendMessage(respA.toJSONString());  // 通过a的id找到a的session的线程，把respA转换为对应的 JSON 字符串发送给a，以便进行网络传输或存储等操作。

        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", respGame);
        if (users.get(b.getId()) != null)
            users.get(b.getId()).sendMessage(respB.toJSONString());
    }

    private void startMatching() {
        System.out.println("start matching!");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        restTemplate.postForObject(addPlayerUrl, data, String.class);
    }

    private void stopMatching() {
        System.out.println("stop matching!");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }

    private void move(int direction) {   //  判断是蛇a还是蛇b
        if (game.getPlayerA().getId().equals(user.getId())) {
            game.setNextStepA(direction);
        } else if (game.getPlayerB().getId().equals(user.getId())) {
            game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {  // 从Client接收消息（前端到后端） //功能：路由：决定下一站做判断
        System.out.println("receive message!");
        JSONObject data = JSONObject.parseObject(message);  // 首先使用 JSONObject.parseObject(message) 将 JSON 字符串 message 解析为 JSONObject 对象 data。
        String event = data.getString("event");  // 然后通过 data.getString("event") 获取名为 "event" 的字段的值，并将其存储在 String 类型的变量 event 中。
        if ("start-matching".equals(event)) {
            startMatching();
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("move".equals(event)) {
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) {  // 后端发送给前端
        synchronized (this.session) {  // 异步加锁：当有多个线程同时访问该代码块时，只有一个线程能够获得对 this.session 的锁定，其他线程将被阻塞，直到获取锁定的线程释放锁。这样可以确保在同一时间只有一个线程能够访问或修改 this.session 对象，从而避免了多线程之间可能发生的数据竞争和不一致性问题。
            try {
                this.session.getBasicRemote().sendText(message);  // 通过 session 对象的 getBasicRemote() 方法获取与客户端通信的远程端点（RemoteEndpoint）对象，并使用 sendText() 方法发送文本消息给客户端。message 参数是要发送的消息内容。
            } catch (IOException e) {  // 文件操作错误：例如文件不存在、无法读取或写入文件等。网络操作错误：例如网络连接中断、超时、协议错误等。输入输出流关闭错误：在关闭输入输出流时发生异常。其他输入输出相关的异常情况。
                e.printStackTrace();  // 在发生 IOException 异常时，将异常信息打印到标准错误流。
            }
        }
    }
}