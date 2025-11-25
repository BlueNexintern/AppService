package com.bluexMainServer.main.Controller;




import com.bluexMainServer.main.Service.AlarmSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitTestController {

    private final AlarmSender alarmSender;

    public RabbitTestController(AlarmSender alarmSender) {
        this.alarmSender = alarmSender;
    }

    @GetMapping("/send-test")
    public String sendTest() {
        Long memberId = 123L; // 임시 (나중에 DB에서 Member 가져감)
        String message = "서원유통 알림 테스트!";

        alarmSender.sendAlarm(memberId, message);
        return "sent";
    }
}
