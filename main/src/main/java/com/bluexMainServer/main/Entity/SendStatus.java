package com.bluexMainServer.main.Entity;

public enum SendStatus {
    READY,   // 아직 안 보냄 / 발송 대기
    SENT,    // 성공
    FAILED   // 실패
}