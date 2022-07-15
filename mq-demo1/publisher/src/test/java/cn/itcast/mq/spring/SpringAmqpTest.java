package cn.itcast.mq.spring;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 晓蝈
 * @version 1.0
 */
@SpringBootTest
public class SpringAmqpTest {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void simpleQueue() {
        String queueName = "simple.queue";
        String message = "hello, spring amqp";
        amqpTemplate.convertAndSend(queueName, message);
    }

    @Test
    public void workQueue() throws InterruptedException {
        String queueName = "work.queue";
        String message = "hello, work.queue";
        for (int i = 1; i <= 50; i++) {
            amqpTemplate.convertAndSend(queueName, message + " " + i);
            Thread.sleep(20);
        }
    }

    @Test
    public void fanoutQueue() {
        String exchangeName = "fanout.queue";
        String message = "hello, fanout.queue";
        amqpTemplate.convertAndSend(exchangeName, "", message);
    }

    @Test
    public void directQueue1() {
        String exchangeName = "direct.queue";
        String message = "hello, direct.queue ";
        amqpTemplate.convertAndSend(exchangeName, "blue", message + "blue");
    }

    @Test
    public void directQueue2() {
        String exchangeName = "direct.queue";
        String message = "hello, direct.queue ";
        amqpTemplate.convertAndSend(exchangeName, "yellow", message + "yellow");
    }

    @Test
    public void directQueue3() {
        String exchangeName = "direct.queue";
        String message = "hello, direct.queue ";
        amqpTemplate.convertAndSend(exchangeName, "red", message + "red");
    }

    @Test
    public void topicQueue1() {
        String exchangeName = "topic.queue";
        String message = "china news";
        amqpTemplate.convertAndSend(exchangeName, "china.news", message);
    }

    @Test
    public void topicQueue2() {
        String exchangeName = "topic.queue";
        String message = "china weather";
        amqpTemplate.convertAndSend(exchangeName, "china.weather", message);
    }

    @Test
    public void topicQueue3() {
        String exchangeName = "topic.queue";
        String message = "Japan news";
        amqpTemplate.convertAndSend(exchangeName, "Japan.news", message + "red");
    }

    @Test
    public void objectQueue() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "jack");
        map.put("age", 18);
        amqpTemplate.convertAndSend("object.queue", map);
    }

}
