package com.mockServer.mocksever.Controller;


import com.mockServer.mocksever.Dto.MockPushRequest;
import com.mockServer.mocksever.Dto.MockPushResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock/nhn")
public class MockPushController {

    @PostMapping("/push")
    public MockPushResponse push(@RequestBody MockPushRequest request) {

        System.out.println("MOCK PUSH SERVER RECEIVED:");
        System.out.println("MemberId : " + request.memberId());
        System.out.println("Message  : " + request.message());

        // 실제 NHN Cloud API라면 success/fail 응답을 돌려줄 테지만 여기서는 랜덤으로 성공/실패를 나눠서 보내보자.
        boolean ok = Math.random() > 0.1; // 90% 성공 확률

        if (ok) {
            return new MockPushResponse(true, null);
        } else {
            return new MockPushResponse(false, "MOCK_FAIL");
        }
    }
}