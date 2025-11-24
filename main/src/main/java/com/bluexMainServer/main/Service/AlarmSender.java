package com.bluexMainServer.main.Service;

import com.bluexMainServer.main.Config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlarmSender {

    private final RabbitTemplate rabbitTemplate;

    public AlarmSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendAlarm(String message) {
        // 기본 익스체인지(빈 문자열) + 큐 이름을 routingKey로 사용
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, message);
        System.out.println(" [x] 보낸 메시지: " + message);
    }
}