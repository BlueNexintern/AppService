package com.bluexMainServer.main.Listener;


import com.bluexMainServer.main.Config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AlarmListener {

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void handleMessage(String message) {
        System.out.println(" [>] 받은 메시지: " + message);
        // 여기서 나중에 NHN Push 호출, DB 상태 업데이트 등 하면 됨
    }
}
