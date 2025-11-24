package com.mockServer.mocksever.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MockPushRequest {
    Long memberId;
    String message;

    public Long memberId() {
        return memberId;
    }

    public String message() {
        return message;
    }
}
