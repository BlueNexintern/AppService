package com.bluexMainServer.main.Listener;

import com.bluexMainServer.main.Config.RabbitConfig;
import com.bluexMainServer.main.Dto.MockPushRequest;
import com.bluexMainServer.main.Dto.MockPushResponse;
import com.bluexMainServer.main.Entity.SendStatus;
import com.bluexMainServer.main.Repository.AlarmSendRepository;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.TimeUnit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AlarmListener {

    private final RestTemplate restTemplate;
    private final AlarmSendRepository alarmSendRepository;

    @Autowired
    MeterRegistry registry;

    public AlarmListener(RestTemplate restTemplate, AlarmSendRepository alarmSendRepository) {
        this.restTemplate = restTemplate;
        this.alarmSendRepository = alarmSendRepository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void handleMessage(String payload) {
        long start = System.currentTimeMillis();

        // "alarmSendId|memberId|message" 형식을 파싱
        String[] parts = payload.split("\\|", 3);

        if (parts.length < 3) {
            System.out.println(" 잘못된 메시지 형식: " + payload);
            return;
        }

        Long alarmSendId;
        Long memberId;
        try {
            alarmSendId = Long.parseLong(parts[0]);
            memberId = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            System.out.println(" ID 파싱 실패: " + payload);
            return;
        }

        String message = parts[2];

        MockPushRequest request = new MockPushRequest(memberId, message);
        try {
            MockPushResponse response = restTemplate.postForObject(
                    "http://localhost:8081/mock/nhn/push",
                    request,
                    MockPushResponse.class
            );

            if (response != null && response.success()) {
                registry.counter("alarm.consume.success").increment();
                updateStatus(alarmSendId, SendStatus.SENT, null);
            } else {
                String errorCode = (response != null) ? response.errorCode() : "NO_RESPONSE";
                registry.counter("alarm.consume.fail").increment();
                updateStatus(alarmSendId, SendStatus.FAILED, errorCode);
            }
        } catch (Exception e) {
            registry.counter("alarm.consume.fail").increment();
            updateStatus(alarmSendId, SendStatus.FAILED, "HTTP_ERROR");
        }

        long end = System.currentTimeMillis();
        registry.timer("alarm.consume.latency").record(end - start, TimeUnit.MILLISECONDS);
    }

    private void updateStatus(Long alarmSendId, SendStatus status, String errorCode) {
        alarmSendRepository.findById(alarmSendId).ifPresent(alarmSend -> {
            alarmSend.setStatus(status);
            alarmSend.setLastErrorCode(errorCode);
            alarmSendRepository.save(alarmSend);
        });
    }
}
