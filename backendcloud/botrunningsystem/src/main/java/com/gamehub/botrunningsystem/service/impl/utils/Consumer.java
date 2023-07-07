package com.gamehub.botrunningsystem.service.impl.utils;

import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class Consumer extends Thread {
    private Bot bot;
    private static RestTemplate restTemplate;
    private final static String receiveBotMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        Consumer.restTemplate = restTemplate;
    }

    public void startTimeout(long timeout, Bot bot) {
        this.bot = bot;
        this.start();  // 开始这个线程

        try {
            this.join(timeout);  // 最多等待timeout毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.interrupt();   // 中断当前线程
        }
    }

    private String addUid(String code, String uid) {  // 在Code中的Bot名后面加uid
        int k = code.indexOf(" implements java.util.function.Supplier<Integer>");
        return code.substring(0, k) + uid + code.substring(k);
    }

    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0, 8);
        // 要保证每次的类名不一样，因为类名一样的话就不会重复编译了。所以加uid
        Supplier<Integer> botInterface = Reflect.compile(
                "com.gamehub.botrunningsystem.utils.Bot" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();  //编译完了以后创建一个类然后获取这个类

        File file = new File("input.txt");
        try (PrintWriter fout = new PrintWriter(file)) {
            fout.println(bot.getInput());  // 将 bot.getInput() 的内容（现在地图的情况）写入文件：这个input是game.java -> botrunningserviceImpl -> BotPool里的addbot -> consume
            fout.flush();  // PrintWriter 类内部维护了一个输出缓冲区，它会将写入的数据暂时保存在缓冲区中，而不是每次都直接写入目标文件。当调用 fout.println(bot.getInput()) 时，数据会被写入缓冲区，但不一定会立即写入文件。通过调用 fout.flush()，可以强制将缓冲区中的内容刷新到目标文件中，确保数据被完全写入。
        } catch (FileNotFoundException e) {  // 无论是正常执行还是发生异常，PrintWriter 对象都会在 try 块结束后自动关闭，不需要在 catch 块中显式关闭
            throw new RuntimeException(e);
        }


        // 将字符串中的代码编译为一个类，并通过反射创建该类的实例。这种动态编译的方式通常用于在运行时生成代码，以实现动态性和灵活性。
        Integer direction = botInterface.get();
        System.out.println("move-direction: " + bot.getUserId() + " " + direction);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", direction.toString());

        restTemplate.postForObject(receiveBotMoveUrl, data, String.class);
    }
}