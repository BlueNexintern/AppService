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
        alarmSender.sendAlarm("알람 테스트!");
        return "sent";
    }
}
