package com.bluexMainServer.main.Service;

import com.bluexMainServer.main.Config.RabbitConfig;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmSender {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    MeterRegistry registry;

    public AlarmSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendAlarm(Long alarmSendId, Long memberId, String message) {
        String payload = alarmSendId + "|" + memberId + "|" + message;

        rabbitTemplate.convertAndSend(
                RabbitConfig.QUEUE_NAME,
                payload
        );

        registry.counter("alarm.publish.count").increment();
        System.out.println("[x] 큐로 전달: " + payload);
    }
}