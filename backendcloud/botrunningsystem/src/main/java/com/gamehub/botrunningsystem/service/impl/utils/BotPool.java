package com.gamehub.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Queue<Bot> bots = new LinkedList<>();  // 这个队列不是线程安全的，但是我加锁让他变得安全

    public void addBot(Integer userId, String botCode, String input) {
        lock.lock();
        try {
            bots.add(new Bot(userId, botCode, input));
            condition.signalAll();  // 唤起另外的所有的线程，在这里指：如果一开始因为队列为空，另一个线程在condition.await里睡住了，然后被叫醒
        } finally {
            lock.unlock();
        }
    }

    private void consume(Bot bot) {  // 为了防止用户写出来的代码死循环，要加上线程，因为线程可以控制时间
        Consumer consumer = new Consumer();
        consumer.startTimeout(2000, bot);
    }

    @Override
    public void run() {
        while(true) {
            lock.lock();  // 因为队列里既有把待运行的代码放入队列的过程，又有把代码拿出来运行的过程，所以要加锁
            if (bots.isEmpty()) {
                try {
                    condition.await();  // 每当队列里没有需要执行的代码了，就把线程阻塞掉。睡住以后会自动把锁释放。等signalAll的时候，又恢复了，循环执行else里的代码
                } catch (InterruptedException e) {  // 报异常时：打印异常、解锁、结束循环
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            } else {
                Bot bot = bots.remove();  // 返回并删除队头
                lock.unlock();
                consume(bot); // 比较耗时，需要编译代码，可能执行好几秒钟。所以要在这之前上一行先把锁释放。因为已经读完了bot了就可以释放了
            }  // 手动实现一个消息队列
        }
    }
}
