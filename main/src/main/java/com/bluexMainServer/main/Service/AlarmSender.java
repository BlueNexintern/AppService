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

    public void sendAlarm(Long memberId, String message) {
        // 하나의 문자열로 합치기
        String payload = memberId + "|" + message;

        rabbitTemplate.convertAndSend(
                RabbitConfig.QUEUE_NAME,
                payload
        );

        System.out.println("[x] 큐로 전달: " + payload);
    }
}