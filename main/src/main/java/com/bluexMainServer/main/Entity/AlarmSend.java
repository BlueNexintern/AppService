package com.bluexMainServer.main.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "alarm_send")
@Getter
@Setter
public class AlarmSend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_id", nullable = false)
    private Alarm alarm;

    // 상태 (READY / SENT / FAILED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SendStatus status;

    @Column(length = 100)
    private String lastErrorCode;
}
