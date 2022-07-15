package cn.itcast.mq.listener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 晓蝈
 * @version 1.0
 */

@Component
public class SpringRabbitListener {

    //@RabbitListener(queues = "simple.queue")
    //public void simpleQueue(String message) {
    //    System.out.println("simple.queue：" + message);
    //}
    //
    //@RabbitListener(queues = "work.queue")
    //public void workQueue1(String message) throws InterruptedException {
    //    System.out.println("work.queue：" + message);
    //    Thread.sleep(20);
    //}
    //
    //@RabbitListener(queues = "work.queue")
    //public void workQueue2(String message) throws InterruptedException {
    //    System.err.println("work.queue：" + message);
    //    Thread.sleep(200);
    //}

    //@RabbitListener(bindings = @QueueBinding(
    //        value = @Queue(name = "fanout.queue1"),
    //        exchange = @Exchange(name = "fanout.queue", type = ExchangeTypes.FANOUT)
    //))
    //public void fanoutQueue1(String message) {
    //    System.out.println("消费者1接收到消息：" + message);
    //}
    //
    //@RabbitListener(bindings = @QueueBinding(
    //        value = @Queue(name = "fanout.queue2"),
    //        exchange = @Exchange(name = "fanout.queue", type = ExchangeTypes.FANOUT)
    //))
    //public void fanoutQueue2(String message) {
    //    System.out.println("消费者2接收到消息：" + message);
    //}

    //@RabbitListener(bindings = @QueueBinding(
    //        value = @Queue(name = "direct.queue1"),
    //        exchange = @Exchange(name = "direct.queue"),
    //        key = {"red", "blue"}
    //))
    //public void directQueue1(String message) {
    //    System.out.println("消费者1接收到消息：" + message);
    //}
    //
    //@RabbitListener(bindings = @QueueBinding(
    //        value = @Queue(name = "direct.queue2"),
    //        exchange = @Exchange(name = "direct.queue"),
    //        key = {"red", "yellow"}
    //))
    //public void directQueue2(String message) {
    //    System.out.println("消费者2接收到消息：" + message);
    //}
    //
    //@RabbitListener(bindings = @QueueBinding(
    //        value = @Queue(name = "topic.queue1"),
    //        exchange = @Exchange(name = "topic.queue", type = ExchangeTypes.TOPIC),
    //        key = "#.news"
    //))
    //public void topicQueue1(String message) {
    //    System.out.println("消费者1接收到消息：" + message);
    //}
    //
    //@RabbitListener(bindings = @QueueBinding(
    //        value = @Queue(name = "topic.queue2"),
    //        exchange = @Exchange(name = "topic.queue", type = ExchangeTypes.TOPIC),
    //        key = "china.#"
    //))
    //public void topicQueue2(String message) {
    //    System.out.println("消费者2接收到消息：" + message);
    //}

    @RabbitListener(queues = "object.queue")
    public void objectQueue(Map<String, Object> map) {
        System.out.println(map);
    }
}
