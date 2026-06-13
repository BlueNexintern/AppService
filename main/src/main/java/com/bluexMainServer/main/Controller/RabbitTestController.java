package com.bluexMainServer.main.Controller;

import com.bluexMainServer.main.Entity.Alarm;
import com.bluexMainServer.main.Entity.AlarmSend;
import com.bluexMainServer.main.Entity.Member;
import com.bluexMainServer.main.Entity.SendStatus;
import com.bluexMainServer.main.Repository.AlarmRepository;
import com.bluexMainServer.main.Repository.AlarmSendRepository;
import com.bluexMainServer.main.Repository.MemberRepository;
import com.bluexMainServer.main.Service.AlarmSender;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitTestController {

    private final AlarmSender alarmSender;
    private final MemberRepository memberRepository;
    private final AlarmRepository alarmRepository;
    private final AlarmSendRepository alarmSendRepository;

    public RabbitTestController(AlarmSender alarmSender, MemberRepository memberRepository,
                                AlarmRepository alarmRepository, AlarmSendRepository alarmSendRepository) {
        this.alarmSender = alarmSender;
        this.memberRepository = memberRepository;
        this.alarmRepository = alarmRepository;
        this.alarmSendRepository = alarmSendRepository;
    }

    @GetMapping("/send-test")
    public String sendTest() {

        long start = System.currentTimeMillis();
        String message = "서원유통 알림 테스트!";

        Alarm alarm = new Alarm();
        alarm.setContent(message);
        alarm = alarmRepository.save(alarm);

        int page = 0;
        int size = 1000;
        long totalSent = 0;

        while (true) {
            Page<Member> memberPage = memberRepository.findAll(PageRequest.of(page, size));

            if (memberPage.isEmpty()) {
                break;
            }

            List<Member> members = memberPage.getContent();

            List<AlarmSend> alarmSends = new ArrayList<>();
            for (Member m : members) {
                AlarmSend alarmSend = new AlarmSend();
                alarmSend.setMember(m);
                alarmSend.setAlarm(alarm);
                alarmSend.setStatus(SendStatus.READY);
                alarmSends.add(alarmSend);
            }
            List<AlarmSend> savedSends = alarmSendRepository.saveAll(alarmSends);

            for (AlarmSend send : savedSends) {
                alarmSender.sendAlarm(send.getId(), send.getMember().getId(), message);
                totalSent++;
            }

            System.out.println("페이지 " + page + " 처리 완료 (" + members.size() + "건)");
            page++;
        }

        long end = System.currentTimeMillis();
        long elapsed = end - start;

        System.out.println("======================================");
        System.out.println("🔔 총 " + totalSent + "건 큐 Publish 완료");
        System.out.println("총 소요시간(ms): " + elapsed);
        System.out.println("초당 처리량(OPS): " + (totalSent * 1000.0 / elapsed));
        System.out.println("======================================");

        return "총 " + totalSent + "건 전송 완료! 시간(ms) = " + elapsed;
    }
}
