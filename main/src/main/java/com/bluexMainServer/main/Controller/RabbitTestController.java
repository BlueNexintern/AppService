package com.bluexMainServer.main.Controller;




import com.bluexMainServer.main.Entity.Member;
import com.bluexMainServer.main.Repository.MemberRepository;
import com.bluexMainServer.main.Service.AlarmSender;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitTestController {


    private final AlarmSender alarmSender;
    private final MemberRepository memberRepository;

    public RabbitTestController(AlarmSender alarmSender, MemberRepository memberRepository) {
        this.alarmSender = alarmSender;
        this.memberRepository = memberRepository;
    }
    @GetMapping("/send-test")
    public String sendTest() {

        long start = System.currentTimeMillis();
        String message = "서원유통 알림 테스트!";

        int page = 0;
        int size = 1000;  // 청크 크기
        long totalSent = 0;

        while (true) {
            Page<Member> memberPage = memberRepository.findAll(PageRequest.of(page, size));

            if (memberPage.isEmpty()) {
                break; // 더 이상 데이터 없음
            }

            List<Member> members = memberPage.getContent();

            for (Member m : members) {
                alarmSender.sendAlarm(m.getId(), message);
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
