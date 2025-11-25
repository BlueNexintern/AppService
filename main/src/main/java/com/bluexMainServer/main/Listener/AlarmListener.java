package com.bluexMainServer.main.Listener;


import com.bluexMainServer.main.Config.RabbitConfig;
import com.bluexMainServer.main.Dto.MockPushRequest;
import com.bluexMainServer.main.Dto.MockPushResponse;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.TimeUnit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AlarmListener {

    private final RestTemplate restTemplate;

    @Autowired
    MeterRegistry registry;

    public AlarmListener(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void handleMessage(String payload) {
//        System.out.println(" [>] 받은 메시지(raw): " + payload);
        long start = System.currentTimeMillis();

        // "memberId|message" 형식을 파싱
        String[] parts = payload.split("\\|", 2); // 최대 2개로만 split

        if (parts.length < 2) {
            System.out.println(" 잘못된 메시지 형식: " + payload);
            return;
        }

        Long memberId;
        try {
            memberId = Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            System.out.println(" memberId 파싱 실패: " + parts[0]);
            return;
        }

        String message = parts[1];
//        System.out.println("    memberId = " + memberId);
//        System.out.println("    message  = " + message);

        // 여기서 Mock 서버로 호출
        MockPushRequest request = new MockPushRequest(memberId, message);
        try {
            MockPushResponse response = restTemplate.postForObject(
                    "http://localhost:8081/mock/nhn/push",
                    request,
                    MockPushResponse.class
            );

            if (response != null && response.success()) {
//                System.out.println(" Mock 서버 전송 성공");
                registry.counter("alarm.consume.success").increment();
                // TODO: memberId 기준으로 DB 상태 SENT로 업데이트
            } else {
                String errorCode = (response != null) ? response.errorCode() : "NO_RESPONSE";
//                System.out.println(" Mock 서버 전송 실패, errorCode=" + errorCode);
                // TODO: DB에 FAILED + errorCode 기록
                registry.counter("alarm.consume.fail").increment();
            }
        } catch (Exception e) {
//            System.out.println(" Mock 서버 호출 예외: " + e.getMessage());
            // TODO: DB에 FAILED + HTTP_ERROR 기록
            registry.counter("alarm.consume.fail").increment();
        }

        long end = System.currentTimeMillis();
        registry.timer("alarm.consume.latency").record(end - start, TimeUnit.MILLISECONDS);
    }
}
