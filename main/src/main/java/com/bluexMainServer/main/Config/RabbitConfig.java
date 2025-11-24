package com.bluexMainServer.main.Config;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@Configuration
@EnableRabbit
public class RabbitConfig {

    public static final String QUEUE_NAME = "alarm.queue";

    @Bean
    public Queue alarmQueue() {
        // durable = true → 서버 재시작해도 큐 유지
        return new Queue(QUEUE_NAME, true);
    }
}