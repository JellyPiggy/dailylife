package cn.itcast.mq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Configuration
public class AmqpConfig {
    @Bean
    public Queue simpleQueue() {
        return new Queue("simple.queue");
    }

    @Bean
    public Queue workQueue() {
        return new Queue("work.queue");
    }

    @Bean
    public Queue objectQueue() {
        return new Queue(("object.queue"));
    }
}
